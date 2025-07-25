package org.example.fanzip.market.service;

import org.example.fanzip.market.dto.ProductDetailDto;
import org.example.fanzip.market.dto.ProductListDto;

import java.util.List;

public interface MarketService {
    // 최초 또는 전체 상품 목록 조회
    List<ProductListDto> getAllProducts(int limit);

    // 마지막으로 가져온 상품 이후 목록 조회 (커서 페이징)
    List<ProductListDto> getProductsAfter(Long lastProductId, int limit);

    // 상세 상품 조회
    ProductDetailDto getProductDetail(Long userId, Long productId);

    // 검색
    List<ProductListDto> searchProducts(String keyword, Long lastProductId, int limit);

}
