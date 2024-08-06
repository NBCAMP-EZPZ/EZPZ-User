package com.sparta.ezpzuser.domain.item.entity;

import com.sparta.ezpzuser.common.entity.Timestamped;
import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.domain.item.enums.ItemStatus;
import com.sparta.ezpzuser.domain.popup.entity.Popup;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.sparta.ezpzuser.common.exception.ErrorType.ITEM_ACCESS_FORBIDDEN;
import static com.sparta.ezpzuser.common.exception.ErrorType.STOCK_NOT_ENOUGH;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(name = "idx_item_popup", columnList = "popup_id"))
public class Item extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    private String name;

    private String description;

    private int price;

    private int stock;

    private String imageUrl;

    private String imageName;

    private int likeCount;

    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_id")
    private Popup popup;

    /**
     * 테스트용 생성자
     */
    private Item(Popup popup, int price, int stock) {
        this.popup = popup;
        this.price = price;
        this.stock = stock;
        this.itemStatus = ItemStatus.SALE;
    }

    public static Item createMockItem(Popup popup, int price, int stock) {
        return new Item(popup, price, stock);
    }

    public static Item createMockItem(int price, int stock) {
        return new Item(null, price, stock);
    }

    /**
     * 주문에 의한 재고 감소
     *
     * @param quantity 주문 수량
     */
    public void removeStock(int quantity) {
        checkStock(quantity);
        this.stock -= quantity;
        if (this.stock == 0) {
            this.itemStatus = ItemStatus.SOLD_OUT;
        }
    }

    /**
     * 재고 증가
     *
     * @param quantity 추가된 재고량
     */
    public void increaseStock(int quantity) {
        this.stock += quantity;
        // 품절 상태였다면 다시 판매 중 상태로 변경
        if (this.itemStatus.equals(ItemStatus.SOLD_OUT)) {
            this.itemStatus = ItemStatus.SALE;
        }
    }

    /**
     * 해당 수량만큼 재고가 있는지 확인
     *
     * @param quantity 확인할 수량
     */
    public void checkStock(int quantity) {
        // 남은 재고가 해당 수량보다 적은 경우
        if (this.stock < quantity) {
            throw new CustomException(STOCK_NOT_ENOUGH);
        }
    }

    /**
     * 상태 확인
     */
    public void verifyStatus() {
        if (this.itemStatus.equals(ItemStatus.SALE_END)) {
            throw new CustomException(ITEM_ACCESS_FORBIDDEN);
        }
    }

    /**
     * 좋아요 개수 (true: 증가 / false: 감소)
     *
     * @param b boolean
     */
    public void updateLikeCount(boolean b) {
        if (b) {
            this.likeCount++;
        } else if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

}
