package org.example.fanzip.survey.controller;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.security.CustomUserPrincipal;
import org.example.fanzip.survey.dto.AIReportDTO;
import org.example.fanzip.survey.dto.SurveySubmissionRequestDTO;
import org.example.fanzip.survey.dto.SurveySubmissionResponseDTO;
import org.example.fanzip.survey.dto.SurveySummaryDTO;
import org.example.fanzip.survey.service.MeetingSurveyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/influencers/{influencerId}")
public class MeetingSurveyController {

    private final MeetingSurveyService meetingSurveyService;

    /**
     * 사용자 만족도 조사 (설문 응답 제출)
     * POST /api/influencers/{influencerId}/feedbacks
     */
    @PostMapping("/feedbacks")
    public ResponseEntity<?> submitSurvey(
            @PathVariable Long influencerId,
            @RequestBody SurveySubmissionRequestDTO request,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        
        try {
            Long userId = principal.getUserId();
            
            // 중복 응답 체크
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

    /**
     * 사용자 만족도 요약 (대시보드)
     * GET /api/influencers/{influencerId}/feedbacks/summary
     */
    @GetMapping("/feedbacks/summary")
    public ResponseEntity<SurveySummaryDTO> getFeedbackSummary(
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

    /**
     * 사용자 만족도 AI 보고서 (요약+인사이트)
     * GET /api/influencers/{influencerId}/feedbacks/report/ai
     */
    @GetMapping("/feedbacks/report/ai")
    public ResponseEntity<?> getAIReport(
            @PathVariable Long influencerId,
            @RequestParam Long meetingId,
            @RequestParam(defaultValue = "false") boolean regenerate,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        
        try {
            AIReportDTO report;
            
            if (regenerate) {
                // 새로운 분석 생성
                report = meetingSurveyService.generateAIReport(meetingId);
            } else {
                // 기존 분석 결과 조회
                report = meetingSurveyService.getLatestAIReport(meetingId);
                
                if (report == null) {
                    // 기존 분석이 없으면 새로 생성
                    report = meetingSurveyService.generateAIReport(meetingId);
                }
            }
            
            return ResponseEntity.ok(report);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("AI 분석 보고서 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 설문 응답 여부 확인
     * GET /api/influencers/{influencerId}/feedbacks/check
     */
    @GetMapping("/feedbacks/check")
    public ResponseEntity<Boolean> checkSurveySubmission(
            @PathVariable Long influencerId,
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