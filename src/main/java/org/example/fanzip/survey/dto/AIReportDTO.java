package org.example.fanzip.survey.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class AIReportDTO {
    private Long analysisId;
    private Long meetingId;
    private LocalDateTime runAt;
    private String model;
    private String summary;
    private List<ThemeAnalysis> topThemes;
    private List<ActionItem> actionItems;
    private BigDecimal sentimentAvg;
    private BigDecimal csatAvg;

    public AIReportDTO() {}

    public AIReportDTO(Long analysisId, Long meetingId, LocalDateTime runAt, String model,
                      String summary, List<ThemeAnalysis> topThemes, List<ActionItem> actionItems,
                      BigDecimal sentimentAvg, BigDecimal csatAvg) {
        this.analysisId = analysisId;
        this.meetingId = meetingId;
        this.runAt = runAt;
        this.model = model;
        this.summary = summary;
        this.topThemes = topThemes;
        this.actionItems = actionItems;
        this.sentimentAvg = sentimentAvg;
        this.csatAvg = csatAvg;
    }

    public static class ThemeAnalysis {
        private String tag;
        private String summary;
        private List<String> quotes;

        public ThemeAnalysis() {}

        public ThemeAnalysis(String tag, String summary, List<String> quotes) {
            this.tag = tag;
            this.summary = summary;
            this.quotes = quotes;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public List<String> getQuotes() {
            return quotes;
        }

        public void setQuotes(List<String> quotes) {
            this.quotes = quotes;
        }
    }

    public static class ActionItem {
        private String owner;
        private String what;
        private String when;
        private String priority;

        public ActionItem() {}

        public ActionItem(String owner, String what, String when, String priority) {
            this.owner = owner;
            this.what = what;
            this.when = when;
            this.priority = priority;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public String getWhat() {
            return what;
        }

        public void setWhat(String what) {
            this.what = what;
        }

        public String getWhen() {
            return when;
        }

        public void setWhen(String when) {
            this.when = when;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }
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

    public List<ThemeAnalysis> getTopThemes() {
        return topThemes;
    }

    public void setTopThemes(List<ThemeAnalysis> topThemes) {
        this.topThemes = topThemes;
    }

    public List<ActionItem> getActionItems() {
        return actionItems;
    }

    public void setActionItems(List<ActionItem> actionItems) {
        this.actionItems = actionItems;
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