package org.example.fanzip.cart.service;

import org.example.fanzip.cart.dto.AddCartItemRequestDto;
import org.example.fanzip.cart.dto.CartItemResponseDto;
import org.example.fanzip.cart.mapper.CartMapper;
import org.example.fanzip.market.mapper.MarketMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CartServiceImpl implements CartService {
    private final CartMapper cartMapper;
    private final MarketMapper marketMapper;

    @Autowired
    public CartServiceImpl(CartMapper cartMapper, MarketMapper marketMapper) {
        this.cartMapper = cartMapper;
        this.marketMapper = marketMapper;
    }

    @Override
    public CartItemResponseDto addItem(Long userId, AddCartItemRequestDto req) {
        // 재고 확인
        int available = marketMapper.getStock(req.getProductId());
        if(available < req.getQuantity()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "재고가 부족합니다."
            );
        }

        // 카트 없으면 생성
        Long cartId = cartMapper.findCartIdByUserId(userId);
        if(cartId == null) {
            cartId = cartMapper.createCart(userId);
        }

        // 상품 추가
        Long cartItemId = cartMapper.insertCartItem(
                cartId, req.getProductId(), req.getQuantity()
        );

        return CartItemResponseDto.builder()
                .cartItemId(cartItemId)
                .productId(req.getProductId())
                .quantity(req.getQuantity())
                .build();
    }

}
