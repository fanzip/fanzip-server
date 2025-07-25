package org.example.fanzip.market.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.example.fanzip.market.dto.ProductDetailDto;
import org.example.fanzip.market.dto.ProductListDto;
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
    public List<ProductListDto> getAllProducts(int limit) {
        return marketMapper.getAllProducts(limit);
    }

    @Override
    public List<ProductListDto> getProductsAfter(Long lastProductId, int limit) {
        return (lastProductId == null)
                ? marketMapper.getAllProducts(limit)
                : marketMapper.getProductsAfter(lastProductId, limit);
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
        ObjectMapper objectMapper = new ObjectMapper();

        List<String> parsedDetailImages;
        try{
            parsedDetailImages = objectMapper.readValue(
                    detail.getDetailImages(),
                    new TypeReference<List<String>>() {}
            );
        } catch (Exception e) {
            parsedDetailImages = Collections.emptyList();
        }
        detail.setDetailImagesList(parsedDetailImages);

        // JSON -> List (설명 이미지 목록)
        List<String> parsedDescriptionImages;
        try{
            parsedDescriptionImages = objectMapper.readValue(
                    detail.getDescriptionImages(),
                    new TypeReference<List<String>>() {}
            );
        } catch (Exception e) {
            parsedDescriptionImages = Collections.emptyList();
        }
        detail.setDescriptionImagesList(parsedDescriptionImages);

        return detail;
    }

    @Override
    public List<ProductListDto> searchProducts(String keyword, Long lastProductId, int limit) {
        if(lastProductId == null) {
            return marketMapper.searchProducts(keyword, limit);
        }
        return marketMapper.searchProductsAfter(keyword, lastProductId, limit);
    }

}
