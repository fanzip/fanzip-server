package org.example.fanzip.survey.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class AIReportSummaryDTO {
    private BigDecimal averageRating;           // 평점
    private BigDecimal satisfactionRate;        // 만족도 (평점 * 20%)
    private Long totalParticipants;            // 설문 참여 팬 수
    private Map<Integer, Long> ratingDistribution;  // 각 점수별 팬 수 (1~5점)
    private String overallSummary;             // 종합 설명
    private List<String> positiveFeedbacks;    // 긍정 피드백
    private List<String> negativeFeedbacks;    // 부정 피드백

    public AIReportSummaryDTO() {}

    public AIReportSummaryDTO(BigDecimal averageRating, BigDecimal satisfactionRate, Long totalParticipants,
                             Map<Integer, Long> ratingDistribution, String overallSummary,
                             List<String> positiveFeedbacks, List<String> negativeFeedbacks) {
        this.averageRating = averageRating;
        this.satisfactionRate = satisfactionRate;
        this.totalParticipants = totalParticipants;
        this.ratingDistribution = ratingDistribution;
        this.overallSummary = overallSummary;
        this.positiveFeedbacks = positiveFeedbacks;
        this.negativeFeedbacks = negativeFeedbacks;
    }

    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    public BigDecimal getSatisfactionRate() {
        return satisfactionRate;
    }

    public void setSatisfactionRate(BigDecimal satisfactionRate) {
        this.satisfactionRate = satisfactionRate;
    }

    public Long getTotalParticipants() {
        return totalParticipants;
    }

    public void setTotalParticipants(Long totalParticipants) {
        this.totalParticipants = totalParticipants;
    }

    public Map<Integer, Long> getRatingDistribution() {
        return ratingDistribution;
    }

    public void setRatingDistribution(Map<Integer, Long> ratingDistribution) {
        this.ratingDistribution = ratingDistribution;
    }

    public String getOverallSummary() {
        return overallSummary;
    }

    public void setOverallSummary(String overallSummary) {
        this.overallSummary = overallSummary;
    }

    public List<String> getPositiveFeedbacks() {
        return positiveFeedbacks;
    }

    public void setPositiveFeedbacks(List<String> positiveFeedbacks) {
        this.positiveFeedbacks = positiveFeedbacks;
    }

    public List<String> getNegativeFeedbacks() {
        return negativeFeedbacks;
    }

    public void setNegativeFeedbacks(List<String> negativeFeedbacks) {
        this.negativeFeedbacks = negativeFeedbacks;
    }
}