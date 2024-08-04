package com.sparta.ezpzuser.domain.cart.repository;

import com.sparta.ezpzuser.domain.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("SELECT c FROM Cart c JOIN FETCH c.item WHERE c.user.id = :userId ORDER BY c.createdAt DESC")
    List<Cart> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    // item까지 함께 가져오도록 구현
    @Query("SELECT c FROM Cart c JOIN FETCH c.item WHERE c.id IN :cartIdList")
    List<Cart> findAllByIdList(@Param("cartIdList") List<Long> cartIdList);

}
