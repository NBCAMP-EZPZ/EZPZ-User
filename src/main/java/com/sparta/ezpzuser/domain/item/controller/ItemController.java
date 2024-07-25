package com.sparta.ezpzuser.domain.item.controller;

import com.sparta.ezpzuser.domain.item.dto.ItemCondition;
import com.sparta.ezpzuser.domain.item.dto.ItemResponseDto;
import com.sparta.ezpzuser.domain.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.sparta.ezpzuser.common.util.ControllerUtil.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    /**
     * 호스트, 팝업 및 상태별 상품 목록 조회
     * @param pageable 페이징
     * @param hostId 호스트 ID
     * @param popupId 팝업 ID
     * @param itemStatus 상품 상태
     * @return 상품 목록
     */
    @GetMapping("/v1/items")
    public ResponseEntity<?> findAllItemsByHostAndPopupAndStatus(
            Pageable pageable,
            @RequestParam(defaultValue = "all") String hostId,
            @RequestParam(defaultValue = "all") String popupId,
            @RequestParam(defaultValue = "all") String itemStatus) {
        ItemCondition cond = ItemCondition.of(hostId, popupId, itemStatus);

        Page<?> itemList = itemService.findAllItemsByHostAndPopupAndStatus(pageable, cond);
        return getResponseEntity(itemList, "굿즈 목록 조회 성공");
    }

    /**
     * 상품 상세 조회
     * @param itemId 상품 ID
     * @return 상품 상세
     */
    @GetMapping("/v1/items/{itemId}")
    public ResponseEntity<?> findItem(
            @PathVariable Long itemId) {
        ItemResponseDto responseDto = itemService.findItem(itemId);
        return getResponseEntity(responseDto, "굿즈 상세보기 조회 성공");
    }
}
