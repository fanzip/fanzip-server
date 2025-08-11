package org.example.fanzip.survey.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.fanzip.survey.domain.MeetingSurveyResponseVO;
import org.example.fanzip.survey.dto.AIReportDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AIAnalysisServiceImpl implements AIAnalysisService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public AIReportDTO analyzeResponses(Long meetingId, List<MeetingSurveyResponseVO> responses) {
        try {
            // 기본 통계 계산
            double avgRating = responses.stream()
                .mapToInt(MeetingSurveyResponseVO::getOverallRating)
                .average()
                .orElse(0.0);

            // 텍스트 피드백 추출
            List<String> textFeedbacks = extractTextFeedbacks(responses);
            
            // 키워드별 분석
            List<AIReportDTO.ThemeAnalysis> themes = analyzeThemes(textFeedbacks);
            
            // 액션 아이템 생성
            List<AIReportDTO.ActionItem> actionItems = generateActionItems(avgRating, themes);
            
            // 감정 분석 (간단한 키워드 기반)
            BigDecimal sentimentScore = calculateSentiment(textFeedbacks);
            
            // 요약 생성
            String summary = generateSummary(avgRating, responses.size(), themes);
            
            return new AIReportDTO(
                null, // analysisId는 DB 저장 후 설정
                meetingId,
                LocalDateTime.now(),
                "fanzip-basic-analyzer-v1.0",
                summary,
                themes,
                actionItems,
                sentimentScore,
                BigDecimal.valueOf(avgRating)
            );
            
        } catch (Exception e) {
            throw new RuntimeException("AI 분석 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private List<String> extractTextFeedbacks(List<MeetingSurveyResponseVO> responses) {
        List<String> feedbacks = new ArrayList<>();
        
        for (MeetingSurveyResponseVO response : responses) {
            try {
                JsonNode answersNode = objectMapper.readTree(response.getAnswersJson());
                JsonNode freeTextNode = answersNode.get("free_text");
                
                if (freeTextNode != null && !freeTextNode.asText().trim().isEmpty()) {
                    feedbacks.add(freeTextNode.asText().trim());
                }
            } catch (Exception e) {
                // 로그만 남기고 계속 진행
                System.err.println("텍스트 피드백 파싱 오류: " + e.getMessage());
            }
        }
        
        return feedbacks;
    }

    private List<AIReportDTO.ThemeAnalysis> analyzeThemes(List<String> textFeedbacks) {
        List<AIReportDTO.ThemeAnalysis> themes = new ArrayList<>();
        
        // 간단한 키워드 기반 테마 분석
        Map<String, List<String>> themeGroups = categorizeByKeywords(textFeedbacks);
        
        for (Map.Entry<String, List<String>> entry : themeGroups.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                themes.add(new AIReportDTO.ThemeAnalysis(
                    entry.getKey(),
                    generateThemeSummary(entry.getKey(), entry.getValue()),
                    entry.getValue().size() > 3 ? entry.getValue().subList(0, 3) : entry.getValue()
                ));
            }
        }
        
        return themes.stream().limit(5).collect(Collectors.toList());
    }

    private Map<String, List<String>> categorizeByKeywords(List<String> feedbacks) {
        Map<String, List<String>> themeGroups = Map.of(
            "좌석/공간", new ArrayList<>(),
            "진행/프로그램", new ArrayList<>(),
            "입장/체크인", new ArrayList<>(),
            "소통/이벤트", new ArrayList<>(),
            "기타", new ArrayList<>()
        );

        for (String feedback : feedbacks) {
            String lowerFeedback = feedback.toLowerCase();
            
            if (lowerFeedback.contains("좌석") || lowerFeedback.contains("자리") || lowerFeedback.contains("공간") || lowerFeedback.contains("넓") || lowerFeedback.contains("좁")) {
                themeGroups.get("좌석/공간").add(feedback);
            } else if (lowerFeedback.contains("진행") || lowerFeedback.contains("프로그램") || lowerFeedback.contains("시간") || lowerFeedback.contains("속도")) {
                themeGroups.get("진행/프로그램").add(feedback);
            } else if (lowerFeedback.contains("입장") || lowerFeedback.contains("체크인") || lowerFeedback.contains("줄") || lowerFeedback.contains("대기")) {
                themeGroups.get("입장/체크인").add(feedback);
            } else if (lowerFeedback.contains("소통") || lowerFeedback.contains("이벤트") || lowerFeedback.contains("게임") || lowerFeedback.contains("상호작용")) {
                themeGroups.get("소통/이벤트").add(feedback);
            } else {
                themeGroups.get("기타").add(feedback);
            }
        }
        
        return themeGroups;
    }

    private String generateThemeSummary(String theme, List<String> feedbacks) {
        int positiveCount = 0;
        int negativeCount = 0;
        
        for (String feedback : feedbacks) {
            String lower = feedback.toLowerCase();
            if (lower.contains("좋") || lower.contains("만족") || lower.contains("훌륭") || lower.contains("완벽")) {
                positiveCount++;
            } else if (lower.contains("아쉽") || lower.contains("불만") || lower.contains("개선") || lower.contains("부족")) {
                negativeCount++;
            }
        }
        
        if (positiveCount > negativeCount) {
            return theme + " 관련하여 대체로 긍정적인 평가를 받았습니다.";
        } else if (negativeCount > positiveCount) {
            return theme + " 관련하여 개선이 필요한 부분이 있습니다.";
        } else {
            return theme + " 관련하여 다양한 의견이 있었습니다.";
        }
    }

    private List<AIReportDTO.ActionItem> generateActionItems(double avgRating, List<AIReportDTO.ThemeAnalysis> themes) {
        List<AIReportDTO.ActionItem> actionItems = new ArrayList<>();
        
        if (avgRating < 3.5) {
            actionItems.add(new AIReportDTO.ActionItem(
                "운영팀",
                "전반적인 만족도가 낮으므로 주요 개선 사항 검토 및 대책 수립",
                "1주일 내",
                "높음"
            ));
        }
        
        for (AIReportDTO.ThemeAnalysis theme : themes) {
            if (theme.getSummary().contains("개선이 필요")) {
                actionItems.add(new AIReportDTO.ActionItem(
                    "관련 담당자",
                    theme.getTag() + " 관련 개선 방안 검토",
                    "2주일 내",
                    "중간"
                ));
            }
        }
        
        if (actionItems.isEmpty()) {
            actionItems.add(new AIReportDTO.ActionItem(
                "운영팀",
                "현재의 높은 만족도 유지를 위한 표준화 작업",
                "1개월 내",
                "낮음"
            ));
        }
        
        return actionItems;
    }

    private BigDecimal calculateSentiment(List<String> textFeedbacks) {
        if (textFeedbacks.isEmpty()) {
            return BigDecimal.valueOf(0.5); // 중립
        }
        
        int positiveWords = 0;
        int negativeWords = 0;
        int totalWords = 0;
        
        List<String> positiveKeywords = Arrays.asList("좋", "만족", "훌륭", "완벽", "최고", "감사", "행복", "즐거", "재미");
        List<String> negativeKeywords = Arrays.asList("아쉽", "불만", "별로", "실망", "부족", "개선", "문제", "힘들", "불편");
        
        for (String feedback : textFeedbacks) {
            String[] words = feedback.split("\\s+");
            totalWords += words.length;
            
            for (String word : words) {
                for (String positive : positiveKeywords) {
                    if (word.contains(positive)) {
                        positiveWords++;
                        break;
                    }
                }
                for (String negative : negativeKeywords) {
                    if (word.contains(negative)) {
                        negativeWords++;
                        break;
                    }
                }
            }
        }
        
        if (totalWords == 0) {
            return BigDecimal.valueOf(0.5);
        }
        
        double sentiment = 0.5 + (double)(positiveWords - negativeWords) / (totalWords * 2);
        return BigDecimal.valueOf(Math.max(0.0, Math.min(1.0, sentiment)));
    }

    private String generateSummary(double avgRating, int responseCount, List<AIReportDTO.ThemeAnalysis> themes) {
        StringBuilder summary = new StringBuilder();
        
        summary.append(String.format("총 %d명이 응답하여 평균 %.1f점의 만족도를 보였습니다. ", responseCount, avgRating));
        
        if (avgRating >= 4.0) {
            summary.append("전반적으로 높은 만족도를 기록했습니다. ");
        } else if (avgRating >= 3.0) {
            summary.append("보통 수준의 만족도를 기록했습니다. ");
        } else {
            summary.append("개선이 필요한 만족도 수준입니다. ");
        }
        
        if (!themes.isEmpty()) {
            summary.append(String.format("주요 피드백 영역은 %s 등이었습니다.", 
                themes.stream().map(AIReportDTO.ThemeAnalysis::getTag).limit(3).collect(Collectors.joining(", "))));
        }
        
        return summary.toString();
    }
}