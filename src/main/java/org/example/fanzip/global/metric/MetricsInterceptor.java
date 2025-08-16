package org.example.fanzip.global.metric;

import io.opencensus.metrics.MetricRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
@RequiredArgsConstructor
@Slf4j
public class MetricsInterceptor implements HandlerInterceptor {

    private final BusinessMetricsService businessMetricsService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long start = System.currentTimeMillis();

        log.debug("API 호출 시작: {} {}", request.getMethod(), request.getRequestURI());
        request.setAttribute("startTime", start);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Long start = (Long) request.getAttribute("startTime");
        if (start != null) {
            long duration = System.currentTimeMillis() - start;

            String method=request.getMethod();
            String uri=simplifyUri(request.getRequestURI());
            int statusCode=response.getStatus();

            businessMetricsService.recordApiCall(uri, method, statusCode);
            businessMetricsService.recordApiResponseTime(uri, method, duration);

            log.debug("API 호출 완료: {} {} - {}ms (status: {})", method, uri, duration, statusCode);
        }
    }
    private String simplifyUri(String uri) {
        if (uri == null) return "unknown";

        // 메트릭 엔드포인트는 제외
        if (uri.equals("/metrics") || uri.equals("/health")) {
            return uri;
        }

        // 쿼리 파라미터 먼저 제거
        if (uri.contains("?")) {
            uri = uri.substring(0, uri.indexOf("?"));
        }

        // 일반적인 ID 패턴들을 {id}로 변경
        uri = uri.replaceAll("/\\d+(?=/|$)", "/{id}");  // 숫자 ID

        // 특정 패턴들 처리
        uri = uri.replaceAll("/code=[^/&]+", "/code={code}");  // OAuth code 파라미터

        return uri;
    }
}
