package com.sparta.ezpzuser.domain.item.service;

import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.common.exception.ErrorType;
import com.sparta.ezpzuser.common.util.PageUtil;
import com.sparta.ezpzuser.domain.item.dto.ItemCondition;
import com.sparta.ezpzuser.domain.item.dto.ItemPageResponseDto;
import com.sparta.ezpzuser.domain.item.dto.ItemResponseDto;
import com.sparta.ezpzuser.domain.item.entity.Item;
import com.sparta.ezpzuser.domain.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    /**
     * 팝업 및 상태별 상품 목록 조회
     *
     * @param cond     조회 조건
     * @param pageable 페이징
     * @return 상품
     */
    @Transactional(readOnly = true)
    public Page<ItemPageResponseDto> findAllByItemCondition(ItemCondition cond, Pageable pageable) {
        Page<Item> page = itemRepository.findAllByItemCondition(pageable, cond);
        PageUtil.validatePageableWithPage(pageable, page);
        return page.map(ItemPageResponseDto::of);
    }

    /**
     * 상품 상세 조회
     *
     * @param itemId 상품 ID
     * @return 상품 상세
     */
    @Transactional(readOnly = true)
    public ItemResponseDto findItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ErrorType.ITEM_NOT_FOUND));
        item.verifyStatus();
        return ItemResponseDto.of(item);
    }

}
