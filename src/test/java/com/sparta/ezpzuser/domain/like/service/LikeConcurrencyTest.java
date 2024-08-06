package com.sparta.ezpzuser.domain.like.service;

import com.sparta.ezpzuser.domain.like.dto.LikeRequestDto;
import com.sparta.ezpzuser.domain.like.entity.LikeContentType;
import com.sparta.ezpzuser.domain.like.repository.LikeRepository;
import com.sparta.ezpzuser.domain.popup.entity.Popup;
import com.sparta.ezpzuser.domain.popup.repository.popup.PopupRepository;
import com.sparta.ezpzuser.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@SpringBootTest
public class LikeConcurrencyTest {

    @Autowired
    LikeService likeService;

    @Autowired
    PopupRepository popupRepository;

    @MockBean
    LikeRepository likeRepository;

    Popup popup;
    User user;

    int threadCount = 10000;

    @BeforeEach
    void setUp() {
        popup = Popup.createMockPopup();
        popupRepository.save(popup);
        user = User.createMockUser();
    }

    @Test
    @DisplayName("성공 - 좋아요 동시성 테스트")
    void toggleLike() {
        // given
        LikeContentType contentType = LikeContentType.POPUP;
        Long contentId = popup.getId();
        given(likeRepository.findByContentTypeAndContentIdAndUser(any(LikeContentType.class), anyLong(), any(User.class)))
                .willReturn(Optional.empty());

        // when
        LikeRequestDto requestDto = LikeRequestDto.of(contentType, contentId);
        IntStream.range(0, threadCount).parallel().forEach(i ->
                likeService.toggleLike(requestDto, user)
        );

        // then
        int popupLikeCount = popupRepository.findById(contentId).orElseThrow().getLikeCount();
        assertThat(popupLikeCount).isEqualTo(threadCount);
    }

}
