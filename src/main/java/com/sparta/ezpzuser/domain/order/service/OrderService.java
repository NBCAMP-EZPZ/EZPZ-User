package com.sparta.ezpzuser.domain.order.service;

import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.common.lock.DistributedLock;
import com.sparta.ezpzuser.common.util.PageUtil;
import com.sparta.ezpzuser.domain.cart.entity.Cart;
import com.sparta.ezpzuser.domain.cart.repository.CartRepository;
import com.sparta.ezpzuser.domain.item.entity.Item;
import com.sparta.ezpzuser.domain.item.enums.ItemStatus;
import com.sparta.ezpzuser.domain.order.dto.OrderFindAllResponseDto;
import com.sparta.ezpzuser.domain.order.dto.OrderRequestDto;
import com.sparta.ezpzuser.domain.order.dto.OrderResponseDto;
import com.sparta.ezpzuser.domain.order.entity.Order;
import com.sparta.ezpzuser.domain.order.enums.OrderStatus;
import com.sparta.ezpzuser.domain.order.repository.OrderRepository;
import com.sparta.ezpzuser.domain.orderline.dto.OrderlineResponseDto;
import com.sparta.ezpzuser.domain.orderline.entity.Orderline;
import com.sparta.ezpzuser.domain.orderline.repository.OrderlineRepository;
import com.sparta.ezpzuser.domain.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.sparta.ezpzuser.common.exception.ErrorType.*;

@Service
@AllArgsConstructor
public class OrderService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderlineRepository orderlineRepository;

    /**
     * 주문하기
     *
     * @param dto  주문할 장바구니 정보 리스트
     * @param user 주문 요청한 사용자
     * @return 완료된 주문 정보
     */
    @DistributedLock(key = "'createOrder-userId-'.concat(#user.id)")
    public OrderResponseDto createOrder(OrderRequestDto dto, User user) {
        List<Cart> cartList = getCartList(dto.getCartIdList(), user.getId());
        validateItemStock(cartList);

        Order order = Order.of(user);
        List<OrderlineResponseDto> orderlineResponseDtoList = new ArrayList<>();
        for (Cart cart : cartList) {
            Item item = cart.getItem();
            int newStock = item.getStock() - cart.getQuantity();
            item.updateStock(newStock);
            if (newStock == 0) {
                item.updateStatus(ItemStatus.SOLD_OUT);
            }

            Orderline orderline = Orderline.of(cart.getQuantity(), order, item);
            order.addOrderline(orderline);
            orderlineResponseDtoList.add(OrderlineResponseDto.of(orderline));
        }

        orderRepository.save(order);
        cartRepository.deleteAll(cartList); // 사용된 장바구니 삭제
        return OrderResponseDto.of(order, orderlineResponseDtoList);
    }

    /**
     * 주문 목록 조회
     *
     * @param pageable 요청한 페이지 정보
     * @param user     요청한 사용자
     * @return 주문 목록 Page
     */
    @Transactional(readOnly = true)
    public Page<OrderFindAllResponseDto> findOrdersAll(Pageable pageable, User user) {
        Page<Order> page = orderRepository.findAllByUserId(user.getId(), pageable);
        PageUtil.validatePageableWithPage(pageable, page);
        return page.map(OrderFindAllResponseDto::of);
    }

    /**
     * 주문 상세 조회
     *
     * @param orderId 조회할 주문 id
     * @param user    요청한 사용자
     * @return 주문 상세 정보
     */
    @Transactional(readOnly = true)
    public OrderResponseDto findOrder(Long orderId, User user) {
        Order order = getOrder(orderId, user);
        List<Orderline> orderlineList = orderlineRepository.findAllByOrderId(orderId);
        List<OrderlineResponseDto> orderlineResponseDtoList = orderlineList.stream()
                .map(OrderlineResponseDto::of)
                .toList();

        return OrderResponseDto.of(order, orderlineResponseDtoList);
    }

    /**
     * 주문 취소
     *
     * @param orderId 취소할 주문 id
     * @param user    요청한 사용
     */
    @DistributedLock(key = "'deleteOrder-orderId-'.concat(#orderId)")
    public void deleteOrder(Long orderId, User user) {
        Order order = getOrder(orderId, user);
        if (order.getOrderStatus() != OrderStatus.ORDER_COMPLETED) {
            throw new CustomException(ORDER_CANCELLATION_NOT_ALLOWED);
        }
        List<Orderline> orderlineList = orderlineRepository.findAllByOrderId(orderId);
        for (Orderline orderline : orderlineList) {
            Item item = orderline.getItem();

            if (item.getItemStatus().equals(ItemStatus.SOLD_OUT)) {
                item.updateStock(orderline.getQuantity());
                item.updateStatus(ItemStatus.SALE);
            } else {
                item.updateStock(item.getStock() + orderline.getQuantity());
            }
        }

        order.updateStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    /* UTIL */

    private Order getOrder(Long orderId, User user) {
        return orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new CustomException(ORDER_NOT_FOUND));
    }

    /**
     * 입력받은 Cart Id 리스트로부터 Cart 리스트를 조회
     *
     * @param cartIdList 주문할 장바구니 Id 리스트
     * @return 주문할 장바구니 리스트
     */
    private List<Cart> getCartList(List<Long> cartIdList, Long userId) {
        List<Cart> cartList = cartRepository.findAllByIdList(cartIdList).stream()
                .filter(cart -> cart.getUser().getId().equals(userId))
                .toList();
        if (cartList.size() != cartIdList.size()) {
            throw new CustomException(CART_NOT_FOUND);
        }
        return cartList;
    }

    /**
     * 장바구니 리스트 전체가 유효한 값(수량)인지 확인
     *
     * @param carts 장바구니 리스트
     */
    private void validateItemStock(List<Cart> carts) {
        for (Cart cart : carts) {
            cart.getItem().checkStock(cart.getQuantity());
        }
    }

}
