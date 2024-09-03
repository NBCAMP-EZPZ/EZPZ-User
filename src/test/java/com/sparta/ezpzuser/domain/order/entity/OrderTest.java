package com.sparta.ezpzuser.domain.order.entity;

import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.domain.cart.entity.Cart;
import com.sparta.ezpzuser.domain.item.entity.Item;
import com.sparta.ezpzuser.domain.order.enums.OrderStatus;
import com.sparta.ezpzuser.domain.user.entity.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.sparta.ezpzuser.common.exception.ErrorType.ORDER_CANCELLATION_NOT_ALLOWED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderTest {

    User user = User.createMockUser();

    int itemAPrice = 10_000;
    int itemAStock = 10;
    Item itemA = Item.createMockItem(itemAPrice, itemAStock);

    int itemBPrice = 1_000;
    int itemBStock = 10;
    Item itemB = Item.createMockItem(itemBPrice, itemBStock);

    int itemAOrderQuantity = 2;
    int itemBOrderQuantity = 2;
    List<Orderline> orderlineList = List.of(
            Orderline.buildOrderline(Cart.createMockCart(itemA, itemAOrderQuantity)),
            Orderline.buildOrderline(Cart.createMockCart(itemB, itemBOrderQuantity))
    );

    @Test
    void 주문_생성() {
        // when
        Order order = Order.of(user, orderlineList);

        // then
        assertThat(order.getUser()).isEqualTo(user);
        assertThat(order.getOrderlineList()).isEqualTo(orderlineList);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ORDER_COMPLETED);

        assertThat(itemA.getStock()).isEqualTo(itemAStock - itemAOrderQuantity);
        assertThat(itemB.getStock()).isEqualTo(itemBStock - itemBOrderQuantity);

        int expectedTotalPrice = itemAPrice * itemAOrderQuantity + itemBPrice * itemBOrderQuantity;
        assertThat(order.getTotalPrice()).isEqualTo(expectedTotalPrice);
    }

    @Test
    void 주문_취소_성공() {
        // given
        Order order = Order.of(user, orderlineList);

        // when
        order.cancel();

        // then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(itemA.getStock()).isEqualTo(itemAStock);
        assertThat(itemB.getStock()).isEqualTo(itemBStock);
    }

    @Test
    void 주문_취소_실패() {
        // given
        Order order = Order.of(user, orderlineList);
        order.startDelivery();

        // when
        CustomException exception = assertThrows(CustomException.class, order::cancel);

        // then
        assertThat(exception.getErrorType()).isEqualTo(ORDER_CANCELLATION_NOT_ALLOWED);
    }

}