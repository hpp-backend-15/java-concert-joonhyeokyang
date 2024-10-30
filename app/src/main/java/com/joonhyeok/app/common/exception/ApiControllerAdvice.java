package com.joonhyeok.app.common.exception;

import com.joonhyeok.app.common.lock.LockException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
class ApiControllerAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(Exception e) {
        return ResponseEntity.status(BAD_REQUEST).body(new ErrorResponse(BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(value = EntityExistsException.class)
    public ResponseEntity<ErrorResponse> handleEntityExistsException(Exception e) {
        return ResponseEntity.status(CONFLICT).body(new ErrorResponse(BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(value = IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(Exception e) {
        return ResponseEntity.status(BAD_REQUEST).body(new ErrorResponse(BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(Exception e) {
        return ResponseEntity.status(BAD_REQUEST).body(new ErrorResponse(BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(value = LockException.class)
    public ResponseEntity<ErrorResponse> handleLockException(Exception e) {
        return ResponseEntity.status(SERVICE_UNAVAILABLE).body(new ErrorResponse(SERVICE_UNAVAILABLE.value(), e.getMessage()));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleUnCaughtException(Exception e) {
        return ResponseEntity.status(500).body(new ErrorResponse(500, "에러가 발생했습니다."));
    }
}