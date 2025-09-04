package org.example.fanzip.fancard.controller;

import io.swagger.annotations.*;
import org.example.fanzip.fancard.dto.request.QrCodeRequest;
import org.example.fanzip.fancard.dto.request.QrCodeValidationRequest;
import org.example.fanzip.fancard.dto.response.*;
import org.example.fanzip.fancard.service.FancardService;
import org.example.fanzip.security.JwtProcessor;
import org.example.fanzip.user.service.UserService;
import org.example.fanzip.user.dto.UserDTO;
import org.example.fanzip.user.dto.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Api(tags = "Fancard", description = "팬카드 및 QR코드 관리 API")
@RestController
@RequestMapping("/api/fancards")
public class FancardController {
    
    private final FancardService fancardService;
    private final JwtProcessor jwtProcessor;
    private final UserService userService;
    
    @Autowired
    public FancardController(FancardService fancardService, JwtProcessor jwtProcessor, UserService userService) {
        this.fancardService = fancardService;
        this.jwtProcessor = jwtProcessor;
        this.userService = userService;
    }
    
    @ApiOperation(value = "사용자 팬카드 목록 조회", notes = "로그인한 사용자의 팬카드 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "팬카드 목록 조회 성공"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<FancardListWrapper> getUserFancards(HttpServletRequest request) {
        Long userId = extractUserIdFromJWT(request);
        FancardListWrapper fancards = fancardService.getUserFancards(userId);
        return ResponseEntity.ok(fancards);
    }

    @ApiOperation(value = "팬카드 상세 조회", notes = "특정 팬카드의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "팬카드 상세 정보 조회 성공"),
            @ApiResponse(code = 404, message = "팬카드를 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/{cardId}")
    public ResponseEntity<FancardDetailResponse> getFancardDetail(
            @ApiParam(value = "팬카드 ID", required = true, example = "1") @PathVariable Long cardId) {
        FancardDetailResponse fancard = fancardService.getFancardDetail(cardId);
        return ResponseEntity.ok(fancard);
    }
    
    @ApiOperation(value = "사용자 팬미팅 예약 목록 조회", notes = "로그인한 사용자의 팬미팅 예약 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "예약 목록 조회 성공"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/reservations")
    public ResponseEntity<?> getUserReservations(HttpServletRequest request) {
        Long userId = extractUserIdFromJWT(request);
        // TODO: 실제 예약 서비스 호출 구현
        // 현재는 테스트용 간단 응답 반환
        return ResponseEntity.ok(java.util.Collections.emptyMap());
    }
    
    @ApiOperation(value = "모바일 티켓 조회", notes = "특정 예약에 대한 모바일 티켓 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "모바일 티켓 조회 성공"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 404, message = "예약 또는 티켓을 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/mobile-ticket/{reservationId}/{seatId}/{meetingId}")
    public ResponseEntity<QrCodeResponse> getMobileTicket(
            @ApiParam(value = "예약 ID", required = true, example = "1") @PathVariable Long reservationId,
            @ApiParam(value = "좌석 ID", required = true, example = "1") @PathVariable Long seatId,
            @ApiParam(value = "팬미팅 ID", required = true, example = "1") @PathVariable Long meetingId,
            HttpServletRequest request) {
        Long userId = extractUserIdFromJWT(request);
        QrCodeResponse mobileTicket = fancardService.getMobileTicketData(userId, reservationId, seatId, meetingId);
        return ResponseEntity.ok(mobileTicket);
    }
    
    @ApiOperation(value = "입장 QR 코드 생성", notes = "팬미팅 입장용 QR 코드를 생성합니다. 행사장 범위 내에서만 생성 가능합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "QR 코드 생성 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청 데이터 또는 행사장 범위 밖"),
            @ApiResponse(code = 404, message = "예약 또는 티켓을 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @PostMapping("/qr")
    public ResponseEntity<QrCodeResponse> generateQrCode(
            @ApiParam(value = "QR 코드 생성 요청 정보", required = true) @RequestBody QrCodeRequest request) {
        QrCodeResponse qrCode = fancardService.generateQrCode(request);
        return ResponseEntity.ok(qrCode);
    }
    
    @ApiOperation(value = "QR 코드 검증", notes = "QR 코드를 검증합니다. 유효한 경우 입장 허용 메시지를 반환합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "QR 코드 검증 성공"),
            @ApiResponse(code = 400, message = "잘못된 QR 코드 또는 만료된 코드"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 404, message = "예약 또는 티켓을 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @PostMapping("/qr/validate")
    public ResponseEntity<QrCodeValidationResponse> validateQrCode(
            @ApiParam(value = "QR 코드 검증 요청 정보", required = true) @RequestBody QrCodeValidationRequest request,
            HttpServletRequest httpRequest) {
        
        // 사용자 ID 추출 (인증 확인용)
        Long userId = extractUserIdFromJWT(httpRequest);
        
        // QR 코드 검증 수행
        QrCodeValidationResponse validationResult = fancardService.validateQrCode(request);
        return ResponseEntity.ok(validationResult);
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
