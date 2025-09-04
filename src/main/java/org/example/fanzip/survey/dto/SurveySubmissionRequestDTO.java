package org.example.fanzip.survey.dto;

import java.util.Map;

public class SurveySubmissionRequestDTO {
    private Long meetingId;
    private Long reservationId;
    private Integer overallRating;
    private Map<String, Object> answers;

    public SurveySubmissionRequestDTO() {}

    public SurveySubmissionRequestDTO(Long meetingId, Long reservationId, 
                                     Integer overallRating, Map<String, Object> answers) {
        this.meetingId = meetingId;
        this.reservationId = reservationId;
        this.overallRating = overallRating;
        this.answers = answers;
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

    public Integer getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(Integer overallRating) {
        this.overallRating = overallRating;
    }

    public Map<String, Object> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<String, Object> answers) {
        this.answers = answers;
    }
}