package org.example.fanzip.cart.service;

import org.example.fanzip.cart.dto.AddCartItemRequestDto;
import org.example.fanzip.cart.dto.CartItemResponseDto;

public interface CartService {
    // 장바구니에 추가
    CartItemResponseDto addItem(Long userId, AddCartItemRequestDto request);
}
