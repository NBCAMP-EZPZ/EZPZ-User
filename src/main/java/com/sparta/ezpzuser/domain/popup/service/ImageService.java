package com.sparta.ezpzuser.domain.popup.service;

import com.sparta.ezpzuser.domain.popup.entity.Image;
import com.sparta.ezpzuser.domain.popup.entity.Popup;
import com.sparta.ezpzuser.domain.popup.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    /**
     * 팝업으로 이미지 url 목록 조회
     *
     * @param popup 팝업
     * @return 이미지 url 목록
     */
    @Transactional(readOnly = true)
    public List<String> findAllByPopup(Popup popup) {
        return imageRepository.findAllByPopup(popup).stream()
                .map(Image::getUrl)
                .toList();
    }

}
