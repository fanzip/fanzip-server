package org.example.fanzip.survey.service;

import org.example.fanzip.survey.dto.AIReportDTO;
import org.example.fanzip.survey.dto.SurveySubmissionRequestDTO;
import org.example.fanzip.survey.dto.SurveySubmissionResponseDTO;
import org.example.fanzip.survey.dto.SurveySummaryDTO;

public interface MeetingSurveyService {
    
    // 설문 응답 제출
    SurveySubmissionResponseDTO submitSurvey(SurveySubmissionRequestDTO request, Long userId);
    
    // 인플루언서별 설문 요약 조회
    SurveySummaryDTO getSurveySummary(Long influencerId);
    
    // 줄글 형태 보고서 생성
    String generateNarrativeReport(Long meetingId);
    
    // 기존 줄글 보고서 조회
    String getLatestNarrativeReport(Long meetingId);
    
    // 사용자가 이미 설문에 응답했는지 확인
    boolean hasUserSubmittedSurvey(Long meetingId, Long userId);
}