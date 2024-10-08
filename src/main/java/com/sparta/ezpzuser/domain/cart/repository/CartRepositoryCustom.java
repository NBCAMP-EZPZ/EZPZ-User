package com.sparta.ezpzuser.domain.cart.repository;

import com.sparta.ezpzuser.domain.cart.entity.Cart;
import com.sparta.ezpzuser.domain.user.entity.User;

import java.util.List;

public interface CartRepositoryCustom {

    List<Cart> findAllWithItemByUser(User user);

    List<Cart> findAllWithItemByIdListAndUser(List<Long> cartIdList, User user);

}
