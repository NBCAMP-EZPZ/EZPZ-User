package com.sparta.ezpzuser.domain.cart.repository;

import com.sparta.ezpzuser.domain.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long>, CartRepositoryCustom {

    @Query("SELECT c FROM Cart c JOIN FETCH c.item WHERE c.user.id = :userId ORDER BY c.createdAt DESC")
    List<Cart> findAllByUserIdOrderByCreatedAtDesc(Long userId);


}
