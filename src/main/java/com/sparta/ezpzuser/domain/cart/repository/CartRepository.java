package com.sparta.ezpzuser.domain.cart.repository;

import com.sparta.ezpzuser.domain.cart.entity.Cart;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findAllByUserIdOrderByCreatedAtDesc(Long userId);

}
