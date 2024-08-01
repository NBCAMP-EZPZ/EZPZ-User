package com.sparta.ezpzuser.domain.user.entity;

import com.sparta.ezpzuser.common.entity.Timestamped;
import com.sparta.ezpzuser.domain.like.entity.Like;
import com.sparta.ezpzuser.domain.user.dto.SignupRequestDto;
import jakarta.persistence.*;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String username;

    private String password;

    private String name;

    private String email;

    private String phoneNumber;

    @OneToMany(mappedBy = "user")
    private List<Like> likeList;

    private User(SignupRequestDto dto, String encodedPassword) {
        username = dto.getUsername();
        password = encodedPassword;
        name = dto.getName();
        email = dto.getEmail();
        phoneNumber = dto.getPhoneNumber();
    }

    public static User of(SignupRequestDto dto, String encodedPassword) {
        return new User(dto, encodedPassword);
    }
}
