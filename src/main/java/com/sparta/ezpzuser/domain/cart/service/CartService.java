package com.sparta.ezpzuser.domain.cart.service;

import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.common.exception.ErrorType;
import com.sparta.ezpzuser.domain.cart.dto.CartCreateRequestDto;
import com.sparta.ezpzuser.domain.cart.dto.CartResponseDto;
import com.sparta.ezpzuser.domain.cart.entity.Cart;
import com.sparta.ezpzuser.domain.cart.repository.CartRepository;
import com.sparta.ezpzuser.domain.item.entity.Item;
import com.sparta.ezpzuser.domain.item.repository.ItemRepository;
import com.sparta.ezpzuser.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {


    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;

    @Transactional
    public CartResponseDto createCart(CartCreateRequestDto requestDto) {
        User user = User.builder()
                .username("exampleUser")
                .password("examplePassword")
                .name("John Doe")
                .email("john.doe@example.com")
                .phoneNumber("123-456-7890")
                .userStatus("active")
                .build();

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
}
