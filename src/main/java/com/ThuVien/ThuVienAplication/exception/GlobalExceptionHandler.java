package com.ThuVien.ThuVienAplication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

// BỎ import @ResponseStatus

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SqlCustomException.class)
    public ResponseEntity<ExceptionResponse> handleSqlCustomException(SqlCustomException ex, WebRequest request) {
        ExceptionResponse errorResponse = new ExceptionResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getErrorMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handler cho các lỗi validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String firstErrorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        ExceptionResponse errorResponse = new ExceptionResponse(
                HttpStatus.BAD_REQUEST.value(),
                firstErrorMessage
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handler cho các lỗi chung khác
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGlobalException(Exception ex, WebRequest request) {
        ExceptionResponse errorResponse = new ExceptionResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Đã có lỗi không mong muốn xảy ra ở server."
        );

        // log.error("Unhandled exception: ", ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}