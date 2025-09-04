package org.example.fanzip.survey.dto;

import java.time.LocalDateTime;

public class SurveySubmissionResponseDTO {
    private Long responseId;
    private String message;
    private LocalDateTime submittedAt;

    public SurveySubmissionResponseDTO() {}

    public SurveySubmissionResponseDTO(Long responseId, String message, LocalDateTime submittedAt) {
        this.responseId = responseId;
        this.message = message;
        this.submittedAt = submittedAt;
    }

    public Long getResponseId() {
        return responseId;
    }

    public void setResponseId(Long responseId) {
        this.responseId = responseId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}