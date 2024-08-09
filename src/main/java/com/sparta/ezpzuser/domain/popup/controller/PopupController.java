package com.sparta.ezpzuser.domain.popup.controller;

import com.sparta.ezpzuser.domain.popup.dto.PopupPageResponseDto;
import com.sparta.ezpzuser.domain.popup.dto.PopupResponseDto;
import com.sparta.ezpzuser.domain.popup.service.PopupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.sparta.ezpzuser.common.util.ControllerUtil.getResponseEntity;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PopupController {

    private final PopupService popupService;

    /**
     * 팝업 상태별 목록 조회
     *
     * @param popupStatus 팝업 상태
     * @param pageable    페이징
     * @return 팝업 목록
     */
    @GetMapping("/v1/popups")
    public ResponseEntity<?> findAllByPopupCondition(
            @RequestParam(required = false) String popupStatus,
            Pageable pageable) {

        Page<PopupPageResponseDto> response = popupService.findAllByPopupStatus(popupStatus, pageable);
        return getResponseEntity(response, "팝업 목록 조회 성공");
    }

    /**
     * 팝업 상세 조회
     *
     * @param popupId 팝업 ID
     * @return 팝업 상세정보
     */
    @GetMapping("/v1/popups/{popupId}")
    public ResponseEntity<?> findPopup(
            @PathVariable Long popupId) {

        PopupResponseDto responseDto = popupService.findPopup(popupId);
        return getResponseEntity(responseDto, "팝업스토어 상세보기 조회 성공");
    }

}
