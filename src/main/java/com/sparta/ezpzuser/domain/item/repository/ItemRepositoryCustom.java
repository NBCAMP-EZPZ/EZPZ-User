package com.sparta.ezpzuser.domain.item.repository;

import com.sparta.ezpzuser.domain.item.dto.ItemCondition;
import com.sparta.ezpzuser.domain.item.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemRepositoryCustom {

    Page<Item> findAllByItemCondition(Pageable pageable, ItemCondition cond);

    Page<Item> findItemByIdList(Pageable pageable, List<Long> itemIdList);

}
