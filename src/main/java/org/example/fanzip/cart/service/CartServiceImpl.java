package org.example.fanzip.cart.service;

import org.example.fanzip.cart.dto.*;
import org.example.fanzip.cart.mapper.CartMapper;
import org.example.fanzip.market.mapper.MarketMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
//@Transactional
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

    @Override
    public CartResponseDto getCart(Long userId) {
        List<CartItemDto> items = cartMapper.findItemsByUser(userId)
                .stream().map(item -> {
                    BigDecimal total = item.getDiscountedPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));
                    item.setTotalPrice(total);
                    return item;
                }).toList();

        // 선택 항목 합
        BigDecimal grandTotal = items.stream()
                .filter(CartItemDto::getIsSelected)
                .map(CartItemDto::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponseDto.builder()
                .items(items)
                .grandTotal(grandTotal)
                .build();
    }

    @Override
    public CartItemDto updateItem(Long userId, Long cartItemId, UpdateCartItemRequestDto req) {
        // 본인 소유 장바구니 개수 검증
        Integer carts = cartMapper.checkOwnership(userId, cartItemId);
        if(carts != 1) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "장바구니가 한개가 아닙니다.");
        }

        CartItemDto existing = cartMapper.findItemById(cartItemId);

        // 수량 재고 체크
        int stock = marketMapper.getStock(existing.getProductId());
        if(req.getQuantity() > stock) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "재고부족");
        }

        // 업데이트
        cartMapper.updateCartItem(cartItemId, req.getQuantity(), req.getIsSelected());

        return cartMapper.findItemById(cartItemId);
    }

    @Override
    public CartResponseDto selectAll(Long userId, Boolean selectAll) {
        cartMapper.updateAllSelection(userId, selectAll);
        return getCart(userId);
    }

    @Override
    public void removeItem(Long userId, Long cartItemId) {
        cartMapper.checkOwnership(userId, cartItemId);
        cartMapper.deleteCartItem(cartItemId);
    }
}
