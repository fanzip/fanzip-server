package org.example.fanzip.market.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.fanzip.market.domain.MarketVO;

import java.util.List;

@Mapper
public interface MarketMapper {
    /*
       - 모든 상품 목록 조회 (limit 개수만큼)
       - (param) limit: 조회 건수
       - (return) ProductVO 리스트
     */
    List<MarketVO> getAllProducts(@Param("limit") int limit);

    /*
       - 마지막으로 가져온 productId 이후의 상품 조회
       - (param) lastProductId: 마지막으로 로드된 상품 ID (처음은 null 허용)
       - (param) limit: 조회 건수
       - (return) ProductVO 리스트
     */
    List<MarketVO> getProductsAfter(
            @Param("lastProductId") Long lastProductId,
            @Param("limit") int limit
    );
}
