package org.example.fanzip.cart.controller;

import org.example.fanzip.cart.dto.AddCartItemRequestDto;
import org.example.fanzip.cart.dto.CartItemResponseDto;
import org.example.fanzip.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // 장바구니에 추가
    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public CartItemResponseDto addCartItem(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody AddCartItemRequestDto requestDto
            ) {
        Long userId = principal.getUserId();
        return cartService.addItem(userId, requestDto);
    }
}
