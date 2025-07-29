package org.example.fanzip.cart.controller;

import org.example.fanzip.cart.dto.*;
import org.example.fanzip.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestHeader;
import org.example.fanzip.auth.jwt.JwtProcessor;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // 장바구니에 추가 - body: productId, quantity
    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public CartItemResponseDto addCartItem(
//            HttpServletRequest request,
            @RequestHeader("X-USER-ID") Long userId,
            @RequestBody AddCartItemRequestDto req
            ) {

//        Long userId = (Long) request.getAttribute("userId");
        return cartService.addItem(userId, req);
    }

    // 장바구니 물품 전체 조회
    @GetMapping("/items")
    public CartResponseDto getCartItems(
//             HttpServletRequest request
            @RequestHeader("X-USER-ID") Long userId
    ) {
//        Long userId = (Long) request.getAttribute("userId");
        return cartService.getCart(userId);
    }

    // 개별 수량/선택 상태 변경 - quantity, isSelected
    @PutMapping("/items/{cartItemId}")
    public CartItemDto updateCartItem(
//            HttpServletRequest request,
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable Long cartItemId,
            @RequestBody UpdateCartItemRequestDto req
    ) {
//        Long userId = (Long) request.getAttribute("userId");
        return cartService.updateItem(userId, cartItemId, req);
    }

    // 전체 선택/해제 토글
    @PutMapping("/items/select-all")
    public CartResponseDto selectAll(
            @RequestHeader("X-USER-ID") Long userId,
//            HttpServletRequest request,
            @RequestParam Boolean selectAll
    ) {

//        Long userId = (Long) request.getAttribute("userId");
        return cartService.selectAll(userId, selectAll);
    }

    // 개별 삭제
    @DeleteMapping("/items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(
//            HttpServletRequest request,
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable Long cartItemId
    ) {
//        Long userId = (Long) request.getAttribute("userId");
        cartService.removeItem(userId, cartItemId);
    }
}
