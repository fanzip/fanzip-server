package org.example.fanzip.global.config;

import lombok.RequiredArgsConstructor;
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
@ComponentScan(basePackages = {
        "org.example.fanzip.controller",
        "org.example.fanzip.auth.controller",
        "org.example.fanzip.user.controller",
        "org.example.fanzip",
        "org.example.fanzip.cart.controller",
        "org.example.fanzip.market.controller",
        "org.example.fanzip.fancard.controller"
})
@RequiredArgsConstructor
public class ServletConfig implements WebMvcConfigurer {

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
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // LocalDateTime 지원 추가
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 타임스탬프 대신 ISO 포맷 사용

        MappingJackson2HttpMessageConverter converter =
                new MappingJackson2HttpMessageConverter(mapper);

        converters.add(converter);
    }
}









