package com.sparta.ezpzuser.domain.item.repository;

import com.sparta.ezpzuser.domain.item.dto.ItemCondition;
import com.sparta.ezpzuser.domain.item.entity.Item;
import com.sparta.ezpzuser.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {

    Page<Item> findAllByItemCondition(Pageable pageable, ItemCondition cond);

    Page<Item> findAllLikedItemByUser(User user, Pageable pageable);

}
