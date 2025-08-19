package org.example.fanzip.market.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.example.fanzip.market.dto.ProductAddRequestDto;
import org.example.fanzip.market.dto.ProductAddResponseDto;
import org.example.fanzip.market.dto.ProductDetailDto;
import org.example.fanzip.market.dto.ProductListDto;
import org.example.fanzip.market.service.MarketService;
import org.example.fanzip.security.CustomUserPrincipal;
import org.example.fanzip.security.JwtProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
@Api(tags = "Market", description = "마켓 상품 관리 API")
@Slf4j
@RestController
@RequestMapping("/api/market")
public class MarketController {

    private final MarketService marketService;

    @Autowired
    public MarketController(MarketService marketService, JwtProcessor jwtProcessor) {
        this.marketService = marketService;
    }

    @ApiOperation(value = "상품 목록 조회", notes = "마켓의 상품 목록을 조회합니다. 검색, 정렬, 카테고리 필터링, 구독 인플루언서 상품만 보기 기능을 제공합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "상품 목록 조회 성공"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/products")
    public List<ProductListDto> getProducts(
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
            @ApiParam(value = "페이지당 상품 개수", example = "20")
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @ApiParam(value = "마지막 상품 ID (페이지네이션용)", example = "100")
            @RequestParam(value = "lastProductId", required = false) Long lastProductId,
            @ApiParam(value = "검색 키워드", example = "코스메틱")
            @RequestParam(value = "q", required = false) String keyword,
            @ApiParam(value = "정렬 방식 (recommended, newest, price_low, price_high)", example = "recommended")
            @RequestParam(value="sort", defaultValue = "recommended") String sort,
            @ApiParam(value = "카테고리", example = "BEAUTY")
            @RequestParam(value = "category", required = false) String category,
            @ApiParam(value = "구독한 인플루언서 상품만 보기", example = "false")
            @RequestParam(value = "onlySubscribed", required = false, defaultValue = "false") boolean onlySubscribed
    ) {
        Long userId = customUserPrincipal.getUserId();

        if(keyword != null && !keyword.isBlank()) {
            return marketService.searchProducts(userId, keyword, lastProductId, limit, sort, category);
        }

        return marketService.getProducts(userId, lastProductId, limit, sort, category, onlySubscribed);
    }

    @ApiOperation(value = "상품 상세 정보 조회", notes = "특정 상품의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "상품 상세 정보 조회 성공"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 404, message = "상품을 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductDetailDto> getProductDetail(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @ApiParam(value = "상품 ID", required = true, example = "1")
            @PathVariable Long productId
    ) {
        Long userId = principal.getUserId();
        ProductDetailDto dto = marketService.getProductDetail(userId, productId);
        return ResponseEntity.ok(dto);
    }

    @ApiOperation(value = "상품 등록", notes = "새로운 상품을 마켓에 등록합니다. 인플루언서만 등록 가능합니다.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "상품 등록 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청 데이터"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 403, message = "권한 없음 (인플루언서만 등록 가능)"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @PostMapping("/products")
    public ResponseEntity<ProductAddResponseDto> addProduct(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @ApiParam(value = "상품 등록 요청 데이터", required = true)
            @RequestBody ProductAddRequestDto requestDto
    ) {
        try {
            // 현재 사용자 정보 확인
            Long userId = principal.getUserId();
            log.info("상품 추가 요청 - 사용자 ID: {}, 요청 데이터: {}", userId, requestDto);
            
            // 상품 추가 서비스 호출
            ProductAddResponseDto response = marketService.addProduct(requestDto);
            
            if (response.isSuccess()) {
                log.info("상품 추가 성공 - 상품 ID: {}", response.getProductId());
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                log.warn("상품 추가 실패: {}", response.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
        } catch (Exception e) {
            log.error("상품 추가 중 예외 발생", e);
            ProductAddResponseDto errorResponse = ProductAddResponseDto.failure(
                    "상품 추가 중 서버 오류가 발생했습니다."
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
