package org.example.fanzip.survey.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MeetingSurveyAnalysisVO {
    private Long analysisId;
    private Long meetingId;
    private LocalDateTime runAt;
    private String model;
    private String summary;
    private String themesJson;       // JSON -> String (MyBatis에서 자동 변환)
    private String actionsJson;      // JSON -> String (MyBatis에서 자동 변환) 
    private BigDecimal sentimentAvg; // DECIMAL(5,2)
    private BigDecimal csatAvg;      // DECIMAL(4,2)

    public MeetingSurveyAnalysisVO() {}

    public MeetingSurveyAnalysisVO(Long meetingId, String model, String summary,
                                   String themesJson, String actionsJson,
                                   BigDecimal sentimentAvg, BigDecimal csatAvg) {
        this.meetingId = meetingId;
        this.model = model;
        this.summary = summary;
        this.themesJson = themesJson;
        this.actionsJson = actionsJson;
        this.sentimentAvg = sentimentAvg;
        this.csatAvg = csatAvg;
    }

    public Long getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Long analysisId) {
        this.analysisId = analysisId;
    }

    public Long getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(Long meetingId) {
        this.meetingId = meetingId;
    }

    public LocalDateTime getRunAt() {
        return runAt;
    }

    public void setRunAt(LocalDateTime runAt) {
        this.runAt = runAt;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getThemesJson() {
        return themesJson;
    }

    public void setThemesJson(String themesJson) {
        this.themesJson = themesJson;
    }

    public String getActionsJson() {
        return actionsJson;
    }

    public void setActionsJson(String actionsJson) {
        this.actionsJson = actionsJson;
    }

    public BigDecimal getSentimentAvg() {
        return sentimentAvg;
    }

    public void setSentimentAvg(BigDecimal sentimentAvg) {
        this.sentimentAvg = sentimentAvg;
    }

    public BigDecimal getCsatAvg() {
        return csatAvg;
    }

    public void setCsatAvg(BigDecimal csatAvg) {
        this.csatAvg = csatAvg;
    }
}