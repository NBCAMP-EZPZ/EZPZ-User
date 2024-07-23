package com.sparta.ezpzuser.domain.user.dto;

import com.sparta.ezpzuser.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserResponseDto {

    private final Long id;
    private final String username;
    private final String name;
    private final String email;
    private final String phoneNumber;

    private UserResponseDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.name = user.getName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
    }

    public static UserResponseDto of(User user) {
        return new UserResponseDto(user);
    }

}