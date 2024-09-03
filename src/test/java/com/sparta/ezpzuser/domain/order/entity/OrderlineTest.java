package com.sparta.ezpzuser.domain.order.entity;

import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.domain.cart.entity.Cart;
import com.sparta.ezpzuser.domain.item.entity.Item;
import com.sparta.ezpzuser.domain.item.enums.ItemStatus;
import org.junit.jupiter.api.Test;

import static com.sparta.ezpzuser.common.exception.ErrorType.STOCK_NOT_ENOUGH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderlineTest {

    Item item;
    Cart cart;

    int itemPrice = 10000;
    int itemStock;
    int orderQuantity;

    @Test
    void 주문라인_생성_성공() {
        // given
        itemStock = 10;
        orderQuantity = 3;

        item = Item.createMockItem(itemPrice, itemStock);
        cart = Cart.createMockCart(item, orderQuantity);

        // when
        Orderline orderline = Orderline.buildOrderline(cart);

        // then
        assertThat(item.getStock()).isEqualTo(itemStock - orderQuantity);
        assertThat(item.getItemStatus()).isEqualTo(ItemStatus.SALE);
        assertThat(orderline.getQuantity()).isEqualTo(orderQuantity);
        assertThat(orderline.getOrderPrice()).isEqualTo(itemPrice * orderQuantity);
    }

    @Test
    void 주문라인_생성_성공_품절() {
        // given
        itemStock = 10;
        orderQuantity = itemStock;

        item = Item.createMockItem(itemPrice, itemStock);
        cart = Cart.createMockCart(item, orderQuantity);

        // when
        Orderline orderline = Orderline.buildOrderline(cart);

        // then
        assertThat(item.getStock()).isZero();
        assertThat(item.getItemStatus()).isEqualTo(ItemStatus.SOLD_OUT);
        assertThat(orderline.getQuantity()).isEqualTo(orderQuantity);
        assertThat(orderline.getOrderPrice()).isEqualTo(itemPrice * orderQuantity);
    }

    @Test
    void 주문라인_생성_실패_재고부족() {
        // given
        itemStock = 10;
        orderQuantity = itemStock + 1;

        item = Item.createMockItem(itemPrice, itemStock);
        cart = Cart.createMockCart(item, orderQuantity);

        // when
        CustomException exception = assertThrows(CustomException.class, () ->
                Orderline.buildOrderline(cart)
        );

        // then
        assertThat(exception.getErrorType()).isEqualTo(STOCK_NOT_ENOUGH);
    }

    @Test
    void 주문라인_주문_취소() {
        // given
        itemStock = 10;
        orderQuantity = itemStock;

        item = Item.createMockItem(itemPrice, itemStock);
        cart = Cart.createMockCart(item, orderQuantity);

        Orderline orderline = Orderline.buildOrderline(cart);

        // when
        orderline.cancel();

        // then
        assertThat(item.getStock()).isEqualTo(itemStock);
        assertThat(item.getItemStatus()).isEqualTo(ItemStatus.SALE);
    }

}