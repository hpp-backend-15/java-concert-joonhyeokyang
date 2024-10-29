package com.joonhyeok.app.common.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Enumeration;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static java.util.Collections.enumeration;

@Component
@Slf4j
public class HttpLoggingFilter implements Filter {


    private final HttpServletRequest httpServletRequest;

    public HttpLoggingFilter(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("Initializing HTTP Logging Filter");
    }

    @Override
    public void doFilter(
            jakarta.servlet.ServletRequest request,
            jakarta.servlet.ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        ContentCachingRequestWrapper cachingRequestWrapper = new ContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper cachingResponseWrapper = new ContentCachingResponseWrapper((HttpServletResponse) response);

        Instant start = Instant.now(); // 시작 시간 기록

        chain.doFilter(cachingRequestWrapper, cachingResponseWrapper);

        Instant end = Instant.now(); // 종료 시간 기록
        long duration = java.time.Duration.between(start, end).toMillis(); // 처리 시간 계산

        cachingResponseWrapper.copyBodyToResponse();
        logRequest(cachingRequestWrapper); // 요청 로깅
        logResponse(cachingRequestWrapper, cachingResponseWrapper, duration); // 응답 로깅
        cachingResponseWrapper.copyBodyToResponse();
    }

    @Override
    public void destroy() {
        log.info("Destroying HTTP Logging Filter");
    }

    private String getRequestBody(HttpServletRequest request) {
        try (BufferedReader reader = request.getReader()) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            log.error("Failed to read request body", e);
            return "Unable to read request body.";
        }
    }


    private void logRequest(ContentCachingRequestWrapper request) {
        String headers = getHeadersAsString(request);
        log.info("HTTP REQUEST - Method: {}, URI: {}, Headers: {}, Body: {}",
                request.getMethod(), request.getRequestURI(), headers, new String(request.getContentAsByteArray(), StandardCharsets.UTF_8));
    }

    private void logResponse(HttpServletRequest request, ContentCachingResponseWrapper response, long duration) {
        log.info("HTTP RESPONSE - URI: {}, Status: {}, Duration: {}ms, Headers: {}, Body: {}",
                request.getRequestURI(), response.getStatus(), duration, getHeaders(response), new String(response.getContentAsByteArray(), StandardCharsets.UTF_8));
    }

    private static String getHeadersAsString(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        StringBuilder headers = new StringBuilder();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            headers.append(name).append(": ").append(value).append("; ");
        }
        return headers.toString();
    }

    private static String getHeadersAsString(Enumeration<String> headerNames,
                                             UnaryOperator<String> headerResolver) {
        StringBuilder headers = new StringBuilder();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            headers.append(name).append(": ").append(headerResolver.apply(name)).append(", ");
        }
        // 마지막 콤마와 공백 제거
        if (headers.length() > 2) {
            headers.setLength(headers.length() - 2);
        }
        return headers.toString();
    }

    private static String getHeaders(HttpServletResponse response) {
        return getHeadersAsString(
                enumeration(response.getHeaderNames()),
                name -> String.join(", ", response.getHeaders(name))
        );
    }
}
