package com.joonhyeok.app.common.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.Enumeration;
import java.util.stream.Collectors;

@Component
@Slf4j
public class HttpLoggingFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("Initializing HTTP Logging Filter");
    }

    @Override
    public void doFilter(
            jakarta.servlet.ServletRequest request,
            jakarta.servlet.ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        Instant start = Instant.now(); // 시작 시간 기록
        String requestBody = getRequestBody(httpRequest); // 요청 본문 읽기

        logRequest(httpRequest, requestBody); // 요청 로깅

        // 응답 필터 체인 실행
        HttpResponseWrapper wrappedResponse = new HttpResponseWrapper(httpResponse);
        chain.doFilter(request, wrappedResponse);

        Instant end = Instant.now(); // 종료 시간 기록
        long duration = java.time.Duration.between(start, end).toMillis(); // 처리 시간 계산

        logResponse(httpRequest, wrappedResponse, duration); // 응답 로깅
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

    private void logRequest(HttpServletRequest request, String requestBody) {
        String headers = getHeadersAsString(request);
        log.info("HTTP REQUEST - Method: {}, URI: {}, Headers: {}, Body: {}",
                request.getMethod(), request.getRequestURI(), headers, requestBody);
    }

    private void logResponse(HttpServletRequest request, HttpResponseWrapper response, long duration) {
        String headers = response.getHeadersAsString();
        log.info("HTTP RESPONSE - URI: {}, Status: {}, Duration: {}ms, Headers: {}, Body: {}",
                request.getRequestURI(), response.getStatus(), duration, headers, response.getBody());
    }

    private String getHeadersAsString(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        StringBuilder headers = new StringBuilder();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            headers.append(name).append(": ").append(value).append("; ");
        }
        return headers.toString();
    }

    // HttpServletResponse를 래핑하는 클래스
    private static class HttpResponseWrapper extends jakarta.servlet.http.HttpServletResponseWrapper {

        private final StringWriter responseWriter = new StringWriter();

        public HttpResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public PrintWriter getWriter() {
            return new PrintWriter(responseWriter);
        }

        public String getBody() {
            return responseWriter.toString();
        }

        public String getHeadersAsString() {
            return this.getHeaderNames().stream()
                    .map(name -> name + ": " + this.getHeader(name))
                    .collect(Collectors.joining("; "));
        }
    }
}
