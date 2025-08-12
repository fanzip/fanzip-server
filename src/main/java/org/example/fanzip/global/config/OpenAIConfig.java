package org.example.fanzip.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class OpenAIConfig {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.timeout:30000}")
    private int timeout;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public RestTemplate openAIRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        // JSON 메시지 컨버터 설정
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setObjectMapper(objectMapper);
        messageConverters.add(jsonConverter);
        restTemplate.setMessageConverters(messageConverters);
        
        // 인터셉터 설정
        restTemplate.setInterceptors(List.of(bearerTokenInterceptor()));
        
        return restTemplate;
    }

    private ClientHttpRequestInterceptor bearerTokenInterceptor() {
        return (request, body, execution) -> {
            request.getHeaders().set("Authorization", "Bearer " + apiKey);
            request.getHeaders().set("Content-Type", "application/json");
            request.getHeaders().set("Accept", "application/json");
            
            return execution.execute(request, body);
        };
    }
}