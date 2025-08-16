package org.example.fanzip.global.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BusinessMetricsService {
    private final MeterRegistry meterRegistry;

    //    비즈니스 메트릭
    private final Counter userLoginAttempts;
    private final Timer userLoginDuration;
    private final Counter userRegistrations;

    @Autowired
    public BusinessMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        this.userLoginAttempts = Counter.builder("fanzip_user_login_attempts_total")
                .description("총 로그인 시도 횟수")
                .register(meterRegistry);

        this.userRegistrations = Counter.builder("fanzip_user_registrations_total")
                .description("사용자 등록 횟수")
                .register(meterRegistry);

        this.userLoginDuration = Timer.builder("fanzip_user_login_duration_seconds")
                .description("로그인 처리 시간")
                .register(meterRegistry);


        log.info("✅ BusinessMetricsService initialized successfully!");
    }

    //    로그인 관련 메트
    public void recordLoginAttempt(){
        userLoginAttempts.increment();
        log.debug("📊 Login attempt recorded - Total: {}", userLoginAttempts.count());
    }

    public void recordLoginFailure(String reason){
        Counter.builder("fanzip_user_login_failures_total")
                .description("로그인 실패 횟수")
                .tag("reason", reason)//tag 별로 Counter 생성됨
                .register(meterRegistry)
                .increment();
        log.debug("📊 Login failure recorded: {}", reason);
    }

    public Timer.Sample startLoginTimer(){
        return Timer.start(meterRegistry);
    }

    public void recordLoginDuration(Timer.Sample sample){
        sample.stop(userLoginDuration);
        log.debug("📊 Login duration recorded");
    }

    public void recordUserRegistration(){
        userRegistrations.increment();
        log.debug("📊 User registration recorded - Total: {}", userRegistrations.count());
    }

    public void recordRegistrationFailure(String reason) {
        Counter.builder("fanzip_user_registration_failures_total")
                .description("회원가입 실패 수")
                .tag("reason", reason)  // ValidationException, DuplicateEmailException 등
                .register(meterRegistry)
                .increment();
        log.debug("📊 Registration failure recorded: {}", reason);
    }

    //    API 호출 메트릭
    public void recordApiCall(String endpoint, String method, int statusCode){
        Counter.builder("fanzip_api_calls_total")
                .tag("endpoint", endpoint)
                .tag("method", method)
                .tag("status", String.valueOf(statusCode))
                .register(meterRegistry)
                .increment();
    }

    public void recordApiResponseTime(String endpoint, String method, long durationMs){
        Timer.builder("fanzip_api_response_duration_seconds")
                .tag("endpoint", endpoint)
                .tag("method", method)
                .register(meterRegistry)
                .record(durationMs, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

}
