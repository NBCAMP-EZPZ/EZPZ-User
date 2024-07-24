package com.sparta.ezpzuser.domain.popup.controller;

import com.sparta.ezpzuser.common.security.UserDetailsImpl;
import com.sparta.ezpzuser.common.util.PageUtil;
import com.sparta.ezpzuser.domain.popup.dto.PopupResponseDto;
import com.sparta.ezpzuser.domain.popup.service.PopupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.sparta.ezpzuser.common.util.ControllerUtil.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PopupController {

    private final PopupService popupService;

    /**
     * 팝업 상태별 목록 조회
     * @param page 페이지
     * @param size 개수
     * @param sortBy 정렬 기준
     * @param popupStatus 팝업 상태
     * @return 팝업 목록
     */
    @GetMapping("/v1/popups")
    public ResponseEntity<?> findAllPopupsByStatus(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "all") String popupStatus) {
        PageUtil pageUtil = PageUtil.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .firstStatus(popupStatus)
                .build();
        Page<?> popupList = popupService.findAllPopupsByStatus(pageUtil);
        return getResponseEntity(popupList, "팝업 목록 조회 성공");
    }

    /**
     * 팝업 상세 조회
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
