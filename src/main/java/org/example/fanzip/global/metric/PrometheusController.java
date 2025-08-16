package org.example.fanzip.global.metric;


import io.micrometer.prometheus.PrometheusMeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/metrics")
public class PrometheusController {

    private final PrometheusMeterRegistry prometheusMeterRegistry;
    private final BusinessMetricsService businessMetricsService;

    @GetMapping("")
    public void scrape(HttpServletResponse response) throws IOException {
        log.info("scrape");
        response.setContentType("text/plain;charset=utf-8");
        response.getWriter().write(prometheusMeterRegistry.scrape());
//        return prometheusMeterRegistry.scrape();
    }

    /**
     * 헬스체크 엔드포인트 (선택사항)
     * 애플리케이션이 정상 동작하는지 확인용
     */
    @GetMapping(value = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
    public String health() {
        return "{\"status\":\"UP\",\"timestamp\":\"" + System.currentTimeMillis() + "\"}";
    }

//    @GetMapping("/test-metrics")
//    public ResponseEntity<String> testMetrics() {
//        try {
//            // 테스트 데이터 생성
//            businessMetricsService.recordLoginAttempt();
//            businessMetricsService.recordLoginFailure("wrong_password");
//            businessMetricsService.recordUserRegistration();
//            businessMetricsService.updateActiveUserCount(42);
//            businessMetricsService.updateDatabaseConnections(8);
//
//            return ResponseEntity.ok("✅ Test metrics generated! Check /metrics endpoint for fanzip_ metrics");
//
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("❌ Error generating test metrics: " + e.getMessage());
//        }
//    }


    @GetMapping("/fanzip-metrics")
    public void fanzipMetrics(HttpServletResponse response) throws IOException {
        try {
            String allMetrics = prometheusMeterRegistry.scrape();
            StringBuilder fanzipMetrics = new StringBuilder();

            // fanzip으로 시작하는 메트릭만 필터링
            String[] lines = allMetrics.split("\n");
            for (String line : lines) {
                if (line.contains("fanzip_") ||
                        line.startsWith("# HELP fanzip_") ||
                        line.startsWith("# TYPE fanzip_")) {
                    fanzipMetrics.append(line).append("\n");
                }
            }

            response.setContentType("text/plain; charset=utf-8");

            if (fanzipMetrics.length() > 0) {
                response.getWriter().write(fanzipMetrics.toString());
            } else {
                response.getWriter().write("# No fanzip metrics found.\n# Try /test-metrics first to generate some data.\n");
            }

        } catch (Exception e) {
            response.setStatus(500);
            response.setContentType("text/plain; charset=utf-8");
            response.getWriter().write("# Error: " + e.getMessage() + "\n");
        }
    }
}
