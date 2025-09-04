package org.example.fanzip.market.util;

import org.example.fanzip.market.dto.ProductAddRequestDto;
import org.example.fanzip.market.exception.MarketErrorCode;
import org.example.fanzip.market.exception.ProductException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

public class ProductValidationUtils {
    
    private static final Pattern URL_PATTERN = Pattern.compile(
        "^(https?://)?[\\w.-]+(\\.[\\w.-]+)+[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=]*$"
    );
    
    private static final int MAX_NAME_LENGTH = 255;
    private static final int MAX_DESCRIPTION_LENGTH = 2000;
    private static final BigDecimal MAX_PRICE = new BigDecimal("99999999.99");
    private static final int MAX_STOCK = 999999;
    private static final int MAX_IMAGES = 10;
    
    public static void validateProductAddRequest(ProductAddRequestDto request) {
        validateBasicInfo(request);
        validatePricing(request);
        validateImages(request);
        validateSaleTiming(request);
    }
    
    private static void validateBasicInfo(ProductAddRequestDto request) {
        // 인플루언서 ID 검증
        if (request.getInfluencerId() == null || request.getInfluencerId() <= 0) {
            throw new ProductException(MarketErrorCode.INFLUENCER_NOT_FOUND);
        }
        
        // 상품명 검증
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ProductException(MarketErrorCode.INVALID_PRODUCT_NAME);
        }
        
        if (request.getName().length() > MAX_NAME_LENGTH) {
            throw new ProductException(MarketErrorCode.INVALID_PRODUCT_NAME);
        }
        
        // 설명 검증
        if (request.getDescription() != null && request.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            throw new ProductException(MarketErrorCode.INVALID_PRODUCT_NAME);
        }
        
        // 재고 검증
        if (request.getStock() == null || request.getStock() < 0) {
            throw new ProductException(MarketErrorCode.INVALID_STOCK_QUANTITY);
        }
        
        if (request.getStock() > MAX_STOCK) {
            throw new ProductException(MarketErrorCode.INVALID_STOCK_QUANTITY);
        }
    }
    
    private static void validatePricing(ProductAddRequestDto request) {
        // 가격 검증
        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ProductException(MarketErrorCode.INVALID_PRODUCT_PRICE);
        }
        
        if (request.getPrice().compareTo(MAX_PRICE) > 0) {
            throw new ProductException(MarketErrorCode.INVALID_PRODUCT_PRICE);
        }
        
        // 할인 가격 검증
        if (request.getDiscountedPrice() != null) {
            if (request.getDiscountedPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new ProductException(MarketErrorCode.INVALID_DISCOUNT_PRICE);
            }
            
            if (request.getDiscountedPrice().compareTo(request.getPrice()) > 0) {
                throw new ProductException(MarketErrorCode.INVALID_DISCOUNT_PRICE);
            }
        }
        
        // 배송비 검증
        if (request.getShippingPrice() == null || request.getShippingPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new ProductException(MarketErrorCode.INVALID_SHIPPING_PRICE);
        }
        
        if (request.getShippingPrice().compareTo(MAX_PRICE) > 0) {
            throw new ProductException(MarketErrorCode.INVALID_SHIPPING_PRICE);
        }
    }
    
    private static void validateImages(ProductAddRequestDto request) {
        // 썸네일 이미지 검증
        if (request.getThumbnailImage() != null && !request.getThumbnailImage().trim().isEmpty()) {
            if (!isValidImageUrl(request.getThumbnailImage())) {
                throw new ProductException(MarketErrorCode.INVALID_IMAGE_URL);
            }
        }
        
        // 상세 이미지 검증
        validateImageList(request.getDetailImages(), "상세 이미지");
        
        // 설명 이미지 검증
        validateImageList(request.getDescriptionImages(), "설명 이미지");
    }
    
    private static void validateImageList(List<String> images, String imageType) {
        if (images == null) return;
        
        if (images.size() > MAX_IMAGES) {
            throw new ProductException(MarketErrorCode.INVALID_IMAGE_URL);
        }
        
        for (String imageUrl : images) {
            if (imageUrl != null && !imageUrl.trim().isEmpty() && !isValidImageUrl(imageUrl)) {
                throw new ProductException(MarketErrorCode.INVALID_IMAGE_URL);
            }
        }
    }
    
    private static void validateSaleTiming(ProductAddRequestDto request) {
        LocalDateTime now = LocalDateTime.now();
        
        validateSaleTime(request.getWhiteOpenTime(), "WHITE 등급", now);
        validateSaleTime(request.getSilverOpenTime(), "SILVER 등급", now);
        validateSaleTime(request.getGoldOpenTime(), "GOLD 등급", now);
        validateSaleTime(request.getVipOpenTime(), "VIP 등급", now);
        validateSaleTime(request.getGeneralOpenTime(), "일반", now);
        
        // 등급별 시간 순서 검증 (선택사항)
        validateTimeOrder(request);
    }
    
    private static void validateSaleTime(LocalDateTime saleTime, String gradeType, LocalDateTime now) {
        if (saleTime != null && saleTime.isBefore(now.minusMinutes(5))) {
            // 5분 전까지는 허용 (시간 차이 고려)
            throw new ProductException(MarketErrorCode.PAST_SALE_TIME);
        }
    }
    
    private static void validateTimeOrder(ProductAddRequestDto request) {
        // VIP -> GOLD -> SILVER -> WHITE -> GENERAL 순서로 시간이 빠른지 검증
        LocalDateTime[] times = {
            request.getVipOpenTime(),
            request.getGoldOpenTime(),
            request.getSilverOpenTime(),
            request.getWhiteOpenTime(),
            request.getGeneralOpenTime()
        };
        
        String[] grades = {"VIP", "GOLD", "SILVER", "WHITE", "GENERAL"};
        
        for (int i = 0; i < times.length - 1; i++) {
            if (times[i] != null && times[i + 1] != null) {
                if (!times[i].isBefore(times[i + 1])) {
                    throw new ProductException(MarketErrorCode.INVALID_SALE_TIME);
                }
            }
        }
    }
    
    private static boolean isValidImageUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        
        // 기본 URL 패턴 검증
        if (!URL_PATTERN.matcher(url).matches()) {
            return false;
        }
        
        // 이미지 파일 확장자 검증
        String lowerUrl = url.toLowerCase();
        return lowerUrl.contains(".jpg") || 
               lowerUrl.contains(".jpeg") || 
               lowerUrl.contains(".png") || 
               lowerUrl.contains(".gif") || 
               lowerUrl.contains(".webp") ||
               lowerUrl.contains(".svg");
    }
}