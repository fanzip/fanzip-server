package org.example.fanzip.survey.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.fanzip.survey.domain.MeetingSurveyAnalysisVO;
import org.example.fanzip.survey.domain.MeetingSurveyResponseVO;
import org.example.fanzip.survey.dto.AIReportDTO;
import org.example.fanzip.survey.dto.SurveySubmissionRequestDTO;
import org.example.fanzip.survey.dto.SurveySubmissionResponseDTO;
import org.example.fanzip.survey.dto.SurveySummaryDTO;
import org.example.fanzip.survey.repository.MeetingSurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    public AIReportDTO generateAIReport(Long meetingId) {
        // 해당 팬미팅의 설문 응답들 조회
        List<MeetingSurveyResponseVO> responses = meetingSurveyRepository.findResponsesByMeetingId(meetingId);
        
        if (responses.isEmpty()) {
            throw new IllegalArgumentException("분석할 설문 응답이 없습니다.");
        }
        
        // AI 분석 수행
        AIReportDTO aiReport = aiAnalysisService.analyzeResponses(meetingId, responses);
        // 분석 결과를 DB에 저장
        try {
            String themesJson = objectMapper.writeValueAsString(aiReport.getTopThemes());
            String actionsJson = objectMapper.writeValueAsString(aiReport.getActionItems());
            
            MeetingSurveyAnalysisVO analysisVO = new MeetingSurveyAnalysisVO(
                meetingId,
                aiReport.getModel(),
                aiReport.getSummary(),
                themesJson,
                actionsJson,
                aiReport.getSentimentAvg(),
                aiReport.getCsatAvg()
            );
            
            meetingSurveyRepository.saveSurveyAnalysis(analysisVO);
            aiReport.setAnalysisId(analysisVO.getAnalysisId());
            aiReport.setRunAt(analysisVO.getRunAt());
            
        } catch (Exception e) {
            throw new RuntimeException("AI 분석 결과 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return aiReport;
    }

    @Override
    public AIReportDTO getLatestAIReport(Long meetingId) {
        MeetingSurveyAnalysisVO analysisVO = meetingSurveyRepository.findLatestAnalysisByMeetingId(meetingId);
        
        if (analysisVO == null) {
            return null;
        }
        
        try {
            List<AIReportDTO.ThemeAnalysis> themes = objectMapper.readValue(
                analysisVO.getThemesJson(), 
                objectMapper.getTypeFactory().constructCollectionType(List.class, AIReportDTO.ThemeAnalysis.class)
            );
            
            List<AIReportDTO.ActionItem> actions = objectMapper.readValue(
                analysisVO.getActionsJson(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, AIReportDTO.ActionItem.class)
            );
            
            return new AIReportDTO(
                analysisVO.getAnalysisId(),
                analysisVO.getMeetingId(),
                analysisVO.getRunAt(),
                analysisVO.getModel(),
                analysisVO.getSummary(),
                themes,
                actions,
                analysisVO.getSentimentAvg(),
                analysisVO.getCsatAvg()
            );
            
        } catch (Exception e) {
            throw new RuntimeException("AI 분석 결과 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public boolean hasUserSubmittedSurvey(Long meetingId, Long userId) {
        return meetingSurveyRepository.existsByMeetingAndUser(meetingId, userId);
    }
}