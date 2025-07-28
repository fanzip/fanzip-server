package org.example.fanzip.user.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

public class RegisterDTO {
    private String socialType;
    private String socialId;
    private String email;
    private String name;
    private String phone;
}
