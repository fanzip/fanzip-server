package org.example.fanzip.market.service;

import org.example.fanzip.market.domain.MarketVO;
import org.example.fanzip.market.dto.ProductDetailResponseDto;
import org.example.fanzip.market.mapper.MarketMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class MarketServiceImpl implements MarketService {
    private final MarketMapper marketMapper;

    @Autowired
    public MarketServiceImpl(MarketMapper marketMapper) {
        this.marketMapper = marketMapper;
    }

    @Override
    public List<MarketVO> getAllProducts(int limit) {
        return marketMapper.getAllProducts(limit);
    }

    @Override
    public List<MarketVO> getProductsAfter(Long lastProductId, int limit) {
        return (lastProductId == null)
                ? marketMapper.getAllProducts(limit)
                : marketMapper.getProductsAfter(lastProductId, limit);
    }

    // 상품 상세 조회
    @Override
    public ProductDetailResponseDto getProductDetail(Long productId) {
        MarketVO vo = marketMapper.findProductById(productId);
        if (vo == null) {
            throw new IllegalArgumentException("해당 상품을 찾을 수 없습니다.");
        }

        return ProductDetailResponseDto.builder()
                .productId(vo.getProductId())
                .name(vo.getName())
                .price(vo.getPrice())
                .discountedPrice(vo.getDiscountedPrice())
                .shippingPrice(vo.getShippingPrice())
                .description("상품 설명 placeholder") // 필요 시 vo.getDescription()으로 교체
                .thumbnailImage(vo.getThumbnailImage())
                .detailImages(Collections.emptyList()) // 추후 JSON 파싱 필요 시 처리
                .options(Collections.emptyList()) // 옵션은 나중에 구현
                .build();
    }
}
