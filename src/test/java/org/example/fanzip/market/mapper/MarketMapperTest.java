package org.example.fanzip.market.mapper;

import lombok.extern.slf4j.Slf4j;
import org.example.fanzip.global.config.RedisConfig;
import org.example.fanzip.global.config.RootConfig;
import org.example.fanzip.global.config.YamlPropertySourceFactory;
import org.example.fanzip.market.dto.ProductDetailDto;
import org.example.fanzip.market.dto.ProductListDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class, RedisConfig.class, YamlPropertySourceFactory.class})
@Transactional
@Slf4j
class MarketMapperTest {
    @Autowired
    private MarketMapper marketMapper;

    @Test
    void getProducts() {
        List<ProductListDto> list = marketMapper.getProducts(1L,null, 20, "priceAsc", "FOOD", false);
        assertNotNull(list, "상품 목록 not null");
        for(ProductListDto dto : list) {
            assertNotNull(dto.getProductId(), "productID not null");
            assertNotNull(dto.getName(), "상품명 not null");
            log.info("=========getProducts=============: {}", dto);
        }
    }

    @Test
    void findProductById() {
        ProductDetailDto dto = marketMapper.findProductById(1L, 1L);
        assertNotNull(dto, "해당 상품 없음");
        log.info("=========> findProductById: {}", dto);
    }


    @Test
    void searchProducts() {
        List<ProductListDto> list = marketMapper.searchProducts(1L,"상품", 20L, 8);
        for(ProductListDto dto : list) {
            assertNotNull(dto.getProductId(), "productID not null");
            log.info("searchProductsAfter: {}", dto);
        }

    }

}