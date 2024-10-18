package com.joonhyeok.app.common.exception;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.NoSuchElementException;

@RestControllerAdvice
class ApiControllerAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        if (e instanceof EntityNotFoundException) {
            return ResponseEntity.status(400).body(new ErrorResponse("404", "존재하지 않는 리소스입니다."));
        }
        if (e instanceof NoSuchElementException)
            return ResponseEntity.status(404).body(new ErrorResponse("404", "존재하지 않는 리소스입니다."));

        if (e instanceof EntityExistsException) {
            return ResponseEntity.status(400).body(new ErrorResponse("400", "중복된 리소스입니다."));
        }

        if (e instanceof IllegalStateException) {
            return ResponseEntity.status(400).body(new ErrorResponse("400", "변경할 수 없는 상태의 리소스입니다."));
        }

        if (e instanceof IllegalArgumentException) {
            return ResponseEntity.status(400).body(new ErrorResponse("400", "잘못된 요청입니다."));
        }

        else return ResponseEntity.status(500).body(new ErrorResponse("500", "에러가 발생했습니다."));
    }
}