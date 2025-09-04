package org.example.fanzip.survey.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.fanzip.survey.domain.MeetingSurveyAnalysisVO;
import org.example.fanzip.survey.domain.MeetingSurveyResponseVO;

import java.util.List;
import java.util.Map;

@Mapper
public interface MeetingSurveyMapper {
    
    // 설문 응답 저장
    int insertSurveyResponse(MeetingSurveyResponseVO response);
    
    // 사용자가 이미 설문에 응답했는지 확인
    boolean existsByMeetingAndUser(@Param("meetingId") Long meetingId, @Param("userId") Long userId);
    
    // 특정 팬미팅의 모든 설문 응답 조회
    List<MeetingSurveyResponseVO> findResponsesByMeetingId(@Param("meetingId") Long meetingId);
    
    // 특정 인플루언서의 모든 팬미팅 설문 응답 조회
    List<MeetingSurveyResponseVO> findResponsesByInfluencerId(@Param("influencerId") Long influencerId);
    
    // 설문 응답 통계 조회 (만족도 평균, 응답 수 등)
    Map<String, Object> getSurveyStatsByMeetingId(@Param("meetingId") Long meetingId);
    
    // 인플루언서별 설문 응답 요약 통계
    Map<String, Object> getSurveyStatsByInfluencerId(@Param("influencerId") Long influencerId);
    
    // AI 분석 결과 저장
    int insertSurveyAnalysis(MeetingSurveyAnalysisVO analysis);
    
    // 특정 팬미팅의 최신 AI 분석 결과 조회
    MeetingSurveyAnalysisVO findLatestAnalysisByMeetingId(@Param("meetingId") Long meetingId);
    
    // 인플루언서의 모든 AI 분석 결과 조회
    List<MeetingSurveyAnalysisVO> findAnalysesByInfluencerId(@Param("influencerId") Long influencerId);
}