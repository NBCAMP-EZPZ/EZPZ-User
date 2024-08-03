package com.sparta.ezpzuser.domain.like.repository;

import com.sparta.ezpzuser.domain.like.entity.Like;
import com.sparta.ezpzuser.domain.like.entity.LikeContentType;
import com.sparta.ezpzuser.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByContentTypeAndContentIdAndUser(LikeContentType contentType, Long contentId, User user);

}
