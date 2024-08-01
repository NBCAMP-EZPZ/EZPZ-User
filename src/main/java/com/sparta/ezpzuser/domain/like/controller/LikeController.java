package com.sparta.ezpzuser.domain.like.controller;

import com.sparta.ezpzuser.common.security.UserDetailsImpl;
import com.sparta.ezpzuser.domain.like.dto.LikeContentDto;
import com.sparta.ezpzuser.domain.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.sparta.ezpzuser.common.util.ControllerUtil.getResponseEntity;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    /**
     * 컨텐츠 좋아요 토글
     * @param content 컨테츠 타입, ID
     * @param userDetails 유저
     * @return 성공 메시지
     */
    @PostMapping("/v1/likes")
    public ResponseEntity<?> contentLike(
            @RequestBody LikeContentDto content,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        likeService.contentLike(content, userDetails.getUser());
        return getResponseEntity("좋아요 토글 성공");
    }

    /**
     * 타입별 좋아요한 컨텐츠 목록 조회
     * @param pageable 페이징
     * @param contentType 컨텐츠 타입
     * @param userDetails 유저
     * @return 컨텐츠 목록
     */
    @GetMapping("/v1/likes")
    public ResponseEntity<?> findAllLikesByContentType(
            Pageable pageable,
            @RequestParam String contentType,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Page<?> contentList = likeService.findAllLikesByContentType(pageable, contentType, userDetails.getUser());
        return getResponseEntity(contentList, "타입별 좋아요한 한 컨텐츠 목록 조회 성공");
    }
}
