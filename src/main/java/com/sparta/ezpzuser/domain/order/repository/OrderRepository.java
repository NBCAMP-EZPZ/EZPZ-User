package com.sparta.ezpzuser.domain.order.repository;

import com.sparta.ezpzuser.domain.order.entity.Order;
import com.sparta.ezpzuser.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findAllByUser(User user, Pageable pageable);

    Optional<Order> findByIdAndUser(Long orderId, User user);

}
