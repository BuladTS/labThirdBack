package com.example.apilabthird.security.advice;

import com.example.apilabthird.DTO.ApiError;
import com.example.apilabthird.security.service.exception.UserAlreadyExistException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class SecurityAdvice {


    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ApiError> catchUserAlreadyExistException(UserAlreadyExistException exception) {
        return new ResponseEntity<>(new ApiError(HttpStatus.CONFLICT, exception), HttpStatus.CONFLICT);
    }
}
