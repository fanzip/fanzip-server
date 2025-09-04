package org.example.fanzip.survey.controller;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.example.fanzip.security.CustomUserPrincipal;
import org.example.fanzip.survey.dto.AIReportDTO;
import org.example.fanzip.survey.dto.AIReportSummaryDTO;
import org.example.fanzip.survey.dto.SurveySubmissionRequestDTO;
import org.example.fanzip.survey.dto.SurveySubmissionResponseDTO;
import org.example.fanzip.survey.dto.SurveySummaryDTO;
import org.example.fanzip.survey.service.MeetingSurveyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Meeting Survey", description = "팬미팅 만족도 설문 및 AI 보고서 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/influencers/{influencerId}")
public class MeetingSurveyController {

    private final MeetingSurveyService meetingSurveyService;

    @ApiOperation(value = "만족도 설문 제출", notes = "팬미팅 참석 후 만족도 설문에 응답합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "설문 제출 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청 데이터 또는 중복 제출"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @PostMapping("/feedbacks")
    public ResponseEntity<?> submitSurvey(
            @ApiParam(value = "인플루언서 ID", required = true, example = "1")
            @PathVariable Long influencerId,
            @ApiParam(value = "설문 제출 요청 데이터", required = true)
            @RequestBody SurveySubmissionRequestDTO request,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        
        try {
            Long userId = principal.getUserId();
            
            // 중복 응답 체크 (테스트용 주석 처리)
            if (meetingSurveyService.hasUserSubmittedSurvey(request.getMeetingId(), userId)) {
                return ResponseEntity.badRequest()
                    .body("이미 해당 팬미팅에 대한 설문에 응답하셨습니다.");
            }
            
            SurveySubmissionResponseDTO response = meetingSurveyService.submitSurvey(request, userId);
            return ResponseEntity.ok(response);
            
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("설문 응답 제출 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @ApiOperation(value = "만족도 설문 요약 조회", notes = "인플루언서의 전체 만족도 설문 결과 요약을 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "설문 요약 조회 성공"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 404, message = "인플루언서를 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/feedbacks/summary")
    public ResponseEntity<SurveySummaryDTO> getFeedbackSummary(
            @ApiParam(value = "인플루언서 ID", required = true, example = "1")
            @PathVariable Long influencerId,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        
        try {
            SurveySummaryDTO summary = meetingSurveyService.getSurveySummary(influencerId);
            return ResponseEntity.ok(summary);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    @ApiOperation(value = "AI 보고서 조회", notes = "팬미팅에 대한 AI 분석 보고서를 조회합니다. 신규 생성 또는 기존 보고서 조회가 가능합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "AI 보고서 조회 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청 파라미터"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 404, message = "팬미팅 또는 인플루언서를 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/feedbacks/report/ai")
    public ResponseEntity<AIReportSummaryDTO> getAIReport(
            @ApiParam(value = "인플루언서 ID", required = true, example = "1")
            @PathVariable Long influencerId,
            @ApiParam(value = "팬미팅 ID", required = true, example = "1")
            @RequestParam Long meetingId,
            @ApiParam(value = "보고서 재생성 여부 (true: 신규 생성, false: 기존 보고서 조회)", example = "true")
            @RequestParam(defaultValue = "true") boolean regenerate,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        
        try {
            AIReportSummaryDTO reportSummary;
            
            if (regenerate) {
                // 새로운 분석 생성 (기본값)
                reportSummary = meetingSurveyService.generateAIReportSummary(meetingId);
            } else {
                // 기존 분석 결과 조회 (캐시 사용)
                reportSummary = meetingSurveyService.getLatestAIReportSummary(meetingId);
                
                if (reportSummary == null) {
                    // 기존 분석이 없으면 새로 생성
                    reportSummary = meetingSurveyService.generateAIReportSummary(meetingId);
                }
            }
            
            return ResponseEntity.ok(reportSummary);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    @ApiOperation(value = "설문 응답 여부 확인", notes = "사용자가 해당 팬미팅에 대한 설문에 이미 응답했는지 확인합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "설문 응답 여부 확인 성공"),
            @ApiResponse(code = 401, message = "인증 필요"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/feedbacks/check")
    public ResponseEntity<Boolean> checkSurveySubmission(
            @ApiParam(value = "인플루언서 ID", required = true, example = "1")
            @PathVariable Long influencerId,
            @ApiParam(value = "팬미팅 ID", required = true, example = "1")
            @RequestParam Long meetingId,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        
        try {
            Long userId = principal.getUserId();
            boolean hasSubmitted = meetingSurveyService.hasUserSubmittedSurvey(meetingId, userId);
            return ResponseEntity.ok(hasSubmitted);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(false);
        }
    }
}