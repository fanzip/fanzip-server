package org.example.fanzip.cart.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.fanzip.cart.dto.CartItemDto;

import java.util.List;

@Mapper
public interface CartMapper {
    // 사용자별 장바구니 ID 조회
    Long findCartIdByUserId(@Param("userId") Long userId);

    // 새로운 장바구니 생성
    Long createCart(@Param("userId") Long userId);

    // 장바구니에 항목 추가
    Long insertCartItem(
            @Param("cartId") Long cartId,
            @Param("productId") Long productId,
            @Param("quantity") int quantity
    );

    // 사용자별 장바구니에 담긴 항목 조회
    List<CartItemDto> findItemsByUser(
            @Param("userId") Long userId
    );

    // 장바구니 ID별 항목 조회
    CartItemDto findItemById(
            @Param("cartItemId") Long cartItemId
    );

    // 장바구니 소유 여부/개수 확인
    Integer checkOwnership(
            @Param("userId") Long userId,
            @Param("cartItemId") Long cartItemId
    );

    // 장바구니 업데이트 (수량 등)
    void updateCartItem(
            @Param("cartItemId") Long cartItemId,
            @Param("quantity") int quantity,
            @Param("isSelected") boolean isSelected
    );

    // 전체 선택
    void updateAllSelection(
            @Param("userId") Long userId,
            @Param("selectAll") boolean selectAll
    );

    // 항목 삭제
    void deleteCartItem(
            @Param("cartItemId") Long cartItemId
    );
}
