package com.sparta.ezpzuser.domain.like.dto;

import com.sparta.ezpzuser.domain.like.entity.LikeContentType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class LikeRequestDto {

    @NotNull(message = "LikeContentType은 필수 값입니다.")
    private final LikeContentType contentType;

    @NotNull(message = "contentId는 필수 값입니다.")
    private final Long contentId;

    private LikeRequestDto(LikeContentType contentType, Long contentId) {
        this.contentType = contentType;
        this.contentId = contentId;
    }

    public static LikeRequestDto of(LikeContentType contentType, Long contentId) {
        return new LikeRequestDto(contentType, contentId);
    }

}
