package com.gmg.global.exception;

import com.gmg.api.ApiResponse;
import com.gmg.global.exception.handelException.MatchMissException;
import com.gmg.global.exception.handelException.NotFoundException;
import com.gmg.global.exception.handelException.ResourceAlreadyExistsException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceAlreadyExistsException.class)
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

}
