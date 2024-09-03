package com.sparta.ezpzuser.domain.item.entity;

import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.domain.item.enums.ItemStatus;
import org.junit.jupiter.api.Test;

import static com.sparta.ezpzuser.common.exception.ErrorType.NOT_LIKED_ITEM;
import static com.sparta.ezpzuser.common.exception.ErrorType.STOCK_NOT_ENOUGH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ItemTest {

    @Test
    void 재고_감소() {
        // given
        int itemStock = 10;
        Item item = Item.createMockItem(itemStock);

        // when
        int quantity = 2;
        item.removeStock(quantity);

        // then
        assertThat(item.getStock()).isEqualTo(itemStock - quantity);
        assertThat(item.getItemStatus()).isEqualTo(ItemStatus.SALE);
    }

    @Test
    void 재고_감소_품절() {
        // given
        int itemStock = 10;
        Item item = Item.createMockItem(itemStock);

        // when
        item.removeStock(itemStock);

        // then
        assertThat(item.getStock()).isZero();
        assertThat(item.getItemStatus()).isEqualTo(ItemStatus.SOLD_OUT);
    }

    @Test
    void 재고_증가_품절_상품() {
        // given
        Item item = Item.createMockSoldOutItem();

        // when
        int quantity = 10;
        item.increaseStock(quantity);

        // then
        assertThat(item.getItemStatus()).isEqualTo(ItemStatus.SALE);
        assertThat(item.getStock()).isEqualTo(quantity);
    }

    @Test
    void 재고_확인() {
        // given
        int itemStock = 10;
        Item item = Item.createMockItem(itemStock);

        // when
        CustomException exception = assertThrows(CustomException.class, () ->
                item.checkStock(itemStock + 1)
        );

        // then
        assertThat(exception.getErrorType()).isEqualTo(STOCK_NOT_ENOUGH);
    }

    @Test
    void 좋아요_증가() {
        // given
        int likeCount = 10;
        Item item = Item.createMockLikedItem(likeCount);

        // when
        item.updateLikeCount(true);

        // then
        assertThat(item.getLikeCount()).isEqualTo(likeCount + 1);
    }

    @Test
    void 좋아요_감소_성공() {
        // given
        int likeCount = 10;
        Item item = Item.createMockLikedItem(likeCount);

        // when
        item.updateLikeCount(false);

        // then
        assertThat(item.getLikeCount()).isEqualTo(likeCount - 1);
    }

    @Test
    void 좋아요_감소_실패() {
        // given
        Item item = Item.createMockLikedItem(0);

        // when
        CustomException exception = assertThrows(CustomException.class, () ->
                item.updateLikeCount(false)
        );

        // then
        assertThat(exception.getErrorType()).isEqualTo(NOT_LIKED_ITEM);
    }

}