package org.example.fanzip.market.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.fanzip.market.domain.MarketVO;
import org.example.fanzip.market.dto.ProductDetailDto;
import org.example.fanzip.market.dto.ProductListDto;
import org.springframework.security.core.parameters.P;

import java.util.List;

@Mapper
public interface MarketMapper {
    // 마지막으로 가져온 productId 까지의 상품 조회
    // sort: latest || priceAsc || recommended
    List<ProductListDto> getProducts(
            @Param("userId") Long userId,
            @Param("lastProductId") Long lastProductId,
            @Param("limit") int limit,
            @Param("sort") String sort,
            @Param("category") String category,
            @Param("onlySubscribed") boolean onlySubscribed
    );

    // 상품 상세 페이지 조회
    ProductDetailDto findProductById(
            @Param("productId") Long productId,
            @Param("userId") Long userId
    );

    // 검색 기능
    List<ProductListDto> searchProducts(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            @Param("lastProductId") Long lastProductId,
            @Param("limit") int limit
    );

    // 재고 조회
    int getStock(
            @Param("productId") Long productId);

    // 상품 추가
    int insertProduct(MarketVO marketVO);

    // 마지막 삽입된 상품 ID 조회
    Long getLastInsertId();
}
