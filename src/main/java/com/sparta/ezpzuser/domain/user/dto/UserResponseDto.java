package com.sparta.ezpzuser.domain.user.dto;

import com.sparta.ezpzuser.domain.user.entity.User;
import lombok.Data;

@Data
public class UserResponseDto {

    private Long id;
    private String username;
    private String name;
    private String email;
    private String phoneNumber;

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