package org.example.fanzip.cart.domain;

import org.example.fanzip.cart.dto.CartItemDto;

import java.math.BigDecimal;
import java.util.List;

public class CartVO {
    // products
    private Long productId;
    private String productName;
    private String thumbnailImage;
    private BigDecimal discountedPrice;
    private BigDecimal unitPrice; // 정가
    private Long influencerId;
    private BigDecimal shippingPrice;

    // Influencers
    private String influencerName;

    // cart_itmes
    private int quantity;
    private Boolean isSelected;
    private Long cartItemId;

    // users - 유저 정보 (배송관련)
    private String address1;
    private String address2;
    private String zipcode;
    private String name;
    private String phone;

    private BigDecimal totalPrice;
    private BigDecimal grandTotal;
}

