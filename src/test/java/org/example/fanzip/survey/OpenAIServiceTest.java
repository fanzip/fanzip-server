package org.example.fanzip.survey;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.fanzip.survey.dto.OpenAIRequestDTO;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OpenAIServiceTest {
    
    public static void main(String[] args) {
        String apiKey = "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";
        String url = "https://api.openai.com/v1/chat/completions";
        String model = "gpt-3.5-turbo";
        
        try {
            // ObjectMapper 생성 (JacksonConfig와 같은 방식)
            ObjectMapper objectMapper = new ObjectMapper();
            
            // RestTemplate 생성 (OpenAIConfig와 같은 방식)
            RestTemplate restTemplate = new RestTemplate();
            
            // JSON 메시지 컨버터 설정
            List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
            MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
            jsonConverter.setObjectMapper(objectMapper);
            messageConverters.add(jsonConverter);
            restTemplate.setMessageConverters(messageConverters);
            
            // 헤더 인터셉터 추가
            restTemplate.getInterceptors().add((request, body, execution) -> {
                request.getHeaders().set("Authorization", "Bearer " + apiKey);
                request.getHeaders().set("Content-Type", "application/json");
                request.getHeaders().set("Accept", "application/json");
                return execution.execute(request, body);
            });
            
            // 요청 메시지 생성
            List<OpenAIRequestDTO.Message> messages = Arrays.asList(
                new OpenAIRequestDTO.Message("system", "당신은 팬미팅 리포트 작성 AI입니다."),
                new OpenAIRequestDTO.Message("user", "테스트 리포트를 작성해주세요.")
            );
            
            // 요청 DTO 생성
            OpenAIRequestDTO request = new OpenAIRequestDTO(model, messages, 0.7, 200);
            
            System.out.println("=== Spring RestTemplate 테스트 ===");
            System.out.println("API 호출 시작...");
            
            // API 호출 (실제 서비스와 동일한 방식)
            String response = restTemplate.postForObject(url, request, String.class);
            
            System.out.println("=== 응답 결과 ===");
            System.out.println("Response: " + response);
            System.out.println("================");
            
        } catch (Exception e) {
            System.err.println("=== 에러 발생 ===");
            System.err.println("에러 메시지: " + e.getMessage());
            System.err.println("에러 타입: " + e.getClass().getSimpleName());
            if (e.getCause() != null) {
                System.err.println("원인: " + e.getCause().getMessage());
            }
            e.printStackTrace();
        }
    }
}