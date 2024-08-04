package com.sparta.ezpzuser.domain.order.entity;

import com.sparta.ezpzuser.domain.item.entity.Item;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Orderline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderline_id")
    private Long id;

    private int quantity;

    private int orderPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    /**
     * 연관관계 편의 메서드
     */
    public void setOrderTo(Order order) {
        this.order = order;
    }

    /**
     * 생성자
     */
    private Orderline(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
        this.orderPrice = quantity * item.getPrice();
    }

    public static Orderline of(Item item, int quantity) {
        item.removeStock(quantity);
        return new Orderline(item, quantity);
    }

    /**
     * 주문 취소
     */
    public void cancel() {
        this.item.increaseStock(quantity);
    }

}
