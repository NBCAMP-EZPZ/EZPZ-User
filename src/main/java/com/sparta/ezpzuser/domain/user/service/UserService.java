package com.sparta.ezpzuser.domain.user.service;

import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.common.exception.ErrorType;
import com.sparta.ezpzuser.domain.user.dto.SignupRequestDto;
import com.sparta.ezpzuser.domain.user.entity.User;
import com.sparta.ezpzuser.domain.user.repository.RefreshTokenRepository;
import com.sparta.ezpzuser.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 이용자 회원가입
     *
     * @param dto 회원가입 시 필요한 정보
     * @return 회원가입된 이용자 엔티티
     */
    @Transactional
    public User signup(SignupRequestDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new CustomException(ErrorType.DUPLICATED_USERNAME);
        }
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        return userRepository.save(User.of(dto, encodedPassword));
    }

    /**
     * 이용자 로그아웃
     *
     * @param user 로그아웃 요청한 이용자
     */
    @Transactional
    public void logout(User user) {
        refreshTokenRepository.deleteByRefreshToken(user.getUsername());
    }

}
