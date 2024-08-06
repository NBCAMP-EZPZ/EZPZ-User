package com.sparta.ezpzuser.domain.like.dto;

import com.sparta.ezpzuser.domain.like.entity.LikeContentType;
import lombok.Getter;

@Getter
public class LikeResponseDto {

    private final String result;
    private final LikeContentType contentType;
    private final Long contentId;

    private LikeResponseDto(boolean toggleResult, LikeContentType contentType, Long contentId) {
        this.result = toggleResult ? "Like" : "Unlike";
        this.contentType = contentType;
        this.contentId = contentId;
    }

    public static LikeResponseDto of(boolean toggleResult, LikeContentType contentType, Long contentId) {
        return new LikeResponseDto(toggleResult, contentType, contentId);
    }

}
