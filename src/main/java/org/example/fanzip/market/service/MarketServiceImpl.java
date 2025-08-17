package org.example.fanzip.market.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.example.fanzip.market.domain.enums.ProductCategory;
import lombok.extern.slf4j.Slf4j;
import org.example.fanzip.market.domain.MarketVO;
import org.example.fanzip.market.dto.ProductAddRequestDto;
import org.example.fanzip.market.dto.ProductAddResponseDto;
import org.example.fanzip.market.dto.ProductDetailDto;
import org.example.fanzip.market.dto.ProductListDto;
import org.example.fanzip.market.exception.MarketErrorCode;
import org.example.fanzip.market.event.ProductCreatedEvent;
import org.example.fanzip.market.exception.ProductException;
import org.example.fanzip.market.mapper.MarketMapper;
import org.example.fanzip.market.util.ProductValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.awt.*;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class MarketServiceImpl implements MarketService {
    private final ApplicationEventPublisher publisher;
    private final MarketMapper marketMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public MarketServiceImpl(MarketMapper marketMapper, ApplicationEventPublisher publisher) {
        this.marketMapper = marketMapper;
        this.publisher = publisher;
    }


    @Override
    public List<ProductListDto> getProducts(Long userId, Long lastProductId, int limit, String sort, String category, boolean onlySubscribed) {
        return marketMapper.getProducts(userId, lastProductId, limit, sort, category, onlySubscribed);
    }

    // 상품 상세 조회
    @Override
    public ProductDetailDto getProductDetail(Long userId, Long productId) {
        ProductDetailDto detail = marketMapper.findProductById(productId, userId);
        if (detail == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "해당 상품을 찾을 수 없습니다.");
        }

        // JSON -> List (상세 이미지 목록)
        try{
            detail.setDetailImagesList(
                    objectMapper.readValue(
                            detail.getDetailImages(),
                            new TypeReference<List<String>>() {}
                    )
            );
        } catch (Exception e) {
            detail.setDetailImagesList(Collections.emptyList());
        }

        // JSON -> List (설명 이미지 목록)
        try{
            detail.setDescriptionImagesList(
                    objectMapper.readValue(
                    detail.getDescriptionImages(),
                    new TypeReference<List<String>>() {}
                    )
            );
        } catch (Exception e) {
            detail.setDescriptionImagesList(Collections.emptyList());
        }

        return detail;
    }

    @Override
    public List<ProductListDto> searchProducts(Long userId, String keyword, Long lastProductId, int limit, String sort, String category) {
        return marketMapper.searchProducts(userId, keyword, lastProductId, limit, sort, category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductAddResponseDto addProduct(ProductAddRequestDto requestDto) {
        try {
            log.info("상품 등록 시작 - 요청 데이터: {}", requestDto);

            // 상품 정보 검증
            log.info("검증 시작");
            ProductValidationUtils.validateProductAddRequest(requestDto);
            log.info("검증 완료");

            // DTO -> VO 변환
            log.info("DTO -> VO 변환 시작");
            MarketVO marketVO = convertToMarketVO(requestDto);
            log.info("VO 변환 완료: {}", marketVO);

            // 상품 저장
            log.info("DB 저장 시작");
            int result = marketMapper.insertProduct(marketVO);
            log.info("DB 저장 결과: {}", result);

            if (result <= 0) {
                log.error("상품 저장 실패: {}", requestDto.getName());
                return ProductAddResponseDto.failure("상품 저장에 실패했습니다.");
            }

            // 4) 🔔 이벤트 발행 (커밋 후 FCM 발송 트리거)
            publisher.publishEvent(new ProductCreatedEvent(
                    marketVO.getProductId(),
                    marketVO.getInfluencerId(),
                    marketVO.getName(),
                    marketVO.getThumbnailImage()
            ));

            log.info("상품이 성공적으로 등록되었습니다. ID: {}, Name: {}",
                    marketVO.getProductId(), requestDto.getName());

            return ProductAddResponseDto.success(
                    marketVO.getProductId(),
                    requestDto.getName(),
                    "상품이 성공적으로 등록되었습니다."
            );

        } catch (ProductException e) {
            log.warn("상품 등록 검증 실패: {}", e.getMessage());
            return ProductAddResponseDto.failure(e.getMessage());
        } catch (Exception e) {
            log.error("상품 등록 중 오류 발생 - 상세 정보: ", e);
            return ProductAddResponseDto.failure("상품 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    @Override
    public ProductAddResponseDto addProductByMe(Long userId, ProductAddRequestDto requestDto) {
        // 지금은 DTO에 influencerId가 들어오므로 그대로 재사용
        // (나중에 userId -> influencerId 조회로 바꿔도 됨)
        return addProduct(requestDto);
    }


    private MarketVO convertToMarketVO(ProductAddRequestDto requestDto) {

        try {
            // 이미지 리스트를 JSON 문자열로 변환
            String detailImagesJson = null;
            String descriptionImagesJson = null;
            String categoriesJson = null;

            if (requestDto.getDetailImages() != null && !requestDto.getDetailImages().isEmpty()) {
                detailImagesJson = objectMapper.writeValueAsString(requestDto.getDetailImages());
            }

            if (requestDto.getDescriptionImages() != null && !requestDto.getDescriptionImages().isEmpty()) {
                descriptionImagesJson = objectMapper.writeValueAsString(requestDto.getDescriptionImages());
            }

            if(requestDto.getCategories() != null && !requestDto.getCategories().isEmpty()) {
                categoriesJson = objectMapper.writeValueAsString(requestDto.getCategories());
            }

            // 등급별 오픈 시간
            var general = requestDto.getGeneralOpenTime();
            var white  = (requestDto.getWhiteOpenTime()  != null) ? requestDto.getWhiteOpenTime()
                    : general;
            var silver = (requestDto.getSilverOpenTime() != null) ? requestDto.getSilverOpenTime()
                    : (general != null ? general.minusHours(1) : null);
            var gold   = (requestDto.getGoldOpenTime()   != null) ? requestDto.getGoldOpenTime()
                    : (general != null ? general.minusHours(2) : null);
            var vip    = (requestDto.getVipOpenTime()    != null) ? requestDto.getVipOpenTime()
                    : (general != null ? general.minusHours(3) : null);

            return MarketVO.builder()
                    .influencerId(requestDto.getInfluencerId())
                    .name(requestDto.getName())
                    .description(requestDto.getDescription())
                    .price(requestDto.getPrice())
                    .groupBuyPrice(requestDto.getGroupBuyPrice())
                    .discountedPrice(requestDto.getDiscountedPrice())
                    .shippingPrice(requestDto.getShippingPrice())
                    .stock(requestDto.getStock())
                    .thumbnailImage(requestDto.getThumbnailImage())
                    .detailImages(detailImagesJson)
                    .descriptionImages(descriptionImagesJson)
                    // 오픈 시간
                    .whiteOpenTime(white)
                    .silverOpenTime(silver)
                    .goldOpenTime(gold)
                    .vipOpenTime(vip)
                    .generalOpenTime(requestDto.getGeneralOpenTime())
                    .categories(categoriesJson)
                    .build();

        } catch (Exception e) {
            log.error("DTO 변환 중 오류 발생", e);
            throw new RuntimeException("상품 데이터 변환 중 오류가 발생했습니다.", e);
        }
    }

}
