package org.example.fanzip.market.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.example.fanzip.market.domain.MarketVO;
import org.example.fanzip.market.dto.ProductDetailResponseDto;
import org.example.fanzip.market.mapper.MarketMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
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
    public ProductDetailResponseDto getProductDetail(Long userId, Long productId) {
        MarketVO vo = marketMapper.findProductById(productId);
        if (vo == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "해당 상품을 찾을 수 없습니다.");
        }

        Integer grade = marketMapper.findMyGrade(userId, vo.getInfluencerId());
        if(grade == null) {
            grade = 0;
        }

        // grade: 1=white, 2=silver, 3=gold, 4=vip, default=general
        LocalDateTime openTime = switch(grade) {
            case 1 -> vo.getWhiteOpenTime();
            case 2 -> vo.getSilverOpenTime();
            case 3 -> vo.getGoldOpenTime();
            case 4 -> vo.getVipOpenTime();
            default -> vo.getGeneralOpenTime();
        };

        List<String> parsedDetailImages;
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            parsedDetailImages = objectMapper.readValue(
                    vo.getDetailImages(),
                    new TypeReference<List<String>>() {}
            );
        } catch (Exception e) {
            parsedDetailImages = Collections.emptyList();
        }

        return ProductDetailResponseDto.builder()
                .productId(vo.getProductId())
                .name(vo.getName())
                .description(vo.getDescription())
                .price(vo.getPrice())
                .discountedPrice(vo.getDiscountedPrice())
                .shippingPrice(vo.getShippingPrice())
                .stock(vo.getStock())
                .thumbnailImage(vo.getThumbnailImage())
                .detailImages(parsedDetailImages)
                .gradeId(grade)
                .openTime(openTime)
                .isAvailable(LocalDateTime.now().isAfter(openTime))
                .options(Collections.emptyList()) // 옵션은 추후에 구현
                .build();
    }

    @Override
    public List<MarketVO> searchProducts(String keyword, Long lastProductId, int limit) {
        if(lastProductId == null) {
            return marketMapper.searchProducts(keyword, limit);
        }
        return marketMapper.searchProductsAfter(keyword, lastProductId, limit);
    }
}
