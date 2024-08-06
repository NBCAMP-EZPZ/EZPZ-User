package com.sparta.ezpzuser.domain.cart.repository;

import com.sparta.ezpzuser.domain.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long>, CartRepositoryCustom {
}
