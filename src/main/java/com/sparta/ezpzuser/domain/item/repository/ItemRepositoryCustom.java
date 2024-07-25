package com.sparta.ezpzuser.domain.item.repository;

import com.sparta.ezpzuser.domain.item.dto.ItemCondition;
import com.sparta.ezpzuser.domain.item.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {
    Page<Item> findAllItemsByHostAndPopupAndStatus(Pageable pageable, ItemCondition cond);
}
