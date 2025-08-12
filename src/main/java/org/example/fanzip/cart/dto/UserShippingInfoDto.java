package org.example.fanzip.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserShippingInfoDto {
    // 유저 정보 (배송관련)
    private String address1;
    private String address2;
    private String zipcode;
    private String name;
    private String phone;
}
