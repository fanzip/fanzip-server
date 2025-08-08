package org.example.fanzip.fancard.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.example.fanzip.fancard.dto.request.QrCodeRequest;
import org.example.fanzip.fancard.dto.response.*;
import org.example.fanzip.fancard.service.FancardService;
import org.example.fanzip.security.JwtProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Api(tags = "Fancard API", description = "팬카드 관리 API")
@RestController
@RequestMapping("/api/fancards")
public class FancardController {
    
    private final FancardService fancardService;
    private final JwtProcessor jwtProcessor;
    
    @Autowired
    public FancardController(FancardService fancardService, JwtProcessor jwtProcessor) {
        this.fancardService = fancardService;
        this.jwtProcessor = jwtProcessor;
    }
    
    @ApiOperation(value = "사용자 팬카드 목록 조회", notes = "로그인한 사용자의 팬카드 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<FancardListWrapper> getUserFancards(HttpServletRequest request) {
        Long userId = extractUserIdFromJWT(request);
        FancardListWrapper fancards = fancardService.getUserFancards(userId);
        return ResponseEntity.ok(fancards);
    }

    @ApiOperation(value = "팬카드 상세 조회", notes = "특정 팬카드의 상세 정보를 조회합니다.")
    @GetMapping("/{cardId}")
    public ResponseEntity<FancardDetailResponse> getFancardDetail(
            @ApiParam(value = "팬카드 ID", required = true) @PathVariable Long cardId) {
        FancardDetailResponse fancard = fancardService.getFancardDetail(cardId);
        return ResponseEntity.ok(fancard);
    }
    
    @ApiOperation(value = "사용자 팬미팅 예약 목록 조회", notes = "로그인한 사용자의 팬미팅 예약 목록을 조회합니다.")
    @GetMapping("/reservations")
    public ResponseEntity<?> getUserReservations(HttpServletRequest request) {
        Long userId = extractUserIdFromJWT(request);
        // TODO: 실제 예약 서비스 호출 구현
        // 현재는 테스트용 간단 응답 반환
        return ResponseEntity.ok(java.util.Collections.emptyMap());
    }
    
    @ApiOperation(value = "입장 QR 코드 생성", notes = "팬미팅 입장용 QR 코드를 생성합니다. 행사장 범위 내에서만 생성 가능합니다.")
    @PostMapping("/qr")
    public ResponseEntity<QrCodeResponse> generateQrCode(
            @ApiParam(value = "QR 코드 생성 요청 정보", required = true) @RequestBody QrCodeRequest request) {
        QrCodeResponse qrCode = fancardService.generateQrCode(request);
        return ResponseEntity.ok(qrCode);
    }
    
    private Long extractUserIdFromJWT(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtProcessor.getUserIdFromToken(token);
        }
        throw new RuntimeException("JWT 토큰이 없거나 유효하지 않습니다.");
    }
}
