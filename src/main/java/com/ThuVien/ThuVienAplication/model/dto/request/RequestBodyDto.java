package com.ThuVien.ThuVienAplication.model.dto.request;

import com.ThuVien.ThuVienAplication.model.dto.request.bookRqDto.BookRqDto;
import com.ThuVien.ThuVienAplication.model.dto.response.bookRp.BookRpDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class RequestBodyDto {
    @JsonProperty("action")
    private String action;

    @JsonProperty("sachData")
    private BookRqDto sachData;
}
