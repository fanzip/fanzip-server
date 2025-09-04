package org.example.fanzip.market.service;

import lombok.extern.slf4j.Slf4j;
import org.example.fanzip.global.config.RootConfig;
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
@ContextConfiguration(classes = {RootConfig.class})
@Slf4j
@Transactional
class MarketServiceImplTest {
    @Autowired
    private MarketService marketService;

    @Test
    void getProducts() {
        List<ProductListDto> list = marketService.getProducts(1L,60L, 10, "latest", "FOOD", false);
        for (ProductListDto dto : list) {
            log.info(dto.toString());
        }
    }

    @Test
    void getProductDetail() {
        ProductDetailDto dto = marketService.getProductDetail(1L, 1L);
        log.info("==========> getProductDetail()" + dto.toString());
    }

    @Test
    void searchProducts() {
        List<ProductListDto> list = marketService.searchProducts(1L,"상품", 30L, 20, "priceAsc", "FOOD");
        for (ProductListDto dto : list) {
            log.info(dto.toString());
        }
    }
}