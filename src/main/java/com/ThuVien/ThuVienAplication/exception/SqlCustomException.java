package com.ThuVien.ThuVienAplication.exception;


import lombok.Getter;

@Getter
public class SqlCustomException extends RuntimeException {
    private final String errorMessage;
    public SqlCustomException(String message) {
        super(message);
        this.errorMessage = message;
    }
}
