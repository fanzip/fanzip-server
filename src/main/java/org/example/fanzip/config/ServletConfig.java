package org.example.fanzip.config;

import lombok.RequiredArgsConstructor;
import org.example.fanzip.auth.jwt.JwtInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import java.util.List;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"org.example.fanzip.controller", "org.example.fanzip.fancard.controller"})
@ComponentScan(basePackages = {
        "org.example.fanzip.controller",
        "org.example.fanzip.auth.controller",
        "org.example.fanzip.user.controller",
        "org.example.fanzip",
        "org.example.fanzip.cart.controller",
        "org.example.fanzip.market.controller"
})
@RequiredArgsConstructor
public class ServletConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/resources/**")
                .addResourceLocations("/resources/");

        // Swagger UI 리소스 추가
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    // jsp view resolver 설정
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry){
        InternalResourceViewResolver bean = new InternalResourceViewResolver();
        bean.setViewClass(JstlView.class);
        bean.setPrefix("/WEB-INF/views/");
        bean.setSuffix(".jsp");
        registry.viewResolver(bean);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/resources/**",
                        "/api/auth/oauth/**",
                        "/api/auth/reissue/**",
                        "/api/users/register/**",
//                 TODO: 아래 api는 개발 편의상 넣어 놓은 것. 추후 삭제 필요
                 "/api/cart/**", "/api/market/**","/api/payments/**");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // LocalDateTime 지원 추가
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 타임스탬프 대신 ISO 포맷 사용

        MappingJackson2HttpMessageConverter converter =
                new MappingJackson2HttpMessageConverter(mapper);

        converters.add(converter);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        System.out.println("cors 설정");
        registry.addMapping("/**")//전체 경로에 대해
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST")
                .allowCredentials(true)
                .exposedHeaders("Authorization");
    }
}









