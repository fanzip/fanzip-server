package org.example.fanzip.survey.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.fanzip.survey.domain.MeetingSurveyResponseVO;
import org.example.fanzip.survey.dto.AIReportDTO;
import org.example.fanzip.survey.dto.AIReportSummaryDTO;
import org.example.fanzip.survey.dto.OpenAIRequestDTO;
import org.example.fanzip.survey.dto.OpenAIResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            
            // 줄글 그대로 반환 (JSON 파싱 없이)
            return openAIResult;
            
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
                new OpenAIRequestDTO.Message("system", "당신은 친근하지만 정중한 톤으로 카카오톡 스타일의 팬미팅 후기를 전달하는 전문가입니다. '🌟 좋았던 점들' 같은 구조적 제목은 절대 쓰지 말고, 자연스럽게 좋았던 점과 아쉬운 점을 대화하듯 섞어서 말하세요. '~했어요', '~랍니다', '~거든요' 같은 정중하면서 친근한 말투로 400-600자 분량으로 길게 써주세요!"),
                new OpenAIRequestDTO.Message("user", prompt)
            );
            
            OpenAIRequestDTO request = new OpenAIRequestDTO(openaiModel, messages, 0.9, 1500);
            
            // 요청 로깅 (필요시 활성화)
            // System.out.println("OpenAI API 호출 중...");
            
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
            System.err.println("OpenAI API 호출 실패: " + e.getMessage());
            
            // 503 에러의 경우 잠시 대기 후 재시도 (선택사항)
            if (e.getMessage().contains("503")) {
                System.err.println("OpenAI 서버 과부하로 fallback 사용");
            }
            
            throw new RuntimeException("OpenAI API 호출 실패: " + e.getMessage());
        }
    }
    
    private String buildNarrativePrompt(double avgRating, int responseCount, List<String> textFeedbacks) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("인플루언서를 위한 팬미팅 후기 AI 리포트를 작성해주세요. 반드시 카카오톡 메시지처럼 짧은 줄로 나누어서 작성하세요.\n\n");
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
        
        prompt.append("친근한 카톡 스타일의 줄글로만 응답해주세요. JSON이나 구조화된 형태는 절대 사용하지 마세요.\n\n");
        
        prompt.append("📝 줄글 리포트 작성 가이드:\n");
        prompt.append("다음과 같은 친근하고 구분되는 포맷으로 작성해주세요:\n\n");
        prompt.append("=== 친근한 줄글 스타일 예시 ===\n");
        prompt.append("팬미팅 후기 결과 나왔어요! 🎉 총 X명이 참여해주셨고 평균 X.X점 나왔답니다! 😊\n\n");
        prompt.append("정말 좋았던 부분들이 많았어요! 좌석이 편안했다는 후기가 정말 많았거든요 👍 그리고 진행도 매끄럽게 잘 되었대요! 팬들이 만족해했답니다 ✨\n\n");
        prompt.append("조금 아쉬웠던 점은 입장 대기 시간이 길었다는 의견이 있었어요 🤔 하지만 전반적으로는 만족스러운 결과였고 다음번엔 더 좋아질 것 같아요! 💪\n");
        prompt.append("====================================\n\n");
        
        prompt.append("✨ 친근한 카톡 스타일 작성 규칙:\n");
        prompt.append("❗ IMPORTANT: 친근하지만 정중한 톤으로 작성하세요!\n");
        prompt.append("- 🌟, 💡 같은 구조적 이모지 헤더는 절대 사용 금지!\n");
        prompt.append("- '좋았던 점들', '개선해보면 좋을 것들' 같은 딱딱한 제목 금지!\n");
        prompt.append("- 친근하지만 예의 있게, 자연스러운 줄글로 작성\n");
        prompt.append("- '~했어요', '~랍니다', '~했거든요', '~대요', '~네요' 같은 정중하면서 친근한 말투\n");
        prompt.append("- '야야', 'ㅋㅋ' 같은 너무 친한 표현은 피하고 적절한 거리감 유지\n");
        prompt.append("- 줄바꿈은 문단별로만 사용 (한 문장마다 줄바꿈 절대 금지!)\n");
        prompt.append("- 400-600자로 충분히 길게 작성\n");
        prompt.append("- 좋았던 점들과 아쉬운 점들을 자연스러운 문장 안에 섞어서 언급\n");
        prompt.append("- 이모티콘은 자연스럽게 문장 끝에 적당히\n");
        prompt.append("- 마지막에 격려나 다음 계획에 대한 긍정적 멘트\n");
        prompt.append("- 연결된 줄글 형태로 읽기 쉽게!");
        
        return prompt.toString();
    }
    
    private String generateBasicNarrativeReport(List<MeetingSurveyResponseVO> responses) {
        double avgRating = responses.stream()
            .mapToInt(MeetingSurveyResponseVO::getOverallRating)
            .average()
            .orElse(0.0);
            
        List<String> textFeedbacks = extractTextFeedbacks(responses);
        
        // 친근한 줄글 시작 문구
        String[] greetings = {
            "📢 팬미팅 결과 나왔어요! 총 %d명이 참여해주셨고 평균 만족도는 %.1f점이었답니다! 🎉",
            "🔥 드디어 후기 결과가 나왔어요! %d명의 소중한 팬들이 %.1f점을 주셨어요! 👏✨",
            "📊 이번 행사 결과를 공개해드려요! %d명이 응답해주셨고 평균 %.1f점 나왔네요! 🌟",
            "💕 팬들 후기가 도착했어요! 총 %d명이 솔직하게 %.1f점 매겨주셨답니다! 😊",
            "🥳 결과 발표 시간이에요! %d명의 팬분들께서 %.1f점 주셨어요! 💖"
        };
        
        StringBuilder report = new StringBuilder();
        
        // 랜덤하게 인사말 선택
        int randomGreeting = (int)(Math.random() * greetings.length);
        report.append(String.format(greetings[randomGreeting], responses.size(), avgRating)).append("\n\n");
        
        // 좋았던 점들을 자연스러운 문장으로
        List<String> highlights = generateHighlightsFromFeedback(textFeedbacks, avgRating);
        if (!highlights.isEmpty()) {
            report.append(" 특히 좋았던 점들을 보면 ");
            for (int i = 0; i < highlights.size(); i++) {
                String highlight = highlights.get(i).replace("!", "");
                report.append(highlight);
                if (i < highlights.size() - 1) {
                    report.append(", ");
                } else {
                    report.append("! ");
                }
            }
        }
        
        report.append("\n\n아쉬웠던 부분도 조금 있었는데 ");
        
        // 💡 제안 섹션 - 후기 내용 기반
        String suggestion = generateSuggestionFromFeedback(textFeedbacks, avgRating);
        report.append(suggestion);
        
        return report.toString();
    }
    
    private List<String> generateHighlightsFromFeedback(List<String> textFeedbacks, double avgRating) {
        List<String> highlights = new ArrayList<>();
        
        // 후기 키워드 분석
        Map<String, Integer> positiveKeywords = new HashMap<>();
        Map<String, Integer> negativeKeywords = new HashMap<>();
        
        for (String feedback : textFeedbacks) {
            String lower = feedback.toLowerCase();
            
            // 긍정적 키워드 카운트
            if (lower.contains("좋") || lower.contains("완벽") || lower.contains("만족")) {
                positiveKeywords.merge("만족도", 1, Integer::sum);
            }
            if (lower.contains("좌석") || lower.contains("자리")) {
                if (lower.contains("편안") || lower.contains("좋")) {
                    positiveKeywords.merge("좌석", 1, Integer::sum);
                } else if (lower.contains("불편") || lower.contains("아쉬")) {
                    negativeKeywords.merge("좌석", 1, Integer::sum);
                }
            }
            if (lower.contains("진행") || lower.contains("프로그램")) {
                if (lower.contains("좋") || lower.contains("깔끔") || lower.contains("매끄")) {
                    positiveKeywords.merge("진행", 1, Integer::sum);
                } else if (lower.contains("지루") || lower.contains("아쉬")) {
                    negativeKeywords.merge("진행", 1, Integer::sum);
                }
            }
            if (lower.contains("소통") || lower.contains("상호작용")) {
                if (lower.contains("좋") || lower.contains("친근")) {
                    positiveKeywords.merge("소통", 1, Integer::sum);
                }
            }
            if (lower.contains("음향") || lower.contains("소리")) {
                if (lower.contains("울") || lower.contains("작") || lower.contains("아쉬")) {
                    negativeKeywords.merge("음향", 1, Integer::sum);
                }
            }
            if (lower.contains("대기") || lower.contains("줄") || lower.contains("입장")) {
                if (lower.contains("길") || lower.contains("오래") || lower.contains("아쉬")) {
                    negativeKeywords.merge("대기", 1, Integer::sum);
                }
            }
        }
        
        // 키워드 기반으로 하이라이트 생성 (이모티콘 추가)
        if (positiveKeywords.getOrDefault("좌석", 0) >= 2) {
            highlights.add("좌석 배치와 공간 활용에 대한 긍정적 평가가 쏟아졌어요! 👏✨");
        }
        if (positiveKeywords.getOrDefault("진행", 0) >= 2) {
            highlights.add("매끄럽고 안정적인 행사 진행으로 팬들이 정말 만족했답니다! 🎯💕");
        }
        if (positiveKeywords.getOrDefault("소통", 0) >= 1) {
            highlights.add("아티스트와 팬들 간의 자연스러운 소통이 빛났어요! 💬🌟");
        }
        if (positiveKeywords.getOrDefault("만족도", 0) >= 3) {
            highlights.add("전반적인 만족도에서 탄탄한 점수를 확보했네요! 📈👍");
        }
        
        // 기본 하이라이트 (키워드가 부족할 때) - 이모티콘 추가
        if (highlights.isEmpty()) {
            if (avgRating >= 4.0) {
                highlights.add("팬들의 높은 참여도와 긍정적인 반응이 대박이었어요! 🥳💖");
                highlights.add("행사 전반에 걸쳐 만족스러운 경험을 선사했답니다! ✨🎉");
            } else if (avgRating >= 3.5) {
                highlights.add("안정적인 행사 운영으로 기본기를 탄탄히 다졌어요! 💪😊");
                highlights.add("팬들과의 소통에서 정말 좋은 반응을 얻었네요! 💬👏");
            } else {
                highlights.add("다양한 피드백으로 소중한 개선점을 찾았어요! 🔍💡");
                highlights.add("팬들의 솔직한 의견이 성장의 기회가 되었답니다! 🌱📈");
            }
        }
        
        // 항상 마지막에 데이터 관련 하이라이트 추가
        if (textFeedbacks.size() > 5) {
            highlights.add(String.format("%d개의 알찬 후기로 풍성한 인사이트를 얻었어요! 📊💕", textFeedbacks.size()));
        }
        
        return highlights;
    }
    
    private String generateSuggestionFromFeedback(List<String> textFeedbacks, double avgRating) {
        List<String> suggestions = new ArrayList<>();
        
        // 후기 기반 개선사항 추출
        boolean hasAudioIssue = textFeedbacks.stream().anyMatch(f -> 
            f.toLowerCase().contains("음향") || f.toLowerCase().contains("소리"));
        boolean hasWaitingIssue = textFeedbacks.stream().anyMatch(f -> 
            f.toLowerCase().contains("대기") || f.toLowerCase().contains("줄"));
        boolean hasProgramIssue = textFeedbacks.stream().anyMatch(f -> 
            f.toLowerCase().contains("지루") || f.toLowerCase().contains("노잼"));
            
        if (hasAudioIssue) {
            suggestions.add("음향 시설 점검");
        }
        if (hasWaitingIssue) {
            suggestions.add("입장 절차 개선");
        }
        if (hasProgramIssue) {
            suggestions.add("프로그램 구성 다양화");
        }
        
        // 제안문 생성 (카톡 스타일)
        if (!suggestions.isEmpty()) {
            return String.format("%s 조금만 신경쓰면\n다음 행사가 완전 대박날듯! 🎉", 
                String.join("과\n", suggestions));
        }
        
        // 기본 제안문들 (카톡 스타일)
        String[] defaultSuggestions = {
            "팬들 피드백 보니까\n작은 디테일들만\n조금 더 신경쓰면\n완전 완벽할 것 같아요! ✨",
            "이번 후기들 분석해서\n팬들이 더 만족할\n포인트들 찾아보면\n좋을 것 같아요! 💡",
            "지금도 좋지만\n아쉬웠던 부분들만\n살짝 개선하면\n더 대박날듯! 🚀",
            "팬들 솔직한 의견으로\n다음 행사는\n한 단계 업그레이드\n해보시면 어떨까요! 🌟"
        };
        
        int randomIndex = (int)(Math.random() * defaultSuggestions.length);
        return defaultSuggestions[randomIndex];
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


    @Override
    public AIReportSummaryDTO generateAIReportSummary(Long meetingId, List<MeetingSurveyResponseVO> responses) {
        try {
            // 기본 통계 계산
            double avgRating = responses.stream()
                .mapToInt(MeetingSurveyResponseVO::getOverallRating)
                .average()
                .orElse(0.0);

            // 평점별 분포 계산
            Map<Integer, Long> ratingDistribution = new HashMap<>();
            for (int i = 1; i <= 5; i++) {
                ratingDistribution.put(i, 0L);
            }
            
            responses.stream()
                .map(MeetingSurveyResponseVO::getOverallRating)
                .forEach(rating -> ratingDistribution.merge(rating, 1L, Long::sum));

            // 텍스트 피드백 추출
            List<String> textFeedbacks = extractTextFeedbacks(responses);
            
            // OpenAI를 사용한 AI 분석
            AIReportSummaryDTO reportSummary = callOpenAIForSummary(avgRating, responses.size(), textFeedbacks, ratingDistribution);
            
            return reportSummary;
            
        } catch (Exception e) {
            // OpenAI API 호출 실패 시 fallback으로 기본 요약 생성
            return generateBasicAIReportSummary(responses);
        }
    }

    private AIReportSummaryDTO callOpenAIForSummary(double avgRating, int responseCount, List<String> textFeedbacks, Map<Integer, Long> ratingDistribution) {
        try {
            String prompt = buildSummaryPrompt(avgRating, responseCount, textFeedbacks, ratingDistribution);
            
            List<OpenAIRequestDTO.Message> messages = Arrays.asList(
                new OpenAIRequestDTO.Message("system", "당신은 팬미팅 설문 결과를 분석하여 구조화된 데이터를 제공하는 AI 분석가입니다. overallSummary는 반드시 친근한 카카오톡 스타일로 이모티콘을 포함하여 매번 다르게 작성하세요. JSON 형태로만 응답하세요."),
                new OpenAIRequestDTO.Message("user", prompt)
            );
            
            OpenAIRequestDTO request = new OpenAIRequestDTO(openaiModel, messages, 0.9, 1500);
            
            OpenAIResponseDTO response = openAIRestTemplate.postForObject(
                openaiApiUrl, 
                request, 
                OpenAIResponseDTO.class
            );
            
            if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                String jsonResult = response.getChoices().get(0).getMessage().getContent();
                return parseAIReportSummary(jsonResult, avgRating, responseCount, ratingDistribution);
            }
            
            throw new RuntimeException("OpenAI API 응답이 비어있습니다.");
            
        } catch (Exception e) {
            System.err.println("OpenAI API 호출 실패: " + e.getMessage());
            throw new RuntimeException("OpenAI API 호출 실패: " + e.getMessage());
        }
    }

    private String buildSummaryPrompt(double avgRating, int responseCount, List<String> textFeedbacks, Map<Integer, Long> ratingDistribution) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("팬미팅 설문 결과를 분석하여 다음 JSON 형태로 응답해주세요:\n\n");
        
        prompt.append("📊 분석 데이터:\n");
        prompt.append("- 총 참여자: ").append(responseCount).append("명\n");
        prompt.append("- 평균 평점: ").append(String.format("%.1f", avgRating)).append("점\n");
        prompt.append("- 평점 분포: ");
        for (int i = 1; i <= 5; i++) {
            prompt.append(i).append("점 ").append(ratingDistribution.get(i)).append("명 ");
        }
        prompt.append("\n\n");
        
        if (!textFeedbacks.isEmpty()) {
            prompt.append("💭 팬들의 후기:\n");
            for (int i = 0; i < Math.min(textFeedbacks.size(), 10); i++) {
                prompt.append("\"").append(textFeedbacks.get(i)).append("\"\n");
            }
            prompt.append("\n");
        }
        
        prompt.append("다음 JSON 형태로만 응답해주세요:\n");
        prompt.append("{\n");
        prompt.append("  \"overallSummary\": \"친근한 카톡 스타일의 상세한 종합 설명 (6-8문장, 이모티콘 포함)\",\n");
        prompt.append("  \"positiveFeedbacks\": [\n");
        prompt.append("    \"긍정적 피드백 1 (한 주제당 한줄)\",\n");
        prompt.append("    \"긍정적 피드백 2\",\n");
        prompt.append("    \"긍정적 피드백 3\"\n");
        prompt.append("  ],\n");
        prompt.append("  \"negativeFeedbacks\": [\n");
        prompt.append("    \"개선점 1 (한 주제당 한줄)\",\n");
        prompt.append("    \"개선점 2\",\n");
        prompt.append("    \"개선점 3\"\n");
        prompt.append("  ]\n");
        prompt.append("}\n\n");
        
        prompt.append("✨ 작성 가이드:\n");
        prompt.append("- overallSummary: 친근한 카카오톡 스타일로 충분히 길게 작성! '~했어요', '~네요', '~랍니다' 같은 정중하면서 친근한 말투 사용\n");
        prompt.append("  * 이모티콘을 자연스럽게 섞어서 사용 (🎉, 😊, 👏, ✨, 💖, 🥳, 📊, 💕, 🌟, 🔥 등)\n");
        prompt.append("  * 6-8문장으로 상세하고 풍성하게 작성! 너무 짧지 말고 충분한 내용 포함\n");
        prompt.append("  * 기본 통계 언급 → 좋았던 점들 구체적으로 → 아쉬운 점들 → 격려 메시지 순서로 구성\n");
        prompt.append("  * 딱딱한 보고서 말투가 아닌, 친구에게 자세히 설명하듯 자연스럽게\n");
        prompt.append("  * 좋았던 점과 아쉬운 점을 구체적으로 언급하되 긍정적 톤 유지\n");
        prompt.append("  * 실제 피드백 내용을 반영하여 구체적인 예시 포함\n");
        prompt.append("- positiveFeedbacks: 좋았던 점들을 주제별로 한 줄씩 (3-5개), 문장은 '~음'으로 끝나게 작성\n");
        prompt.append("- negativeFeedbacks: 아쉬웠던 점이나 개선점을 주제별로 한 줄씩 (2-4개), 문장은 '~음'으로 끝나게 작성\n");
        prompt.append("- 각 항목은 구체적이고 실용적인 내용으로 작성\n");
        prompt.append("- 매번 다른 표현과 어조로 작성하여 신선함 유지\n");
        prompt.append("- 피드백 예시: '행사 진행이 매끄럽고 안정적이었음', '음향 시설 개선이 필요함' 등\n");
        
        return prompt.toString();
    }

    private AIReportSummaryDTO parseAIReportSummary(String jsonResult, double avgRating, int responseCount, Map<Integer, Long> ratingDistribution) {
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonResult);
            
            String overallSummary = jsonNode.get("overallSummary").asText();
            
            List<String> positiveFeedbacks = new ArrayList<>();
            JsonNode positiveNode = jsonNode.get("positiveFeedbacks");
            if (positiveNode != null && positiveNode.isArray()) {
                for (JsonNode feedback : positiveNode) {
                    positiveFeedbacks.add(feedback.asText());
                }
            }
            
            List<String> negativeFeedbacks = new ArrayList<>();
            JsonNode negativeNode = jsonNode.get("negativeFeedbacks");
            if (negativeNode != null && negativeNode.isArray()) {
                for (JsonNode feedback : negativeNode) {
                    negativeFeedbacks.add(feedback.asText());
                }
            }
            
            return new AIReportSummaryDTO(
                BigDecimal.valueOf(avgRating),
                BigDecimal.valueOf(avgRating * 20), // 만족도 (평점 * 20%)
                (long) responseCount,
                ratingDistribution,
                overallSummary,
                positiveFeedbacks,
                negativeFeedbacks
            );
            
        } catch (Exception e) {
            throw new RuntimeException("AI 보고서 요약 JSON 파싱 실패: " + e.getMessage());
        }
    }

    private AIReportSummaryDTO generateBasicAIReportSummary(List<MeetingSurveyResponseVO> responses) {
        double avgRating = responses.stream()
            .mapToInt(MeetingSurveyResponseVO::getOverallRating)
            .average()
            .orElse(0.0);

        // 평점별 분포 계산
        Map<Integer, Long> ratingDistribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            ratingDistribution.put(i, 0L);
        }
        
        responses.stream()
            .map(MeetingSurveyResponseVO::getOverallRating)
            .forEach(rating -> ratingDistribution.merge(rating, 1L, Long::sum));

        List<String> textFeedbacks = extractTextFeedbacks(responses);
        
        // 친근한 카톡 스타일 요약 생성 (랜덤 변화, 더 길게)
        String[] summaryTemplates = {
            "팬미팅 결과 드디어 나왔어요! 🎉 총 %d명의 소중한 팬분들이 참여해주셨고 평균 %.1f점이라는 점수를 받았답니다! 😊 전반적으로 팬들의 반응이 정말 좋았어요. 특히 진행 과정에서 많은 분들이 만족해하시는 모습을 볼 수 있었어요 👏 물론 아쉬운 부분들도 몇 가지 있었지만, 이런 솔직한 피드백이 있어야 다음번에 더 발전할 수 있잖아요! ✨ 팬들의 목소리를 들어보니 정말 유의미한 인사이트들을 많이 얻을 수 있었어요. 다음 행사에서는 이번 경험을 바탕으로 훨씬 더 완벽한 모습을 보여드릴 수 있을 것 같네요! 🚀",
            
            "와! 드디어 후기 결과가 나왔어요! 🥳 %d명의 팬분들께서 정성스럽게 %.1f점을 주셨답니다! 정말 감사해요 💕 결과를 보니까 좋았던 점들이 생각보다 많더라고요! 팬들이 행사 진행이나 소통 부분에서 특히 만족해하신 것 같아요 😄 그리고 몇 가지 개선할 점들도 명확하게 나왔는데, 이런 건설적인 의견들이 정말 소중해요 📝 팬들의 솔직한 피드백 덕분에 어떤 부분을 더 신경써야 할지 방향이 보이네요! 👍 이번 경험을 통해 얻은 인사이트들로 다음 행사는 정말 대박날 것 같아요! 벌써부터 기대되네요 🌟",
            
            "이번 팬미팅 어땠는지 결과 공개할 시간이에요! 📊 총 %d명이 응답해주셨고 평균 %.1f점이라는 점수가 나왔어요! 🎯 전체적으로 보면 팬들이 정말 많은 부분에서 만족해해주신 것 같아요. 특히 준비한 프로그램들이나 진행 방식에 대해서 긍정적인 반응이 많았어요 😊 물론 완벽하지는 않았죠! 몇 가지 아쉬운 점들도 있었고, 개선했으면 좋겠다는 의견들도 나왔답니다 💡 하지만 이런 다양한 피드백이 있어야 성장할 수 있는 거잖아요? 팬들의 진솔한 의견 덕분에 다음엔 더 멋진 행사를 준비할 수 있을 것 같아요! 정말 기대해주세요! 🔥",
            
            "팬들의 소중한 후기가 도착했어요! 💌 총 %d명이 시간 내서 솔직하게 %.1f점을 매겨주셨답니다! 정말 고마워요 🙏 하나하나 살펴보니까 정말 다양한 의견들이 있더라고요. 좋았던 점들은 물론이고, 아쉬웠던 부분까지 세심하게 알려주셔서 너무 감사해요 📋 팬들이 어떤 부분에서 만족했는지, 또 어떤 부분을 개선하면 좋을지 명확하게 파악할 수 있었어요 ✨ 이런 피드백이야말로 진짜 보물이죠! 팬들의 목소리를 바탕으로 다음 행사는 훨씬 더 업그레이드된 모습으로 찾아뵐게요 💪 벌써부터 어떻게 더 좋게 만들지 아이디어가 막 떠올라요! 🌈"
        };
        
        int randomTemplate = (int)(Math.random() * summaryTemplates.length);
        String overallSummary = String.format(summaryTemplates[randomTemplate], responses.size(), avgRating);

        List<String> positiveFeedbacks = Arrays.asList(
            "행사 진행이 매끄럽고 안정적이었음",
            "팬들과의 소통 시간이 충분했음", 
            "전반적인 참여도와 만족도가 높았음"
        );

        List<String> negativeFeedbacks = Arrays.asList(
            "일부 세부 사항에서 개선의 여지가 있음",
            "다음 행사에서는 더 다양한 프로그램 구성 필요"
        );

        return new AIReportSummaryDTO(
            BigDecimal.valueOf(avgRating),
            BigDecimal.valueOf(avgRating * 20),
            (long) responses.size(),
            ratingDistribution,
            overallSummary,
            positiveFeedbacks,
            negativeFeedbacks
        );
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