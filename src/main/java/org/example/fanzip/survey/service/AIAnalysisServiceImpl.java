package org.example.fanzip.survey.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.fanzip.survey.domain.MeetingSurveyResponseVO;
import org.example.fanzip.survey.dto.AIReportDTO;
import org.example.fanzip.survey.dto.OpenAIRequestDTO;
import org.example.fanzip.survey.dto.OpenAIResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
    
    @Autowired
    private RestTemplate openAIRestTemplate;
    
    @Value("${openai.api.url}")
    private String openaiApiUrl;
    
    @Value("${openai.api.model}")
    private String openaiModel;

    @Override
    public String generateNarrativeReport(Long meetingId, List<MeetingSurveyResponseVO> responses) {
        try {
            // 기본 통계 계산
            double avgRating = responses.stream()
                .mapToInt(MeetingSurveyResponseVO::getOverallRating)
                .average()
                .orElse(0.0);

            // 텍스트 피드백 추출
            List<String> textFeedbacks = extractTextFeedbacks(responses);
            
            // OpenAI API를 사용한 줄글 리포트 생성
            String openAIResult = callOpenAIForNarrative(avgRating, responses.size(), textFeedbacks);
            
            // JSON 파싱해서 narrative 부분만 반환
            return parseNarrativeFromJSON(openAIResult);
            
        } catch (Exception e) {
            // OpenAI API 호출 실패 시 fallback으로 기본 줄글 생성
            return generateBasicNarrativeReport(responses);
        }
    }
    
    public NarrativeReportData generateFullNarrativeReport(Long meetingId, List<MeetingSurveyResponseVO> responses) {
        try {
            // 기본 통계 계산
            double avgRating = responses.stream()
                .mapToInt(MeetingSurveyResponseVO::getOverallRating)
                .average()
                .orElse(0.0);

            // 텍스트 피드백 추출
            List<String> textFeedbacks = extractTextFeedbacks(responses);
            
            // OpenAI API를 사용한 전체 리포트 생성
            String openAIResult = callOpenAIForNarrative(avgRating, responses.size(), textFeedbacks);
            
            // JSON 파싱해서 전체 데이터 반환
            return parseFullNarrativeReport(openAIResult, meetingId, avgRating);
            
        } catch (Exception e) {
            // OpenAI API 호출 실패 시 fallback
            return generateBasicFullNarrativeReport(meetingId, responses);
        }
    }

    
    
    private String callOpenAIForNarrative(double avgRating, int responseCount, List<String> textFeedbacks) {
        try {
            String prompt = buildNarrativePrompt(avgRating, responseCount, textFeedbacks);
            
            List<OpenAIRequestDTO.Message> messages = Arrays.asList(
                new OpenAIRequestDTO.Message("system", "당신은 팬미팅 설문조사 결과를 친근하고 자연스러운 줄글로 작성하는 전문 작가입니다. 데이터를 바탕으로 따뜻하고 읽기 쉬운 보고서를 작성해주세요."),
                new OpenAIRequestDTO.Message("user", prompt)
            );
            
            OpenAIRequestDTO request = new OpenAIRequestDTO(openaiModel, messages, 0.7, 1000);
            
            OpenAIResponseDTO response = openAIRestTemplate.postForObject(
                openaiApiUrl, 
                request, 
                OpenAIResponseDTO.class
            );
            
            if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                return response.getChoices().get(0).getMessage().getContent();
            }
            
            throw new RuntimeException("OpenAI API 응답이 비어있습니다.");
            
        } catch (Exception e) {
            throw new RuntimeException("OpenAI API 호출 실패: " + e.getMessage());
        }
    }
    
    private String buildNarrativePrompt(double avgRating, int responseCount, List<String> textFeedbacks) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("인플루언서를 위한 팬미팅 후기 AI 리포트를 작성해주세요. 줄글 리포트와 함께 구조화된 분석 데이터도 함께 제공해야 합니다.\n\n");
        prompt.append("📊 기본 정보:\n");
        prompt.append("- 참여한 구독자/팬: ").append(responseCount).append("명\n");
        prompt.append("- 평균 만족도: ").append(String.format("%.1f", avgRating)).append("점/5점\n\n");
        
        if (!textFeedbacks.isEmpty()) {
            prompt.append("💭 팬들이 남긴 솔직한 후기:\n");
            for (int i = 0; i < Math.min(textFeedbacks.size(), 12); i++) {
                prompt.append("\"").append(textFeedbacks.get(i)).append("\"\n");
            }
            prompt.append("\n");
        }
        
        prompt.append("다음 JSON 형식으로 응답해주세요:\n\n");
        prompt.append("{\n");
        prompt.append("  \"narrative\": \"친근하고 상세한 줄글 리포트 (400-600자)\",\n");
        prompt.append("  \"themes\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"tag\": \"좌석/공간\",\n");
        prompt.append("      \"summary\": \"해당 테마에 대한 팬들의 반응 요약 (긍정적/아쉬운 등)\",\n");
        prompt.append("      \"examples\": [\"팬들이 언급한 구체적 후기 1-2개\"]\n");
        prompt.append("    }\n");
        prompt.append("  ],\n");
        prompt.append("  \"actions\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"assignee\": \"운영팀\",\n");
        prompt.append("      \"action\": \"구체적인 개선 방안\",\n");
        prompt.append("      \"timeline\": \"다음주까지\",\n");
        prompt.append("      \"priority\": \"급해요\"\n");
        prompt.append("    }\n");
        prompt.append("  ]\n");
        prompt.append("}\n\n");
        
        prompt.append("📝 줄글 리포트 작성 가이드:\n");
        prompt.append("- 첫 문단: 전체 분위기 소개 ('이번 팬미팅에는 총 X명의 소중한 구독자들이...')\n");
        prompt.append("- 두 번째 문단: 특히 만족했던 부분들 ('특히 ~에 대한 만족도가 높았습니다. 팬들은 \"~\"라는 의견과...')\n");
        prompt.append("- 세 번째 문단: 아쉬웠던 점들 ('다만, ~에서는 일부 팬들이 \"~\"라는 의견을...')\n");
        prompt.append("- 네 번째 문단: 개선 방안 제시 ('이에 따라 운영팀은 ~ 방안을 마련하기로...')\n");
        prompt.append("- 다섯 번째 문단: 마무리 ('종합적으로 이번 행사는 ~했지만, ~을 통해...')\n\n");
        
        prompt.append("✨ 요구사항:\n");
        prompt.append("- 인플루언서에게 말하듯 친근한 톤\n");
        prompt.append("- 구체적인 후기 인용과 데이터 활용\n");
        prompt.append("- 건설적이고 따뜻한 개선 제안\n");
        prompt.append("- 테마는 후기 내용을 기반으로 5개 이하로 분류\n");
        prompt.append("- 액션 아이템은 실질적이고 구체적으로 작성");
        
        return prompt.toString();
    }
    
    private String generateBasicNarrativeReport(List<MeetingSurveyResponseVO> responses) {
        double avgRating = responses.stream()
            .mapToInt(MeetingSurveyResponseVO::getOverallRating)
            .average()
            .orElse(0.0);
            
        List<String> textFeedbacks = extractTextFeedbacks(responses);
        
        StringBuilder report = new StringBuilder();
        
        // 첫 문단: 전체 분위기 소개
        report.append(String.format("이번 팬미팅에는 총 %d명의 소중한 구독자들이 참여해서 후기를 남겨주었어요! 평균 만족도는 %.1f점으로 ", responses.size(), avgRating));
        
        if (avgRating >= 4.0) {
            report.append("정말 높은 만족도를 보여주셨네요. 구독자들이 이번 팬미팅을 진심으로 즐겼다는 게 느껴져요! ");
        } else if (avgRating >= 3.5) {
            report.append("전반적으로 만족스러운 반응을 보였어요. 대부분의 구독자들이 좋은 시간을 보냈다고 하네요. ");
        } else if (avgRating >= 3.0) {
            report.append("무난하고 긍정적인 반응을 보였어요. 구독자들이 나름 즐거운 시간을 보낸 것 같아요. ");
        } else {
            report.append("아직 개선할 부분들이 보이는 점수네요. 하지만 구독자들의 솔직한 피드백이 정말 소중해요. ");
        }
        
        // 두 번째 문단: 긍정적 부분
        if (avgRating >= 3.0) {
            report.append("구독자들이 특히 만족했던 부분들을 보면, 전반적인 행사 진행과 분위기에 대해서는 좋은 평가를 해주셨어요. ");
        }
        
        // 세 번째 문단: 개선점과 제안
        if (!textFeedbacks.isEmpty()) {
            if (avgRating < 4.0) {
                report.append("다만 일부 구독자들은 몇 가지 아쉬운 점들을 언급해주셨는데요, 이런 피드백들이 오히려 다음 팬미팅을 더욱 완벽하게 만드는 데 도움이 될 것 같아요. ");
            }
        }
        
        // 네 번째 문단: 개선 제안과 격려  
        if (avgRating < 3.5) {
            report.append("앞으로 더 좋은 팬미팅을 위해서는 구독자들의 니즈를 좀 더 세심하게 파악해보시면 좋을 것 같아요. ");
        } else {
            report.append("이미 구독자들이 많이 만족해하고 있지만, 더 특별한 경험을 위해 작은 디테일들을 보완해보시면 어떨까요? ");
        }
        
        // 다섯 번째 문단: 마무리 격려
        report.append("구독자들의 사랑이 정말 많이 느껴지는 후기들이었어요. 이런 소중한 피드백들을 바탕으로 다음 팬미팅에서는 더욱 만족도 높고 특별한 경험을 선사하실 수 있을 거예요. 구독자들도 분명 기대하고 있을 거고요!");
        
        return report.toString();
    }
    
    private String parseNarrativeFromJSON(String jsonResult) {
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonResult);
            return jsonNode.get("narrative").asText();
        } catch (Exception e) {
            // JSON 파싱 실패시 그대로 반환
            return jsonResult;
        }
    }
    
    private NarrativeReportData parseFullNarrativeReport(String jsonResult, Long meetingId, double avgRating) {
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonResult);
            
            String narrative = jsonNode.get("narrative").asText();
            
            List<AIReportDTO.ThemeAnalysis> themes = new ArrayList<>();
            JsonNode themesNode = jsonNode.get("themes");
            if (themesNode != null && themesNode.isArray()) {
                for (JsonNode themeNode : themesNode) {
                    List<String> examples = new ArrayList<>();
                    JsonNode examplesNode = themeNode.get("examples");
                    if (examplesNode != null && examplesNode.isArray()) {
                        for (JsonNode example : examplesNode) {
                            examples.add(example.asText());
                        }
                    }
                    themes.add(new AIReportDTO.ThemeAnalysis(
                        themeNode.get("tag").asText(),
                        themeNode.get("summary").asText(),
                        examples
                    ));
                }
            }
            
            List<AIReportDTO.ActionItem> actionItems = new ArrayList<>();
            JsonNode actionsNode = jsonNode.get("actions");
            if (actionsNode != null && actionsNode.isArray()) {
                for (JsonNode actionNode : actionsNode) {
                    actionItems.add(new AIReportDTO.ActionItem(
                        actionNode.get("assignee").asText(),
                        actionNode.get("action").asText(),
                        actionNode.get("timeline").asText(),
                        actionNode.get("priority").asText()
                    ));
                }
            }
            
            return new NarrativeReportData(narrative, themes, actionItems);
            
        } catch (Exception e) {
            throw new RuntimeException("줄글 리포트 JSON 파싱 실패: " + e.getMessage());
        }
    }
    
    private NarrativeReportData generateBasicFullNarrativeReport(Long meetingId, List<MeetingSurveyResponseVO> responses) {
        String narrative = generateBasicNarrativeReport(responses);
        List<AIReportDTO.ThemeAnalysis> themes = new ArrayList<>();
        List<AIReportDTO.ActionItem> actionItems = new ArrayList<>();
        
        // 기본 테마 추가
        themes.add(new AIReportDTO.ThemeAnalysis("전반적 만족도", "구독자들의 전반적인 반응", new ArrayList<>()));
        
        // 기본 액션 아이템 추가  
        actionItems.add(new AIReportDTO.ActionItem("운영팀", "다음 행사 개선 방안 검토", "2주 내", "보통"));
        
        return new NarrativeReportData(narrative, themes, actionItems);
    }
    
    // 내부 클래스 추가
    public static class NarrativeReportData {
        private final String narrative;
        private final List<AIReportDTO.ThemeAnalysis> themes;
        private final List<AIReportDTO.ActionItem> actionItems;
        
        public NarrativeReportData(String narrative, List<AIReportDTO.ThemeAnalysis> themes, List<AIReportDTO.ActionItem> actionItems) {
            this.narrative = narrative;
            this.themes = themes;
            this.actionItems = actionItems;
        }
        
        public String getNarrative() { return narrative; }
        public List<AIReportDTO.ThemeAnalysis> getThemes() { return themes; }
        public List<AIReportDTO.ActionItem> getActionItems() { return actionItems; }
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

}