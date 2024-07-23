package com.sparta.ezpzuser.domain.cart.service;

import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.common.exception.ErrorType;
import com.sparta.ezpzuser.domain.cart.dto.CartCreateRequestDto;
import com.sparta.ezpzuser.domain.cart.dto.CartResponseDto;
import com.sparta.ezpzuser.domain.cart.dto.CartUpdateRequestDto;
import com.sparta.ezpzuser.domain.cart.entity.Cart;
import com.sparta.ezpzuser.domain.cart.repository.CartRepository;
import com.sparta.ezpzuser.domain.item.entity.Item;
import com.sparta.ezpzuser.domain.item.repository.ItemRepository;
import com.sparta.ezpzuser.domain.user.entity.User;
import com.sparta.ezpzuser.domain.user.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {


    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    /**
     * 장바구니 생성
     *
     * @param requestDto 생성할 장바구니 정보
     * @param user       요청 사용자
     * @return 생성된 장바구니 정보
     */
    @Transactional
    public CartResponseDto createCart(CartCreateRequestDto requestDto, User user) {
        Item item = itemRepository.findById(requestDto.getItemId()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 상품입니다.") // 에러코드 만들어주시면 수정
        );

        // 현재 재고량보다 사용자가 요청한 개수가 많을 때 예외 처리
        if (item.getStock() <= requestDto.getQuantity()) {
            throw new CustomException(ErrorType.INSUFFICIENT_STOCK);
        }

        Cart cart = cartRepository.save(Cart.of(
                requestDto.getQuantity(),
                user,
                item
        ));

        return CartResponseDto.of(cart);
    }

    /**
     * 장바구니 내역 조회
     *
     * @param user 요청한 사용자
     * @return 장바구니 내역 리스트
     */
    public List<CartResponseDto> findCartsAll(User user) {
        List<Cart> cartList = cartRepository.findAllByUserIdOrderByCreatedAtDesc(
                user.getId());

        // 장바구니에 넣을 때는 재고가 있었지만, 현재는 재고가 없는 물건에 대한 예외 처리
        for (Cart cart : cartList) {
            if (cart.getItem().getStock() < cart.getQuantity()) {
                throw new CustomException(ErrorType.STOCK_UNAVAILABLE);
            }
        }
        return cartList.stream().map(CartResponseDto::of).collect(Collectors.toList());
    }

    /**
     * 장바구니 수량 변경
     *
     * @param cartId     수량을 변경할 장바구니의 id
     * @param requestDto 변경할 수량
     * @param user       요청한 사용자
     * @return 변경된 장바구니의 정보
     */
    @Transactional
    public CartResponseDto updateCart(Long cartId, CartUpdateRequestDto requestDto, User user) {
        Cart cart = getValidatedCart(cartId, user);

        Item item = itemRepository.findById(cart.getItem().getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        // 재고보다 많은 수량으로 변경하려고 할 때의 예외 처리
        if (item.getStock() < requestDto.getQuantity()) {
            throw new CustomException(ErrorType.INSUFFICIENT_STOCK);
        }

        cart.updateCart(requestDto.getQuantity());
        return CartResponseDto.of(cart);
    }

    /**
     * 장바구니 삭제
     *
     * @param cartId 삭제할 장바구니 id
     * @param user   요청한 사용자
     */
    @Transactional
    public void deleteCart(Long cartId, User user) {
        Cart cart = getValidatedCart(cartId, user);
        cartRepository.delete(cart);
    }

    /* UTIL*/

    /**
     * 해당 장바구니가 존재하고, 요청한 사용자의 장바구니가 맞는지 확인하는 메서드
     *
     * @param cartId 장바구니 id
     * @param user   요청한 사용자
     * @return 존재하는 장바구니
     */
    private Cart getValidatedCart(Long cartId, User user) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CustomException(ErrorType.CART_NOT_FOUND));

        // 본인의 장바구니가 아닌데 삭제하려고 할 때의 예외 처리
        if (!user.getId().equals(cart.getUser().getId())) {
            throw new CustomException(ErrorType.UNAUTHORIZED_CART_ACCESS);
        }

        return cart;
    }
}
