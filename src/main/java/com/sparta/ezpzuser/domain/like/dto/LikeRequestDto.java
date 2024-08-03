package com.sparta.ezpzuser.domain.like.dto;

import com.sparta.ezpzuser.domain.like.entity.LikeContentType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class LikeRequestDto {

    @NotNull(message = "LikeContentType은 필수 값입니다.")
    private LikeContentType contentType;

    @NotNull(message = "contentId는 필수 값입니다.")
    private Long contentId;

}
