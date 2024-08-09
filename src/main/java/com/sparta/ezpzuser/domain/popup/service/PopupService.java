package com.sparta.ezpzuser.domain.popup.service;

import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.common.util.PageUtil;
import com.sparta.ezpzuser.domain.popup.dto.PopupPageResponseDto;
import com.sparta.ezpzuser.domain.popup.dto.PopupResponseDto;
import com.sparta.ezpzuser.domain.popup.entity.Popup;
import com.sparta.ezpzuser.domain.popup.repository.popup.PopupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sparta.ezpzuser.common.exception.ErrorType.POPUP_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PopupService {

    private final PopupRepository popupRepository;
    private final ImageService imageService;

    /**
     * 팝업 상태별 목록 조회
     *
     * @param popupStatus 팝업 상태
     * @param pageable    페이징
     * @return 팝업 목록
     */
    @Transactional(readOnly = true)
    public Page<PopupPageResponseDto> findAllByPopupStatus(String popupStatus, Pageable pageable) {
        Page<Popup> page = popupRepository.findAllByPopupStatus(popupStatus, pageable);
        PageUtil.validatePageableWithPage(pageable, page);
        return page.map(PopupPageResponseDto::of);
    }

    /**
     * 팝업 상세 조회
     *
     * @param popupId 팝업 ID
     * @return 팝업 상세정보
     */
    @Transactional(readOnly = true)
    public PopupResponseDto findPopup(Long popupId) {
        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CustomException(POPUP_NOT_FOUND));
        popup.verifyStatus();
        List<String> imageUrls = imageService.findAllByPopup(popup);
        return PopupResponseDto.of(popup, imageUrls);
    }

}
