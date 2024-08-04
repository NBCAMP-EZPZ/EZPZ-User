package com.sparta.ezpzuser.domain.cart.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.ezpzuser.domain.cart.entity.Cart;
import com.sparta.ezpzuser.domain.user.entity.User;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.sparta.ezpzuser.domain.cart.entity.QCart.cart;
import static com.sparta.ezpzuser.domain.item.entity.QItem.item;

@RequiredArgsConstructor
public class CartRepositoryCustomImpl implements CartRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Cart> findAllByIdListAndUser(List<Long> cartIdList, User user) {
        return queryFactory
                .selectFrom(cart)
                .join(cart.item, item).fetchJoin()
                .where(
                        cart.id.in(cartIdList),
                        cart.user.eq(user)
                )
                .fetch();
    }

}
