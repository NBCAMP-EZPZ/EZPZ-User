package com.sparta.ezpzuser.domain.item.dto;

import com.sparta.ezpzuser.domain.item.entity.Item;
import lombok.Getter;

@Getter
public class ItemResponseDto {

    private final Long itemId;
    private final String name;
    private final String description;
    private final int price;
    private final int stock;
    private final int likeCount;
    private final String itemStatus;
    private final String imageUrl;
    private final String imageName;

    private ItemResponseDto(Item item) {
        this.itemId = item.getId();
        this.name = item.getName();
        this.description = item.getDescription();
        this.price = item.getPrice();
        this.stock = item.getStock();
        this.likeCount = item.getLikeCount();
        this.itemStatus = item.getItemStatus().toString();
        this.imageUrl = item.getImageUrl();
        this.imageName = item.getImageName();
    }

    public static ItemResponseDto of(Item item) {
        return new ItemResponseDto(item);
    }
}
