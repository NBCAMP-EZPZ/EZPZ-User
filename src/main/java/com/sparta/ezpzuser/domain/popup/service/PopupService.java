package com.sparta.ezpzuser.domain.popup.service;

import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.common.exception.ErrorType;
import com.sparta.ezpzuser.common.util.PageUtil;
import com.sparta.ezpzuser.domain.popup.dto.PopupPageResponseDto;
import com.sparta.ezpzuser.domain.popup.dto.PopupResponseDto;
import com.sparta.ezpzuser.domain.popup.entity.Popup;
import com.sparta.ezpzuser.domain.popup.repository.popup.PopupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PopupService {

    private final PopupRepository popupRepository;
    private final ImageService imageService;

    /**
     * 팝업 상태별 목록 조회
     * @param pageUtil 페이징 기준 정보
     * @return 팝업 목록
     */
    public Page<?> findAllPopupsByStatus(PageUtil pageUtil) {
        return popupRepository.findAllPopupsByStatus(pageUtil)
                .map(PopupPageResponseDto::of);
    }

    /**
     * 팝업 상세 조회
     * @param popupId 팝업 ID
     * @return 팝업 상세정보
     */
    public PopupResponseDto findPopup(Long popupId) {
        Popup popup = findPopupById(popupId);

        List<String> imageUrls = imageService.findAllByPopup(popup);

        return PopupResponseDto.of(popup, imageUrls);
    }

    /**
     * 팝업 찾기
     * <p>
     *     심사 중 또는 취소된 팝업일 경우 예외처리
     * </p>
     * @param popupId 팝업 ID
     * @return 팝업
     */
    private Popup findPopupById(Long popupId) {
        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CustomException(ErrorType.POPUP_NOT_FOUNT));
        popup.verifyStatus();
        return popup;
    }
}
