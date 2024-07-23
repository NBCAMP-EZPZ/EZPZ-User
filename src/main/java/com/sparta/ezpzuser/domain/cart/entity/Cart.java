package com.sparta.ezpzuser.domain.cart.entity;

import com.sparta.ezpzuser.common.entity.Timestamped;
import com.sparta.ezpzuser.domain.item.entity.Item;
import com.sparta.ezpzuser.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;

    @Column
    private Long quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    /**
     * Cart 생성자
     *
     * @param quantity 장바구니에 담을 굿즈의 개수
     * @param user     장바구니를 생성하는 사용자
     * @param item     장바구니에 담을 굿즈
     */
    private Cart(Long quantity, User user, Item item) {
        this.quantity = quantity;
        this.user = user;
        this.item = item;
    }

    /**
     * Cart 정적 팩토리 메서드
     *
     * @param quantity 장바구니에 담을 굿즈의 개수
     * @param user     장바구니를 생성하는 사용자
     * @param item     장바구니에 담을 굿즈
     * @return cart
     */
    public static Cart of(Long quantity, User user, Item item) {
        return new Cart(quantity, user, item);
    }

    /**
     * Cart 수량 변경 메서드
     *
     * @param quantity 변경할 수량
     */
    public void updateCart(Long quantity) {
        this.quantity = quantity;
    }
}
