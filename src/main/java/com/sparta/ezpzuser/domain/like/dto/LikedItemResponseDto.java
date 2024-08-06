package com.sparta.ezpzuser.domain.like.dto;

import com.sparta.ezpzuser.domain.item.entity.Item;
import lombok.Getter;

@Getter
public class LikedItemResponseDto {

    private final Long ItemId;
    private final String name;
    private final int price;
    private final int likeCount;
    private final String image;

    public LikedItemResponseDto(Item item) {
        this.ItemId = item.getId();
        this.name = item.getName();
        this.price = item.getPrice();
        this.likeCount = item.getLikeCount();
        this.image = item.getImageUrl();
    }

    public static LikedItemResponseDto of(Item item) {
        return new LikedItemResponseDto(item);
    }

}
