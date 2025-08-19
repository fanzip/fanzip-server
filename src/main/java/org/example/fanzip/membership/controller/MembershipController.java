package org.example.fanzip.membership.controller;

import io.swagger.annotations.*;
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

@Api(tags = "Membership", description = "멤버십 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/memberships")
public class MembershipController {

    private final MembershipService membershipService;
    private final JwtProcessor jwtProcessor;

    @ApiOperation(value = "멤버십 구독", notes = "인플루언서의 멤버십에 구독합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "멤버십 구독 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청 데이터 또는 이미 구독 중"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 404, message = "인플루언서 또는 멤버십 등급을 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @PostMapping("/subscribe")
    public ResponseEntity<MembershipSubscribeResponseDTO> subscribe(
            @RequestHeader("Authorization") String authorizationHeader,
            @ApiParam(value = "멤버십 구독 요청 데이터", required = true)
            @RequestBody MembershipSubscribeRequestDTO requestDTO
    ) {
        String token = authorizationHeader.substring(7);
        Long userId = jwtProcessor.getUserIdFromToken(token);

        MembershipSubscribeResponseDTO responseDTO =
                membershipService.subscribe(requestDTO, userId);

        return ResponseEntity.ok(responseDTO);
    }

    @ApiOperation(value = "멤버십 등급 목록 조회", notes = "사용 가능한 모든 멤버십 등급 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "멤버십 등급 목록 조회 성공"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/grades")
    public ResponseEntity<List<MembershipGradeDTO>> getMembershipGrades() {
        return ResponseEntity.ok(membershipService.getMembershipGrades());
    }

    @ApiOperation(value = "내 멤버십 정보 조회", notes = "사용자의 모든 멤버십 구독 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "사용자 멤버십 정보 조회 성공"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/my-info")
    public ResponseEntity<UserMembershipInfoDTO> getUserMembershipInfo(
            Authentication authentication
    ) {
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();

        UserMembershipInfoDTO userInfo = membershipService.getUserMembershipInfo(userId);
        return ResponseEntity.ok(userInfo);
    }

    @ApiOperation(value = "특정 인플루언서 멤버십 구독 정보 조회", notes = "지정된 인플루언서에 대한 사용자의 멤버십 구독 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "인플루언서 멤버십 구독 정보 조회 성공"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 404, message = "인플루언서를 찾을 수 없음 또는 구독 내역 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/subscription/{influencerId}")
    public ResponseEntity<UserMembershipInfoDTO.UserMembershipSubscriptionDTO> getUserSubscriptionByInfluencer(
            @ApiParam(value = "인플루언서 ID", required = true, example = "1")
            @PathVariable Long influencerId,
            Authentication authentication
    ) {
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();

        UserMembershipInfoDTO.UserMembershipSubscriptionDTO subscription =
                membershipService.getUserSubscriptionByInfluencer(userId, influencerId);
        return ResponseEntity.ok(subscription);
    }
    
    @ApiOperation(value = "멤버십 구독 취소", notes = "지정된 멤버십 구독을 취소합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "멤버십 구독 취소 성공"),
            @ApiResponse(code = 400, message = "구독 취소 실패"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 404, message = "멤버십을 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @DeleteMapping("/{membershipId}/cancel")
    public ResponseEntity<String> cancelMembership(
            @ApiParam(value = "멤버십 ID", required = true, example = "1")
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
