package com.sparta.ezpzuser.domain.like.service;

import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.common.exception.ErrorType;
import com.sparta.ezpzuser.common.util.PageUtil;
import com.sparta.ezpzuser.domain.item.entity.Item;
import com.sparta.ezpzuser.domain.item.repository.ItemRepository;
import com.sparta.ezpzuser.domain.like.dto.LikeContentDto;
import com.sparta.ezpzuser.domain.like.dto.LikeItemPageResponseDto;
import com.sparta.ezpzuser.domain.like.dto.LikePopupPageResponseDto;
import com.sparta.ezpzuser.domain.like.entity.Like;
import com.sparta.ezpzuser.domain.like.repository.LikeRepository;
import com.sparta.ezpzuser.domain.popup.entity.Popup;
import com.sparta.ezpzuser.domain.popup.repository.popup.PopupRepository;
import com.sparta.ezpzuser.domain.user.entity.User;
import com.sparta.ezpzuser.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class LikeService {

    private final LikeRepository likeRepository;
    private final PopupRepository popupRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    /**
     * 컨텐츠 좋아요 토글
     * @param content 컨텐츠 타입, ID
     * @param user 유저
     */
    @Transactional
    public void contentLike(LikeContentDto content, User user) {

        // 좋아요 가능한 상태인지 확인
        switch (content.getContentType()) {
            case "popup" -> popupLike(content, user);
            case "item" -> itemLike(content, user);
            default -> throw new CustomException(ErrorType.INVALID_CONTENT_TYPE);
        }
    }

    /**
     * 타입별 좋아요한 컨텐츠 목록 조회
     * @param pageable 페이징
     * @param contentType 컨텐츠 타입
     * @param user 유저
     * @return 컨텐츠 목록
     */
    public Page<?> findAllLikesByContentType(Pageable pageable, String contentType, User user) {

        User findUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new CustomException(ErrorType.DUPLICATED_USERNAME));

        // 유저 좋아요 목록
        List<Like> likeList = findUser.getLikeList();

        switch (contentType) {
            case "popup" -> {
                // 팝업 Id 목록
                List<Long> popupIdList = contentIdList(likeList, contentType);

                // 좋아요한 팝업 목록
                Page<?> popupList = popupRepository.findPopupByIdList(pageable, popupIdList)
                        .map(LikePopupPageResponseDto::of);
                PageUtil.validatePageableWithPage(pageable, popupList);
                return popupList;
            }
            case "item" -> {
                // 굿즈 ID 목록
                List<Long> itemIdList = contentIdList(likeList, contentType);

                // 좋아요한 굿즈 목록
                Page<?> itemList = itemRepository.findItemByIdList(pageable, itemIdList)
                        .map(LikeItemPageResponseDto::of);
                PageUtil.validatePageableWithPage(pageable, itemList);
                return itemList;
            }
            default -> throw new CustomException(ErrorType.INVALID_CONTENT_TYPE);
        }
    }

    /**
     * 팝업 좋아요
     * @param content 팝업 ID
     * @param user 유저
     */
    private void popupLike(LikeContentDto content, User user) {
        Popup popup = popupRepository.findById(content.getContentId())
                .orElseThrow(() -> new CustomException(ErrorType.POPUP_NOT_FOUNT));
        popup.verifyStatus();
        popup.updateLikeCount(toggleLike(content, user));
    }

    /**
     * 굿즈 좋아요
     * @param content 굿즈 ID
     * @param user 유저
     */
    private void itemLike(LikeContentDto content, User user) {
        Item item = itemRepository.findById(content.getContentId())
                .orElseThrow(() -> new CustomException(ErrorType.ITEM_NOT_FOUNT));
        item.checkStatus();
        item.updateLikeCount(toggleLike(content, user));
    }

    /**
     * 좋아요 토글
     * @param content 컨텐츠 타입, ID
     * @param user 유저
     */
    private boolean toggleLike(LikeContentDto content, User user) {
        Optional<Like> like = likeRepository.findByUserAndContentIdAndContentType(
                user, content.getContentId(), content.getContentType());

        // 좋아요 등록/취소 토글
        if (like.isEmpty()) {
            Like saveLike = Like.of(user, content.getContentId(), content.getContentType());
            likeRepository.save(saveLike);
            return true;
        }else {
            likeRepository.delete(like.get());
            return false;
        }
    }

    /**
     * 컨텐츠 ID 목록
     * @param likeList 좋아요 목록
     * @param contentType 컨텐츠 타입
     * @return ID 목록
     */
    private List<Long> contentIdList(List<Like> likeList, String contentType) {
        return likeList.stream()
                .filter(like -> contentType.equals(like.getContentType()))
                .map(Like::getContentId)
                .toList();
    }
}
