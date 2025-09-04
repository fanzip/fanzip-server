package org.example.fanzip.market.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.fanzip.market.domain.enums.ProductCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAddRequestDto {
    
    private Long influencerId;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal groupBuyPrice;
    private BigDecimal discountedPrice;
    private BigDecimal shippingPrice;
    private Integer stock;
    private String thumbnailImage;

    // 카테고리 리스트 (JSON으로 저장될 예정)
    private List<ProductCategory> categories;

    // 상세 이미지 리스트 (JSON으로 저장될 예정)
    private List<String> detailImages;
    
    // 설명 이미지 리스트 (JSON으로 저장될 예정)
    private List<String> descriptionImages;
    
    // 등급별 판매 오픈 시간
    private LocalDateTime generalOpenTime;

    /** 서버 계산용 – 클라이언트 입력 무시 */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime whiteOpenTime;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime silverOpenTime;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime goldOpenTime;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime vipOpenTime;
}