package org.example.fanzip.survey;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class OpenAIConnectionTest {
    
    public static void main(String[] args) {
        String apiKey = "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";
        String urlString = "https://api.openai.com/v1/chat/completions";
        
        // 테스트할 JSON 요청
        String jsonRequest = "{\n" +
            "  \"model\": \"gpt-3.5-turbo\",\n" +
            "  \"messages\": [\n" +
            "    {\"role\": \"system\", \"content\": \"당신은 테스트를 위한 AI입니다.\"},\n" +
            "    {\"role\": \"user\", \"content\": \"안녕하세요!\"}\n" +
            "  ],\n" +
            "  \"temperature\": 0.7,\n" +
            "  \"max_tokens\": 100\n" +
            "}";
        
        try {
            // URL 생성
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            // HTTP 요청 설정
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            
            System.out.println("=== 요청 정보 ===");
            System.out.println("URL: " + urlString);
            System.out.println("Authorization: Bearer " + apiKey.substring(0, 20) + "...");
            System.out.println("Content-Type: application/json");
            System.out.println("Request JSON:");
            System.out.println(jsonRequest);
            System.out.println("================");
            
            // 요청 본문 전송
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonRequest.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            
            // 응답 읽기
            int responseCode = connection.getResponseCode();
            System.out.println("=== 응답 정보 ===");
            System.out.println("Response Code: " + responseCode);
            
            BufferedReader br;
            if (responseCode >= 200 && responseCode < 300) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"));
            }
            
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            
            System.out.println("Response Body: " + response.toString());
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