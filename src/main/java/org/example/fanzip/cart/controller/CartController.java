package org.example.fanzip.cart.controller;

import io.swagger.annotations.*;
import org.example.fanzip.cart.dto.*;
import org.example.fanzip.cart.service.CartService;
import org.example.fanzip.security.CustomUserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.servlet.http.HttpServletRequest;

@Api(tags = "Cart", description = "장바구니 관리 API")
@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @ApiOperation(value = "장바구니에 상품 추가", notes = "지정된 상품을 장바구니에 추가합니다.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "장바구니 상품 추가 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청 데이터 또는 이미 존재하는 상품"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 404, message = "상품을 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public CartItemResponseDto addCartItem(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @ApiParam(value = "장바구니 상품 추가 요청 데이터", required = true)
            @RequestBody AddCartItemRequestDto req
            ) {
        Long userId = principal.getUserId();
        return cartService.addItem(userId, req);
    }

    @ApiOperation(value = "장바구니 목록 조회", notes = "사용자의 장바구니에 있는 모든 상품을 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "장바구니 목록 조회 성공"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/items")
    public CartResponseDto getCartItems(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        Long userId = principal.getUserId();
        return cartService.getCart(userId);
    }

    @ApiOperation(value = "장바구니 상품 수정", notes = "장바구니 상품의 수량 또는 선택 상태를 수정합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "장바구니 상품 수정 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청 데이터"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 404, message = "장바구니 상품을 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @PutMapping("/items/{cartItemId}")
    public CartItemDto updateCartItem(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @ApiParam(value = "장바구니 상품 ID", required = true, example = "1")
            @PathVariable Long cartItemId,
            @ApiParam(value = "장바구니 상품 수정 요청 데이터", required = true)
            @RequestBody UpdateCartItemRequestDto req
    ) {
        Long userId = principal.getUserId();
        return cartService.updateItem(userId, cartItemId, req);
    }

    @ApiOperation(value = "장바구니 전체 선택/해제", notes = "장바구니의 모든 상품을 선택하거나 선택 해제합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "전체 선택/해제 성공"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @PutMapping("/items/select-all")
    public CartResponseDto selectAll(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @ApiParam(value = "전체 선택 여부 (true: 전체 선택, false: 전체 해제)", required = true, example = "true")
            @RequestParam Boolean selectAll
    ) {
        Long userId = principal.getUserId();
        return cartService.selectAll(userId, selectAll);
    }

    @ApiOperation(value = "장바구니 상품 삭제", notes = "지정된 상품을 장바구니에서 삭제합니다.")
    @ApiResponses({
            @ApiResponse(code = 204, message = "장바구니 상품 삭제 성공"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 404, message = "장바구니 상품을 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @DeleteMapping("/items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @ApiParam(value = "장바구니 상품 ID", required = true, example = "1")
            @PathVariable Long cartItemId
    ) {
        Long userId = principal.getUserId();
        cartService.removeItem(userId, cartItemId);
    }
}
