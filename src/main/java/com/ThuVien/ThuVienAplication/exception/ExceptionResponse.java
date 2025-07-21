package com.ThuVien.ThuVienAplication.exception;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ExceptionResponse {

    private int status;
    private String message;


}