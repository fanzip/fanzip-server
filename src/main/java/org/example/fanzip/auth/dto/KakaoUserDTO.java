package org.example.fanzip.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.fanzip.user.dto.enums.UserRole;

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

    @JsonIgnore
    private UserRole role;

    private boolean isRegistered;
}
