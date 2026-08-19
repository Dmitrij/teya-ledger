package com.teya.ledger.app.rest.advisor;

import com.teya.ledger.app.exception.AccountNotFoundException;
import com.teya.ledger.lib.api.dto.ApiErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public final class LedgerControllerAdvisor {

    /**
     * 1. Обработка ошибок валидации DTO (@Valid @RequestBody)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Validation failed for incoming request");

        final Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        ApiErrorDto errorBody = new ApiErrorDto(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                LocalDateTime.now(),
                validationErrors
        );

        return new ResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
    }

    /**
     * 2. Обработка ошибки "Аккаунт не найден"
     */
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleAccountNotFound(AccountNotFoundException ex) {
        log.warn("Account not found: {}", ex.getMessage());

        ApiErrorDto errorBody = new ApiErrorDto(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now(),
                null // Детальных ошибок полей нет
        );

        return new ResponseEntity<>(errorBody, HttpStatus.NOT_FOUND);
    }


}
