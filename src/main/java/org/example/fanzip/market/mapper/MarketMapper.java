package org.example.fanzip.market.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.fanzip.market.domain.MarketVO;

import java.util.List;

@Mapper
public interface MarketMapper {
    // 모든 상품 목록 조회 (limit 개수만큼)
    List<MarketVO> getAllProducts(@Param("limit") int limit);

    // 마지막으로 가져온 productId 이후의 상품 조회
    List<MarketVO> getProductsAfter(
            @Param("lastProductId") Long lastProductId,
            @Param("limit") int limit
    );

    // 상품 상세 페이지 조회
    MarketVO findProductById(@Param("productId") Long productId);

    // 인플루언서별 구독 등급 찾기
    @Select("""
                select grade_id
                from memberships
                where user_id = #{userId}
                  and influencer_id = #{influencerId}
                  and status = 'ACTIVE'
                limit 1
            """)
    Integer findMyGrade(
            @Param("userId") Long userId,
            @Param("influencerId") Long influencerId
    );

    // 검색 기능 (1페이지)
    List<MarketVO> searchProducts(
            @Param("keyword") String keyword,
            @Param("limit") int limit
    );

    // 검색 기능
    List<MarketVO> searchProductsAfter(
            @Param("keyword") String keyword,
            @Param("lastProductId") Long lastProductId,
            @Param("limit") int limit
    );
}
