package com.sparta.ezpzuser.domain.cart.entity;

import com.sparta.ezpzuser.common.entity.Timestamped;
import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.domain.item.entity.Item;
import com.sparta.ezpzuser.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.sparta.ezpzuser.common.exception.ErrorType.UNAUTHORIZED_CART_ACCESS;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;

    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Cart(User user, Item item, int quantity) {
        this.user = user;
        this.item = item;
        this.quantity = quantity;
    }

    /**
     * Cart 정적 팩토리 메서드
     *
     * @param user     장바구니를 생성하는 사용자
     * @param item     장바구니에 담을 굿즈
     * @param quantity 장바구니에 담을 굿즈의 개수
     * @return cart
     */
    public static Cart of(User user, Item item, int quantity) {
        return new Cart(user, item, quantity);
    }

    public static Cart createMockCart(Item item, int quantity) {
        return new Cart(null, item, quantity);
    }

    /**
     * Cart 수량 변경 메서드
     *
     * @param quantity 변경할 수량
     */
    public void updateCart(int quantity) {
        this.quantity = quantity;
    }

    /**
     * 본인의 카트가 맞는지 확인
     *
     * @param userId 이용자 ID
     */
    public void verifyUser(Long userId) {
        if (!this.user.getId().equals(userId)) {
            throw new CustomException(UNAUTHORIZED_CART_ACCESS);
        }
    }

}
