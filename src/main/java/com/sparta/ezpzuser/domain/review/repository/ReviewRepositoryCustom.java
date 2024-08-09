package com.sparta.ezpzuser.domain.review.repository;

import com.sparta.ezpzuser.domain.user.entity.User;

public interface ReviewRepositoryCustom {

    boolean existsByUser(User user);

}
