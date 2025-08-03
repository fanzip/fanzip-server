package org.example.fanzip.cart.mapper;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.example.fanzip.cart.dto.CartItemDto;
import org.example.fanzip.global.config.RootConfig;
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
@Transactional
@Slf4j
class CartMapperTest {
    @Autowired
    private CartMapper cartMapper;

    @Test
    void findCartIdByUserId() {
        Long cart = cartMapper.findCartIdByUserId(1L);
        assertNotNull(cart);
        log.info("=========> findCartIdByUserId: {}", cart.toString());
    }

    @Test
    void createCart() {
        Long cart = cartMapper.createCart(2L);
        assertNotNull(cart);
        log.info("createCart: {}", cart.toString());
    }

    @Test
    void insertCartItem() {
        Long cart = cartMapper.insertCartItem(1L, 30L, 3);
        assertNotNull(cart);
        log.info("insertCartItem: {}", cart.toString());
    }

    @Test
    void findItemsByUser() {
        List<CartItemDto> list = cartMapper.findItemsByUser(2L);
        assertNotNull(list);
        for (CartItemDto cartItemDto : list) {
            log.info("============> findItemsByUser: {}", cartItemDto.toString());
        }
    }

    @Test
    void findItemById() {
        CartItemDto cartItemDto = cartMapper.findItemById(1L);
        assertNotNull(cartItemDto);
        log.info("===========> findItemById: {}", cartItemDto.toString());
    }

    @Test
    void checkCartItem() {
        Integer cart = cartMapper.checkCartItem(2L, 2L);
        log.info("===============> checkCartItem: {}", cart.toString());
    }

    @Test
    void findCartItemIdByProductId() {
        Long cartItemId = cartMapper.findCartItemIdByProductId(1L, 6L);
        assertNotNull(cartItemId);
        log.info("===============> findCartItemIdByProductId: {}", cartItemId.toString());
    }

    @Test
    void updateCartItem() {
        cartMapper.updateCartItem(1L, 12, true);
        List<CartItemDto> list = cartMapper.findItemsByUser(2L);
        assertNotNull(list);
        for (CartItemDto cartItemDto : list) {
            log.info("============> updated: {}", cartItemDto.toString());
        }
    }

    @Test
    void updateAllSelection() {
        cartMapper.updateAllSelection(2L, false);
        List<CartItemDto> list = cartMapper.findItemsByUser(2L);
        assertNotNull(list);
        for (CartItemDto cartItemDto : list) {
            log.info("============> updateAllSelection: {}", cartItemDto.toString());
        }
    }

    @Test
    void deleteCartItem() {
        cartMapper.deleteCartItem(11L);
        List<CartItemDto> list = cartMapper.findItemsByUser(1L);
        assertNotNull(list);
        for (CartItemDto cartItemDto : list) {
            log.info("============> deleteCartItem: {}", cartItemDto.toString());
        }
    }

    @Test
    void shippingAddress() {
        String address = cartMapper.shippingAddress(1L);
        assertNotNull(address);
        log.info("===============> shippingAddress: {}", address);
    }
}