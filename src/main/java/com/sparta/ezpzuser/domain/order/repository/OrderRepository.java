package com.sparta.ezpzuser.domain.order.repository;

import com.sparta.ezpzuser.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
