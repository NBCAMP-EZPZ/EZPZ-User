package com.sparta.ezpzuser.domain.item.repository;

import com.sparta.ezpzuser.domain.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {
}
