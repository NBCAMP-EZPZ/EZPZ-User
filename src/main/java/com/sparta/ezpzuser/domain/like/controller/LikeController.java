package com.sparta.ezpzuser.domain.like.controller;

import com.sparta.ezpzuser.common.security.UserDetailsImpl;
import com.sparta.ezpzuser.domain.like.dto.LikeRequestDto;
import com.sparta.ezpzuser.domain.like.dto.LikeResponseDto;
import com.sparta.ezpzuser.domain.like.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.sparta.ezpzuser.common.util.ControllerUtil.getResponseEntity;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LikeController {

    private final LikeService likeService;

    /**
     * 컨텐츠 좋아요 토글
     *
     * @param likeRequestDto 컨텐츠 ID, 컨텐츠 타입
     * @param userDetails    유저
     * @return 성공 메시지
     */
    @PostMapping("/v1/likes")
    public ResponseEntity<?> contentLike(
            @Valid @RequestBody LikeRequestDto likeRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        LikeResponseDto response = likeService.toggleLike(likeRequestDto, userDetails.getUser());
        return getResponseEntity(response, "좋아요 토글 성공");
    }

    /**
     * 타입별 좋아요한 컨텐츠 목록 조회
     *
     * @param pageable    페이징
     * @param type        컨텐츠 타입
     * @param userDetails 유저
     * @return 컨텐츠 목록
     */
    @GetMapping("/v1/likes")
    public ResponseEntity<?> findAllLikesByContentType(
            Pageable pageable,
            @RequestParam String type,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Page<?> response = likeService.findAllLikedContentByType(pageable, type, userDetails.getUser());
        return getResponseEntity(response, "타입별 좋아요한 컨텐츠 목록 조회 성공");
    }

}
