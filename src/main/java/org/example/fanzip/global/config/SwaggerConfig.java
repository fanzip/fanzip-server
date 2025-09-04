package org.example.fanzip.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.example.fanzip"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .securitySchemes(Arrays.asList(apiKeyScheme()))
                .securityContexts(Arrays.asList(securityContext()));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Fanzip API")
                .description("Fanzip 팬미팅 및 인플루언서 플랫폼 서비스 API 문서\n\n" +
                            "주요 기능:\n" +
                            "- 팬미팅 관리 및 예약\n" +
                            "- 인플루언서 관리 및 구독\n" +
                            "- 멤버십 및 결제 관리\n" +
                            "- 마켓 상품 관리\n" +
                            "- 팬카드 및 QR코드 관리\n" +
                            "- 알림 및 설문조사 관리")
                .version("1.0.0")
                .contact(new Contact("Fanzip Team", "https://fanzip.com", "support@fanzip.com"))
                .build();
    }

    private ApiKey apiKeyScheme() {
        return new ApiKey("JWT", "Authorization", "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("/api/.*"))
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
    }
}