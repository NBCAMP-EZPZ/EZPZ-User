package com.sparta.ezpzuser.domain.item.entity;

import com.sparta.ezpzuser.common.entity.Timestamped;
import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.domain.item.enums.ItemStatus;
import com.sparta.ezpzuser.domain.popup.entity.Popup;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.sparta.ezpzuser.common.exception.ErrorType.INSUFFICIENT_STOCK;
import static com.sparta.ezpzuser.common.exception.ErrorType.ITEM_ACCESS_FORBIDDEN;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "item", indexes = @Index(name = "idx_item_popup", columnList = "popup_id"))
public class Item extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_id", nullable = false)
    private Popup popup;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int stock;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "image_name", nullable = false)
    private String imageName;

    @Column(name = "like_count")
    private int likeCount;

    @Column(name = "item_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus;

    // 테스트용 생성자
    private Item(Popup popup, int price, int stock) {
        this.popup = popup;
        this.price = price;
        this.stock = stock;
        this.itemStatus = ItemStatus.SALE;
    }

    public static Item createMockItem(Popup popup, int price, int stock) {
        return new Item(popup, price, stock);
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

    /**
     * 재고 확인
     *
     * @param quantity 요청한 수량
     */
    public void checkStock(int quantity) {
        if (this.stock < quantity) {
            throw new CustomException(INSUFFICIENT_STOCK);
        }
    }

    /**
     * 재고량 변경
     *
     * @param stock 변경할 수량
     */
    public void updateStock(int stock) {
        this.stock = stock;
    }

    /**
     * 상품 상태 변경
     *
     * @param itemStatus 변경할 상태
     */
    public void updateStatus(ItemStatus itemStatus) {
        this.itemStatus = itemStatus;
    }
}
