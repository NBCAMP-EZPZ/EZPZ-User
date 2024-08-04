package com.sparta.ezpzuser.domain.order.service;

import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.common.lock.DistributedLock;
import com.sparta.ezpzuser.common.util.PageUtil;
import com.sparta.ezpzuser.domain.cart.entity.Cart;
import com.sparta.ezpzuser.domain.cart.repository.CartRepository;
import com.sparta.ezpzuser.domain.order.dto.OrderPageResponseDto;
import com.sparta.ezpzuser.domain.order.dto.OrderRequestDto;
import com.sparta.ezpzuser.domain.order.dto.OrderResponseDto;
import com.sparta.ezpzuser.domain.order.entity.Order;
import com.sparta.ezpzuser.domain.order.entity.Orderline;
import com.sparta.ezpzuser.domain.order.repository.OrderRepository;
import com.sparta.ezpzuser.domain.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.sparta.ezpzuser.common.exception.ErrorType.ORDER_NOT_FOUND;

@Service
@AllArgsConstructor
public class OrderService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;

    /**
     * 주문하기
     *
     * @param dto  주문할 장바구니 정보 리스트
     * @param user 주문 요청한 사용자
     * @return 완료된 주문 정보
     */
    @DistributedLock(key = "createOrder")
    public OrderResponseDto createOrder(OrderRequestDto dto, User user) {
        // 장바구니 목록 가져오기
        List<Cart> cartList = cartRepository.findAllWithItemByIdListAndUser(dto.getCartIdList(), user);
        // 장바구니 목록을 통해 주문상품 목록 생성
        List<Orderline> orderlineList = new ArrayList<>();
        for (Cart cart : cartList) {
            Orderline orderline = Orderline.of(cart.getItem(), cart.getQuantity());
            orderlineList.add(orderline);
        }
        // 주문 생성
        Order order = orderRepository.save(Order.of(user, orderlineList));
        cartRepository.deleteAll(cartList); // 주문 완료된 장바구니 삭제
        return OrderResponseDto.of(order, orderlineList);
    }

    /**
     * 주문 목록 조회
     *
     * @param pageable 요청한 페이지 정보
     * @param user     요청한 사용자
     * @return 주문 목록 Page
     */
    @Transactional(readOnly = true)
    public Page<OrderPageResponseDto> findAllOrders(Pageable pageable, User user) {
        Page<Order> page = orderRepository.findAllByUser(user, pageable);
        PageUtil.validatePageableWithPage(pageable, page);
        return page.map(OrderPageResponseDto::of);
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
        return OrderResponseDto.of(order, order.getOrderlineList());
    }

    /**
     * 주문 취소
     *
     * @param orderId 취소할 주문 id
     * @param user    요청 이용자
     */
    @DistributedLock(key = "cancelOrder")
    public void cancelOrder(Long orderId, User user) {
        Order order = getOrder(orderId, user);
        order.cancel();
    }

    /* UTIL */

    private Order getOrder(Long orderId, User user) {
        return orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new CustomException(ORDER_NOT_FOUND));
    }

}
