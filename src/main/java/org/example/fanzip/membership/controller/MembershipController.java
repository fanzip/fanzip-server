package org.example.fanzip.membership.controller;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.membership.dto.MembershipSubscribeRequestDTO;
import org.example.fanzip.membership.dto.MembershipSubscribeResponseDTO;
import org.example.fanzip.membership.service.MembershipService;
import org.example.fanzip.security.JwtProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/memberships")
public class MembershipController {

    private final MembershipService membershipService;
    private final JwtProcessor jwtProcessor;

    @PostMapping("/subscribe")
    public ResponseEntity<MembershipSubscribeResponseDTO> subscribe(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody MembershipSubscribeRequestDTO requestDTO
    ) {
        String token = authorizationHeader.substring(7);
        Long userId = jwtProcessor.getUserIdFromToken(token);

        MembershipSubscribeResponseDTO responseDTO =
                membershipService.subscribe(requestDTO, userId);

        return ResponseEntity.ok(responseDTO);
    }
}
