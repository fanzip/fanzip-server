package org.example.fanzip.cart.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.fanzip.cart.dto.CartItemDto;
import org.example.fanzip.cart.dto.UserShippingInfoDto;

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

    // 사용자별 장바구니에 담긴 전체 항목 조회
    List<CartItemDto> findItemsByUser(
            @Param("userId") Long userId
    );

    // 장바구니 ID별 항목 조회
    CartItemDto findItemById(
            @Param("cartItemId") Long cartItemId
    );

    // 특정 유저, 해당상품 장바구니 조회
    Integer checkCartItem(
            @Param("userId") Long userId,
            @Param("cartItemId") Long cartItemId
    );

    // 상품 Id로 cartItemId 조회
    Long findCartItemIdByProductId(
            @Param("userId") Long userId,
            @Param("productId") Long productId
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

    // 배송 정보 조회
    UserShippingInfoDto getUserShippingInfo(@Param("userId") Long userId);


}
