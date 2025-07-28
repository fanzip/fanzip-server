package org.example.fanzip.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class KakaoUserDTO {
    private String socialType;
    private String socialId;
    private String email;

    @JsonIgnore
    private Long userId;

    private boolean isRegistered;
}
