package org.example.fanzip.market.service;

import org.example.fanzip.market.dto.ProductAddRequestDto;
import org.example.fanzip.market.dto.ProductAddResponseDto;
import org.example.fanzip.market.dto.ProductDetailDto;
import org.example.fanzip.market.dto.ProductListDto;

import java.util.List;

public interface MarketService {
    // 마지막으로 가져온 상품 이후 목록 조회 (커서 페이징)
    List<ProductListDto> getProducts(Long userId, Long lastProductId, int limit,
                                     String sort, String category, boolean onlySubscribed);

    // 상세 상품 조회
    ProductDetailDto getProductDetail(Long userId, Long productId);

    // 검색
    List<ProductListDto> searchProducts(Long userId, String keyword, Long lastProductId, int limit);

    // 상품 추가
    ProductAddResponseDto addProduct(ProductAddRequestDto requestDto);

}
