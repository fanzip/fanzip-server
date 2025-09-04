package org.example.fanzip.survey.service;

import org.example.fanzip.survey.domain.MeetingSurveyResponseVO;
import org.example.fanzip.survey.dto.AIReportDTO;
import org.example.fanzip.survey.dto.AIReportSummaryDTO;

import java.util.List;

public interface AIAnalysisService {
    
    /**
     * 설문 응답들을 분석하여 줄글 형태의 보고서를 생성합니다.
     * 
     * @param meetingId 팬미팅 ID
     * @param responses 설문 응답 리스트
     * @return 줄글 형태의 보고서 텍스트
     */
    String generateNarrativeReport(Long meetingId, List<MeetingSurveyResponseVO> responses);
    
    /**
     * 설문 응답들을 분석하여 AI 보고서 요약을 생성합니다.
     * 
     * @param meetingId 팬미팅 ID
     * @param responses 설문 응답 리스트
     * @return AI 보고서 요약 DTO
     */
    AIReportSummaryDTO generateAIReportSummary(Long meetingId, List<MeetingSurveyResponseVO> responses);
}