package org.example.fanzip.cart.service;

import lombok.extern.slf4j.Slf4j;
import org.example.fanzip.cart.dto.*;
import org.example.fanzip.global.config.RootConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class})
@Slf4j
@Transactional
class CartServiceImplTest {
    @Autowired
    CartService cartService;

    @Test
    void addItem() {
        AddCartItemRequestDto req = new AddCartItemRequestDto();
        req.setProductId(7L);
        req.setQuantity(7);
        CartItemResponseDto dto = cartService.addItem(1L, req);
        log.info("==========> addItem: " + dto);
    }

    @Test
    void getCart() {
        CartResponseDto dto = cartService.getCart(1L);
        log.info("==========> getCart: " + dto);
    }

    @Test
    void updateItem() {
        UpdateCartItemRequestDto req = new UpdateCartItemRequestDto();
        req.setQuantity(2);
        req.setIsSelected(true);
        CartItemDto dto = cartService.updateItem(1L, 12L, req);
        log.info("==========> updateItem: " + dto);
    }

    @Test
    void selectAll() {
        CartResponseDto dto = cartService.selectAll(1L, true);
        log.info("==========> selectAll: " + dto);
    }

    @Test
    void removeItem() {
        cartService.removeItem(1L, 11L);
    }
}