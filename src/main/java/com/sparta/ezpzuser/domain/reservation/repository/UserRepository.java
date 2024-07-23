package com.sparta.ezpzuser.domain.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.ezpzuser.domain.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
