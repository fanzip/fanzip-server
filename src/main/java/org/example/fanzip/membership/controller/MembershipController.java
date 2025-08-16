package org.example.fanzip.membership.controller;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.membership.dto.MembershipGradeDTO;
import org.example.fanzip.membership.dto.MembershipSubscribeRequestDTO;
import org.example.fanzip.membership.dto.MembershipSubscribeResponseDTO;
import org.example.fanzip.membership.dto.UserMembershipInfoDTO;
import org.example.fanzip.membership.service.MembershipService;
import org.example.fanzip.security.CustomUserPrincipal;
import org.example.fanzip.security.JwtProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    @GetMapping("/grades")
    public ResponseEntity<List<MembershipGradeDTO>> getMembershipGrades() {
        return ResponseEntity.ok(membershipService.getMembershipGrades());
    }

    @GetMapping("/my-info")
    public ResponseEntity<UserMembershipInfoDTO> getUserMembershipInfo(
            Authentication authentication
    ) {
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();

        UserMembershipInfoDTO userInfo = membershipService.getUserMembershipInfo(userId);
        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/subscription/{influencerId}")
    public ResponseEntity<UserMembershipInfoDTO.UserMembershipSubscriptionDTO> getUserSubscriptionByInfluencer(
            @PathVariable Long influencerId,
            Authentication authentication
    ) {
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();

        UserMembershipInfoDTO.UserMembershipSubscriptionDTO subscription =
                membershipService.getUserSubscriptionByInfluencer(userId, influencerId);
        return ResponseEntity.ok(subscription);
    }
    
    @DeleteMapping("/{membershipId}/cancel")
    public ResponseEntity<String> cancelMembership(
            @PathVariable Long membershipId,
            Authentication authentication
    ) {
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();
        
        boolean success = membershipService.cancelMembership(membershipId, userId);
        
        if (success) {
            return ResponseEntity.ok("구독이 취소되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("구독 취소에 실패했습니다.");
        }
    }

}
