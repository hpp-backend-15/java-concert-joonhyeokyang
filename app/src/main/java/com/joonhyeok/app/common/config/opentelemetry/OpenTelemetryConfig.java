package com.joonhyeok.app.common.config.opentelemetry;

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
        return GlobalOpenTelemetry.getTracer("otelServiceName");
    }
}
