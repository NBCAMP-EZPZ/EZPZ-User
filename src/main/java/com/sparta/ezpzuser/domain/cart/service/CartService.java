package com.sparta.ezpzuser.domain.cart.service;

import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.common.lock.DistributedLock;
import com.sparta.ezpzuser.domain.cart.dto.CartCreateRequestDto;
import com.sparta.ezpzuser.domain.cart.dto.CartResponseDto;
import com.sparta.ezpzuser.domain.cart.dto.CartUpdateRequestDto;
import com.sparta.ezpzuser.domain.cart.entity.Cart;
import com.sparta.ezpzuser.domain.cart.repository.CartRepository;
import com.sparta.ezpzuser.domain.item.entity.Item;
import com.sparta.ezpzuser.domain.item.repository.ItemRepository;
import com.sparta.ezpzuser.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.sparta.ezpzuser.common.exception.ErrorType.*;

@Service
@RequiredArgsConstructor
public class CartService {

    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;

    /**
     * 장바구니 생성
     *
     * @param dto  생성할 장바구니 정보
     * @param user 요청 이용자
     * @return 생성된 장바구니 정보
     */
    @DistributedLock(key = "'createCart-userId-'.concat(#user.id)")
    public CartResponseDto createCart(CartCreateRequestDto dto, User user) {
        Item item = getItem(dto.getItemId());
        item.checkStock(dto.getQuantity());
        Cart cart = cartRepository.save(Cart.of(dto.getQuantity(), user, item));
        return CartResponseDto.of(cart);
    }

    /**
     * 장바구니 내역 조회
     *
     * @param user 요청한 사용자
     * @return 장바구니 내역 리스트
     */
    @Transactional(readOnly = true)
    public List<CartResponseDto> findCartsAll(User user) {
        List<Cart> cartList = cartRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId());
        return cartList.stream()
                .map(CartResponseDto::of)
                .collect(Collectors.toList());
    }

    /**
     * 장바구니 수량 변경
     *
     * @param cartId 수량을 변경할 장바구니의 id
     * @param dto    변경할 수량
     * @param user   요청한 사용자
     * @return 변경된 장바구니의 정보
     */
    @DistributedLock(key = "'updateCart-cartId-'.concat(#cartId)")
    public CartResponseDto updateCart(Long cartId, CartUpdateRequestDto dto, User user) {
        Cart cart = getUserCart(cartId, user);
        Item item = getItem(cart.getItem().getId());
        item.checkStock(dto.getQuantity());
        cart.updateCart(dto.getQuantity());
        return CartResponseDto.of(cart);
    }

    /**
     * 장바구니 삭제
     *
     * @param cartId 삭제할 장바구니 id
     * @param user   요청한 사용자
     */
    @DistributedLock(key = "'deleteCart-cartId-'.concat(#cartId)")
    public void deleteCart(Long cartId, User user) {
        Cart cart = getUserCart(cartId, user);
        cartRepository.delete(cart);
    }

    /* UTIL */

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ITEM_NOT_FOUND));
    }

    /**
     * 해당 장바구니가 존재하고, 요청한 사용자의 장바구니가 맞는지 확인하는 메서드
     *
     * @param cartId 장바구니 id
     * @param user   요청한 사용자
     * @return 존재하는 장바구니
     */
    private Cart getUserCart(Long cartId, User user) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CustomException(CART_NOT_FOUND));
        // 본인의 장바구니가 아닌 경우
        if (!cart.getUser().getId().equals(user.getId())) {
            throw new CustomException(UNAUTHORIZED_CART_ACCESS);
        }
        return cart;
    }

}
