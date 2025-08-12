package org.example.fanzip.survey.domain;

import java.time.LocalDateTime;

public class MeetingSurveyResponseVO {
    private Long responseId;
    private Long meetingId;
    private Long reservationId;
    private Long userId;
    private LocalDateTime submittedAt;
    private Integer overallRating;    // TINYINT -> Integer
    private String answersJson;       // JSON -> String (MyBatis에서 자동 변환)
    private Integer schemaVersion;

    public MeetingSurveyResponseVO() {}

    public MeetingSurveyResponseVO(Long meetingId, Long reservationId, Long userId,
                                   Integer overallRating, String answersJson) {
        this.meetingId = meetingId;
        this.reservationId = reservationId;
        this.userId = userId;
        this.overallRating = overallRating;
        this.answersJson = answersJson;
        this.schemaVersion = 1;
    }

    public Long getResponseId() {
        return responseId;
    }

    public void setResponseId(Long responseId) {
        this.responseId = responseId;
    }

    public Long getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(Long meetingId) {
        this.meetingId = meetingId;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Integer getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(Integer overallRating) {
        this.overallRating = overallRating;
    }

    public String getAnswersJson() {
        return answersJson;
    }

    public void setAnswersJson(String answersJson) {
        this.answersJson = answersJson;
    }

    public Integer getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(Integer schemaVersion) {
        this.schemaVersion = schemaVersion;
    }
}