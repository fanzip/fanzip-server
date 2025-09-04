package org.example.fanzip.survey.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.fanzip.survey.domain.MeetingSurveyAnalysisVO;
import org.example.fanzip.survey.domain.MeetingSurveyResponseVO;
import org.example.fanzip.survey.dto.AIReportDTO;
import org.example.fanzip.survey.dto.AIReportSummaryDTO;
import org.example.fanzip.survey.dto.SurveySubmissionRequestDTO;
import org.example.fanzip.survey.dto.SurveySubmissionResponseDTO;
import org.example.fanzip.survey.dto.SurveySummaryDTO;
import org.example.fanzip.survey.repository.MeetingSurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MeetingSurveyServiceImpl implements MeetingSurveyService {

    @Autowired
    private MeetingSurveyRepository meetingSurveyRepository;
    
    @Autowired
    private AIAnalysisService aiAnalysisService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public SurveySubmissionResponseDTO submitSurvey(SurveySubmissionRequestDTO request, Long userId) {
        // 중복 응답 체크
        if (meetingSurveyRepository.existsByMeetingAndUser(request.getMeetingId(), userId)) {
            throw new IllegalStateException("이미 설문에 응답하셨습니다.");
        }
        
        try {
            // JSON 변환
            String answersJson = objectMapper.writeValueAsString(request.getAnswers());
            
            // VO 생성
            MeetingSurveyResponseVO response = new MeetingSurveyResponseVO(
                request.getMeetingId(),
                request.getReservationId(), 
                userId,
                request.getOverallRating(),
                answersJson
            );
            
            // 저장
            int result = meetingSurveyRepository.saveSurveyResponse(response);
            
            if (result > 0) {
                return new SurveySubmissionResponseDTO(
                    response.getResponseId(),
                    "설문 응답이 성공적으로 저장되었습니다.",
                    response.getSubmittedAt()
                );
            } else {
                throw new RuntimeException("설문 응답 저장에 실패했습니다.");
            }
            
        } catch (Exception e) {
            throw new RuntimeException("설문 응답 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public SurveySummaryDTO getSurveySummary(Long influencerId) {
        Map<String, Object> stats = meetingSurveyRepository.getSurveyStatsByInfluencerId(influencerId);
        
        if (stats == null || stats.isEmpty()) {
            return new SurveySummaryDTO(0L, 0L, BigDecimal.ZERO, 0L, 0L, BigDecimal.ZERO, null, null);
        }
        
        Long totalResponses = (Long) stats.get("total_responses");
        Long totalMeetings = (Long) stats.get("total_meetings");
        BigDecimal avgRating = (BigDecimal) stats.get("avg_overall_rating");
        Long satisfiedCount = (Long) stats.get("satisfied_count");
        Long dissatisfiedCount = (Long) stats.get("dissatisfied_count");
        LocalDateTime firstResponseAt = (LocalDateTime) stats.get("first_response_at");
        LocalDateTime lastResponseAt = (LocalDateTime) stats.get("last_response_at");
        
        // 만족도 비율 계산
        BigDecimal satisfactionRate = totalResponses > 0 
            ? BigDecimal.valueOf(satisfiedCount).divide(BigDecimal.valueOf(totalResponses), 2, BigDecimal.ROUND_HALF_UP)
            : BigDecimal.ZERO;
        
        return new SurveySummaryDTO(
            totalResponses != null ? totalResponses : 0L,
            totalMeetings != null ? totalMeetings : 0L,
            avgRating != null ? avgRating : BigDecimal.ZERO,
            satisfiedCount != null ? satisfiedCount : 0L,
            dissatisfiedCount != null ? dissatisfiedCount : 0L,
            satisfactionRate,
            firstResponseAt,
            lastResponseAt
        );
    }

    @Override
    @Transactional
    public String generateNarrativeReport(Long meetingId) {
        // 해당 팬미팅의 설문 응답들 조회
        List<MeetingSurveyResponseVO> responses = meetingSurveyRepository.findResponsesByMeetingId(meetingId);
        
        if (responses.isEmpty()) {
            throw new IllegalArgumentException("분석할 설문 응답이 없습니다.");
        }
        
        // AI 줄글 보고서 생성
        String narrativeReport = aiAnalysisService.generateNarrativeReport(meetingId, responses);
        
        // DB에 저장 (기존 테이블 활용 - 구조화된 데이터 포함)
        try {
            double avgRating = responses.stream()
                .mapToInt(MeetingSurveyResponseVO::getOverallRating)
                .average()
                .orElse(0.0);
            
            // 전체 구조화된 데이터 생성
            AIAnalysisServiceImpl.NarrativeReportData fullReport = 
                ((AIAnalysisServiceImpl) aiAnalysisService).generateFullNarrativeReport(meetingId, responses);
                
            String themesJson = objectMapper.writeValueAsString(fullReport.getThemes());
            String actionsJson = objectMapper.writeValueAsString(fullReport.getActionItems());
                
            MeetingSurveyAnalysisVO analysisVO = new MeetingSurveyAnalysisVO(
                meetingId,
                "narrative-report-v1.0", // model
                narrativeReport,         // summary 필드에 줄글 저장
                themesJson,             // 실제 테마 데이터 저장
                actionsJson,            // 실제 액션 아이템 저장
                BigDecimal.valueOf(0.5), // 기본 sentiment
                BigDecimal.valueOf(avgRating)
            );
            
            meetingSurveyRepository.saveSurveyAnalysis(analysisVO);
            
        } catch (Exception e) {
            // DB 저장 실패해도 리포트는 반환
            System.err.println("줄글 리포트 DB 저장 실패: " + e.getMessage());
        }
        
        return narrativeReport;
    }

    @Override
    public String getLatestNarrativeReport(Long meetingId) {
        MeetingSurveyAnalysisVO analysisVO = meetingSurveyRepository.findLatestAnalysisByMeetingId(meetingId);
        
        if (analysisVO == null || !analysisVO.getModel().equals("narrative-report-v1.0")) {
            return null;
        }
        
        return analysisVO.getSummary(); // summary 필드에서 줄글 조회
    }

    @Override
    @Transactional
    public AIReportSummaryDTO generateAIReportSummary(Long meetingId) {
        // 해당 팬미팅의 설문 응답들 조회
        List<MeetingSurveyResponseVO> responses = meetingSurveyRepository.findResponsesByMeetingId(meetingId);
        
        if (responses.isEmpty()) {
            throw new IllegalArgumentException("분석할 설문 응답이 없습니다.");
        }
        
        // AI 보고서 요약 생성
        AIReportSummaryDTO reportSummary = aiAnalysisService.generateAIReportSummary(meetingId, responses);
        
        // DB에 저장 (기존 테이블 활용)
        try {
            String summaryJson = objectMapper.writeValueAsString(reportSummary);
            
            MeetingSurveyAnalysisVO analysisVO = new MeetingSurveyAnalysisVO(
                meetingId,
                "ai-report-summary-v1.0", // model
                summaryJson,              // summary 필드에 전체 요약 저장
                "{}",                     // themes 필드 (사용하지 않음)
                "{}",                     // action_items 필드 (사용하지 않음)  
                BigDecimal.valueOf(0.5),  // sentiment 기본값
                reportSummary.getAverageRating()
            );
            
            meetingSurveyRepository.saveSurveyAnalysis(analysisVO);
            
        } catch (Exception e) {
            // DB 저장 실패해도 리포트는 반환
            System.err.println("AI 요약 리포트 DB 저장 실패: " + e.getMessage());
        }
        
        return reportSummary;
    }

    @Override
    public AIReportSummaryDTO getLatestAIReportSummary(Long meetingId) {
        MeetingSurveyAnalysisVO analysisVO = meetingSurveyRepository.findLatestAnalysisByMeetingId(meetingId);
        
        if (analysisVO == null || !analysisVO.getModel().equals("ai-report-summary-v1.0")) {
            return null;
        }
        
        try {
            return objectMapper.readValue(analysisVO.getSummary(), AIReportSummaryDTO.class);
        } catch (Exception e) {
            System.err.println("AI 요약 리포트 역직렬화 실패: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean hasUserSubmittedSurvey(Long meetingId, Long userId) {
        return meetingSurveyRepository.existsByMeetingAndUser(meetingId, userId);
    }
}