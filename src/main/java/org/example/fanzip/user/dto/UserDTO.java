package org.example.fanzip.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDTO {
    private Long userId;
    private String email;
    private String name;

    private String phone;

    private String socialType;
    private String socialId;

    private String address1;
    private String address2;
    private String zipcode;
    private String recipientName;
    private String recipientPhone;

    private Date created_at;
    private Date updated_at;
    private Date deleted_at;
}
