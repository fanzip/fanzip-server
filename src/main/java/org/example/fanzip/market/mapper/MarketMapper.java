package org.example.fanzip.market.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.fanzip.market.dto.ProductDetailDto;
import org.example.fanzip.market.dto.ProductListDto;

import java.util.List;

@Mapper
public interface MarketMapper {
    // 모든 상품 목록 조회 (limit 개수만큼)
    List<ProductListDto> getAllProducts(
            @Param("limit") int limit);

    // 마지막으로 가져온 productId 이후의 상품 조회
    List<ProductListDto> getProductsAfter(
            @Param("lastProductId") Long lastProductId,
            @Param("limit") int limit
    );

    // 상품 상세 페이지 조회
    ProductDetailDto findProductById(
            @Param("productId") Long productId,
            @Param("userId") Long userId
    );

    // 검색 기능 (1페이지)
    List<ProductListDto> searchProducts(
            @Param("keyword") String keyword,
            @Param("limit") int limit
    );

    // 검색 기능
    List<ProductListDto> searchProductsAfter(
            @Param("keyword") String keyword,
            @Param("lastProductId") Long lastProductId,
            @Param("limit") int limit
    );

    // 재고 조회
    int getStock(
            @Param("productId") Long productId);
}
