package com.joonhyeok.app.common.config;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenTelemetryConfig {

    @Value("${otel.service.name}")
    private String otelServiceName;

    @Bean
    public Tracer tracer() {
        // 'my-application-name' 부분을 애플리케이션의 이름으로 변경합니다.
        return GlobalOpenTelemetry.getTracer("otelServiceName");
    }
}
