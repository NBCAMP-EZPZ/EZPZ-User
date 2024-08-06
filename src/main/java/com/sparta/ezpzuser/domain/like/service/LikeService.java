package com.sparta.ezpzuser.domain.like.service;

import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.common.lock.DistributedLock;
import com.sparta.ezpzuser.domain.item.entity.Item;
import com.sparta.ezpzuser.domain.item.repository.ItemRepository;
import com.sparta.ezpzuser.domain.like.dto.LikeRequestDto;
import com.sparta.ezpzuser.domain.like.dto.LikeResponseDto;
import com.sparta.ezpzuser.domain.like.dto.LikedItemResponseDto;
import com.sparta.ezpzuser.domain.like.dto.LikedPopupResponseDto;
import com.sparta.ezpzuser.domain.like.entity.Like;
import com.sparta.ezpzuser.domain.like.entity.LikeContentType;
import com.sparta.ezpzuser.domain.like.repository.LikeRepository;
import com.sparta.ezpzuser.domain.popup.entity.Popup;
import com.sparta.ezpzuser.domain.popup.repository.popup.PopupRepository;
import com.sparta.ezpzuser.domain.user.entity.User;
import com.sparta.ezpzuser.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sparta.ezpzuser.common.exception.ErrorType.*;
import static com.sparta.ezpzuser.common.util.PageUtil.validatePageableWithPage;
import static com.sparta.ezpzuser.domain.like.entity.LikeContentType.ITEM;
import static com.sparta.ezpzuser.domain.like.entity.LikeContentType.POPUP;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PopupRepository popupRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    /**
     * 좋아요 토글
     *
     * @param dto  컨텐츠 타입, ID
     * @param user 유저
     */
    @DistributedLock(
            key = "'toggleLike-contentType-'.concat(#dto.contentType)" +
                    ".concat('-contentId-').concat(#dto.contentId)"
    )
    public LikeResponseDto toggleLike(LikeRequestDto dto, User user) {
        LikeContentType contentType = dto.getContentType();
        Long contentId = dto.getContentId();
        User requestUser = userRepository.findById(user.getId()).orElseThrow();
        boolean toggleResult;
        switch (contentType) {
            case POPUP -> toggleResult = togglePopupLike(contentId, requestUser);
            case ITEM -> toggleResult = toggleItemLike(contentId, requestUser);
            default -> throw new CustomException(INVALID_CONTENT_TYPE);
        }
        return LikeResponseDto.of(toggleResult, contentType, contentId);
    }

    /**
     * 타입별 좋아요한 컨텐츠 목록 조회
     *
     * @param pageable 페이징
     * @param type     컨텐츠 타입
     * @param user     유저
     * @return 컨텐츠 목록
     */
    @Transactional(readOnly = true)
    public Page<?> findAllLikedContentByType(Pageable pageable, String type, User user) {
        LikeContentType contentType = LikeContentType.valueOf(type.toUpperCase());
        List<Like> likeList = likeRepository.findByUserAndContentType(user, contentType);
        switch (contentType) {
            case POPUP -> {
                List<Long> likedPopupIdList = getLikedContentIdList(likeList);
                Page<Popup> page = popupRepository.findAllByIdList(pageable, likedPopupIdList);
                validatePageableWithPage(pageable, page);
                return page.map(LikedPopupResponseDto::of);
            }
            case ITEM -> {
                List<Long> likedItemIdList = getLikedContentIdList(likeList);
                Page<Item> page = itemRepository.findAllByIdList(pageable, likedItemIdList);
                validatePageableWithPage(pageable, page);
                return page.map(LikedItemResponseDto::of);
            }
            default -> throw new CustomException(INVALID_CONTENT_TYPE);
        }
    }

    /**
     * 팝업 좋아요 토글
     *
     * @param popupId 팝업 ID
     * @param user    이용자
     * @return 좋아요 여부 (true: 좋아요, false: 좋아요 취소)
     */
    private boolean togglePopupLike(Long popupId, User user) {
        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CustomException(POPUP_NOT_FOUND));
        popup.verifyStatus(); // 좋아요 토글 가능한 상태인지 검증

        boolean toggleResult = executeToggleLike(POPUP, popupId, user);
        popup.updateLikeCount(toggleResult);
        return toggleResult;
    }

    /**
     * 굿즈 좋아요 토글
     *
     * @param itemId 굿즈 ID
     * @param user   이용자
     * @return 좋아요 여부 (true: 좋아요, false: 좋아요 취소)
     */
    private boolean toggleItemLike(Long itemId, User user) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ITEM_NOT_FOUND));
        item.verifyStatus(); // 좋아요 토글 가능한 상태인지 검증

        boolean toggleResult = executeToggleLike(ITEM, itemId, user);
        item.updateLikeCount(toggleResult);
        return toggleResult;
    }

    /**
     * 좋아요 토글 수행
     *
     * @param contentType 컨텐츠 타입
     * @param contentId   컨텐츠 ID
     * @param user        이용자
     * @return 좋아요 여부 (true: 좋아요, false: 좋아요 취소)
     */
    private boolean executeToggleLike(LikeContentType contentType, Long contentId, User user) {
        Like like = likeRepository.findByContentTypeAndContentIdAndUser(contentType, contentId, user)
                .orElse(null);
        if (like == null) {  // 좋아요인 경우
            likeRepository.save(Like.of(contentType, contentId, user));
            return true;
        } else {  // 좋아요 취소인 경우
            likeRepository.delete(like);
            return false;
        }
    }

    /**
     * 컨텐츠 타입별 좋아요한 컨텐츠 ID 목록 조회
     *
     * @param likeList 좋아요 목록
     * @return 컨텐츠 ID 목록
     */
    private List<Long> getLikedContentIdList(List<Like> likeList) {
        return likeList.stream()
                .map(Like::getContentId)
                .toList();
    }

}
