package org.example.fanzip.survey.repository;

import org.example.fanzip.survey.domain.MeetingSurveyAnalysisVO;
import org.example.fanzip.survey.domain.MeetingSurveyResponseVO;
import org.example.fanzip.survey.mapper.MeetingSurveyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class MeetingSurveyRepositoryImpl implements MeetingSurveyRepository {

    @Autowired
    private MeetingSurveyMapper meetingSurveyMapper;

    @Override
    public int saveSurveyResponse(MeetingSurveyResponseVO response) {
        return meetingSurveyMapper.insertSurveyResponse(response);
    }

    @Override
    public boolean existsByMeetingAndUser(Long meetingId, Long userId) {
        return meetingSurveyMapper.existsByMeetingAndUser(meetingId, userId);
    }

    @Override
    public List<MeetingSurveyResponseVO> findResponsesByMeetingId(Long meetingId) {
        return meetingSurveyMapper.findResponsesByMeetingId(meetingId);
    }

    @Override
    public List<MeetingSurveyResponseVO> findResponsesByInfluencerId(Long influencerId) {
        return meetingSurveyMapper.findResponsesByInfluencerId(influencerId);
    }

    @Override
    public Map<String, Object> getSurveyStatsByMeetingId(Long meetingId) {
        return meetingSurveyMapper.getSurveyStatsByMeetingId(meetingId);
    }

    @Override
    public Map<String, Object> getSurveyStatsByInfluencerId(Long influencerId) {
        return meetingSurveyMapper.getSurveyStatsByInfluencerId(influencerId);
    }

    @Override
    public int saveSurveyAnalysis(MeetingSurveyAnalysisVO analysis) {
        return meetingSurveyMapper.insertSurveyAnalysis(analysis);
    }

    @Override
    public MeetingSurveyAnalysisVO findLatestAnalysisByMeetingId(Long meetingId) {
        return meetingSurveyMapper.findLatestAnalysisByMeetingId(meetingId);
    }

    @Override
    public List<MeetingSurveyAnalysisVO> findAnalysesByInfluencerId(Long influencerId) {
        return meetingSurveyMapper.findAnalysesByInfluencerId(influencerId);
    }
}