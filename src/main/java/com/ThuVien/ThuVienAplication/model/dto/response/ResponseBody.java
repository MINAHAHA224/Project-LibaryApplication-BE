package com.ThuVien.ThuVienAplication.model.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResponseBody {

    private int status;
    private String message;
    private Object data;
}

