package org.example.fanzip.cart.service;

import org.example.fanzip.cart.dto.*;

public interface CartService {
    // 장바구니에 추가
    CartItemResponseDto addItem(Long userId, AddCartItemRequestDto req);

    CartResponseDto getCart(Long userId);
    CartItemDto updateItem(Long userId, Long cartItemId, UpdateCartItemRequestDto req);
    CartResponseDto selectAll(Long userId, Boolean selectAll);
    void removeItem(Long userId, Long cartItemId);
}
