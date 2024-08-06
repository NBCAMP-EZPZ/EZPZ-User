package com.sparta.ezpzuser.domain.like.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.stream.Stream;

public enum LikeContentType {

    POPUP,
    ITEM;

    @JsonCreator
    public static LikeContentType parseJsonToEnum(String inputValue) {
        return Stream.of(LikeContentType.values())
                .filter(contentType -> contentType.toString().equals(inputValue.toUpperCase()))
                .findFirst()
                .orElse(null);
    }

}
