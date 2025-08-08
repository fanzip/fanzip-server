package org.example.fanzip.membership.controller;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.membership.dto.MembershipGradeDTO;
import org.example.fanzip.membership.dto.MembershipSubscribeRequestDTO;
import org.example.fanzip.membership.dto.MembershipSubscribeResponseDTO;
import org.example.fanzip.membership.service.MembershipService;
import org.example.fanzip.security.JwtProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // ✅ 등급/금액 목록 조회 API
    @GetMapping("/grades")
    public ResponseEntity<List<MembershipGradeDTO>> getMembershipGrades() {
        return ResponseEntity.ok(membershipService.getMembershipGrades());
    }
}
