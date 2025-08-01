package org.example.fanzip.security;

import lombok.RequiredArgsConstructor;

import java.security.Principal;


@RequiredArgsConstructor
public class CustomUserPrincipal implements Principal {

    private final Long userId;

    public Long getUserId() {
        return userId;
    }

    @Override
    public String getName() {
        return userId.toString();//username 대체용
    }
}
