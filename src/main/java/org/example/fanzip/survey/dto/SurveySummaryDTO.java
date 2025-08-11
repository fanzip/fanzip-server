package org.example.fanzip.survey.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SurveySummaryDTO {
    private Long totalResponses;
    private Long totalMeetings;
    private BigDecimal avgOverallRating;
    private Long satisfiedCount;
    private Long dissatisfiedCount;
    private BigDecimal satisfactionRate;
    private LocalDateTime firstResponseAt;
    private LocalDateTime lastResponseAt;

    public SurveySummaryDTO() {}

    public SurveySummaryDTO(Long totalResponses, Long totalMeetings, BigDecimal avgOverallRating,
                           Long satisfiedCount, Long dissatisfiedCount, BigDecimal satisfactionRate,
                           LocalDateTime firstResponseAt, LocalDateTime lastResponseAt) {
        this.totalResponses = totalResponses;
        this.totalMeetings = totalMeetings;
        this.avgOverallRating = avgOverallRating;
        this.satisfiedCount = satisfiedCount;
        this.dissatisfiedCount = dissatisfiedCount;
        this.satisfactionRate = satisfactionRate;
        this.firstResponseAt = firstResponseAt;
        this.lastResponseAt = lastResponseAt;
    }

    public Long getTotalResponses() {
        return totalResponses;
    }

    public void setTotalResponses(Long totalResponses) {
        this.totalResponses = totalResponses;
    }

    public Long getTotalMeetings() {
        return totalMeetings;
    }

    public void setTotalMeetings(Long totalMeetings) {
        this.totalMeetings = totalMeetings;
    }

    public BigDecimal getAvgOverallRating() {
        return avgOverallRating;
    }

    public void setAvgOverallRating(BigDecimal avgOverallRating) {
        this.avgOverallRating = avgOverallRating;
    }

    public Long getSatisfiedCount() {
        return satisfiedCount;
    }

    public void setSatisfiedCount(Long satisfiedCount) {
        this.satisfiedCount = satisfiedCount;
    }

    public Long getDissatisfiedCount() {
        return dissatisfiedCount;
    }

    public void setDissatisfiedCount(Long dissatisfiedCount) {
        this.dissatisfiedCount = dissatisfiedCount;
    }

    public BigDecimal getSatisfactionRate() {
        return satisfactionRate;
    }

    public void setSatisfactionRate(BigDecimal satisfactionRate) {
        this.satisfactionRate = satisfactionRate;
    }

    public LocalDateTime getFirstResponseAt() {
        return firstResponseAt;
    }

    public void setFirstResponseAt(LocalDateTime firstResponseAt) {
        this.firstResponseAt = firstResponseAt;
    }

    public LocalDateTime getLastResponseAt() {
        return lastResponseAt;
    }

    public void setLastResponseAt(LocalDateTime lastResponseAt) {
        this.lastResponseAt = lastResponseAt;
    }
}