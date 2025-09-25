package com.gmg.global.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.gmg.api.ApiResponse;
import com.gmg.global.exception.handelException.MatchMissException;
import com.gmg.global.exception.handelException.NotFoundException;
import com.gmg.global.exception.handelException.ResourceAlreadyExistsException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceAlreadyExistsException.class) // 이미 존재하는 자원 생성 시도
    public ApiResponse<Object> ResourceAlreadyExistsException(ResourceAlreadyExistsException e, HttpServletResponse response) {
        response.setStatus(HttpStatus.CONFLICT.value());
        return ApiResponse.of(HttpStatus.CONFLICT, e.getMessage(), null);
    }

    @ExceptionHandler(NotFoundException.class)
    public ApiResponse<Object> NotFoundException(NotFoundException e, HttpServletResponse response) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        return ApiResponse.of(HttpStatus.NOT_FOUND, e.getMessage(), null);
    }

    @ExceptionHandler(MatchMissException.class)
    public ApiResponse<Object> MatchMissException(MatchMissException e, HttpServletResponse response) {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        return ApiResponse.of(HttpStatus.FORBIDDEN, e.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Object> handleValidationExceptions(MethodArgumentNotValidException e, HttpServletResponse response) {
        Map<String, String> errorMap = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            errorMap.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        });

        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return ApiResponse.of(HttpStatus.BAD_REQUEST, "입력 값 검증에 실패했습니다.", errorMap);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletResponse response) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        if (e.getCause() instanceof InvalidFormatException) {
            InvalidFormatException cause = (InvalidFormatException) e.getCause();
            if (cause.getTargetType().isEnum()) {
                return ApiResponse.of(HttpStatus.BAD_REQUEST, "유효하지 않은 카테고리 값입니다.", null);
            }
        }
        return ApiResponse.of(HttpStatus.BAD_REQUEST, "잘못된 요청 형식입니다.", null);
    }

}
