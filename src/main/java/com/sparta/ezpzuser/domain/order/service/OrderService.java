package com.sparta.ezpzuser.domain.order.service;

import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.common.exception.ErrorType;
import com.sparta.ezpzuser.domain.cart.entity.Cart;
import com.sparta.ezpzuser.domain.cart.repository.CartRepository;
import com.sparta.ezpzuser.domain.cart.service.CartService;
import com.sparta.ezpzuser.domain.order.dto.OrderCreateResponseDto;
import com.sparta.ezpzuser.domain.order.dto.OrderFindAllResponseDto;
import com.sparta.ezpzuser.domain.order.dto.OrderRequestDto;
import com.sparta.ezpzuser.domain.order.entity.Order;
import com.sparta.ezpzuser.domain.order.enums.OrderStatus;
import com.sparta.ezpzuser.domain.order.repository.OrderRepository;
import com.sparta.ezpzuser.domain.orderline.dto.OrderlineResponseDto;
import com.sparta.ezpzuser.domain.orderline.entity.Orderline;
import com.sparta.ezpzuser.domain.user.entity.User;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class OrderService {

    private final CartService cartService;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;


    /**
     * 주문하기
     *
     * @param requestDto 주문할 장바구니 정보 리스트
     * @param user       주문 요청한 사용자
     * @return 완료된 주문 정보
     */
    @Transactional
    public OrderCreateResponseDto createOrder(OrderRequestDto requestDto, User user) {

        // 요청받은 장바구니 정보 조회
        List<Cart> cartList = findCartsByIds(requestDto.getCartIdRequestList(), user.getId());

        validateStockForItems(cartList);

        Order order = Order.of(user);
        List<OrderlineResponseDto> orderlineResponseDtoList = new ArrayList<>();
        for (Cart cart : cartList) {
            Orderline orderline = Orderline.of(
                    cart.getQuantity(),
                    order,
                    cart.getItem()
            );

            order.addOrderline(orderline);

            orderlineResponseDtoList.add(OrderlineResponseDto.of(orderline));
        }

        order.setOrderStatus(OrderStatus.ORDER_COMPLETED);
        orderRepository.save(order);

        // 사용된 장바구니 삭제
        cartList.stream().forEach(cart -> cartRepository.delete(cart));
        return OrderCreateResponseDto.of(order, orderlineResponseDtoList);
    }

    /**
     * 주문 목록 조회
     *
     * @param pageable 요청한 페이지 정보
     * @param user     요청한 사용자
     * @return 주문 목록 Page
     */
    public Page<OrderFindAllResponseDto> findOrdersAll(Pageable pageable, User user) {
        Page<Order> orderPages = orderRepository.findAllByUserId(user.getId(), pageable);
        return orderPages.map(OrderFindAllResponseDto::of);
    }

    /* UTIL */

    /**
     * 입력받은 Cart Id 리스트로부터 Cart 리스트를 조회
     *
     * @param cartIdRequestList 주문할 장바구니 id 리스트
     * @return 주문할 장바구니 리스트
     */
    private List<Cart> findCartsByIds(List<OrderRequestDto.CartIdRequest> cartIdRequestList,
            Long userId) {
        List<Long> cartIds = cartIdRequestList.stream()
                .map(OrderRequestDto.CartIdRequest::getCartId).toList();
        List<Cart> carts = cartRepository.findAllByIdWithItems(cartIds).stream()
                .filter(cart -> cart.getUser().getId().equals(userId)).toList();
        if (carts.size() != cartIds.size()) {
            throw new CustomException(ErrorType.CART_NOT_FOUND);
        }
        return carts;
    }

    /**
     * validateStock를 활용하여 장바구니 리스트 전체가 유효한 값(수량)인지 확인함
     *
     * @param carts 장바구니 리스트
     */
    private void validateStockForItems(List<Cart> carts) {
        for (Cart cart : carts) {
            cartService.validateStock(cart.getItem(), cart.getQuantity());
        }
    }
}
