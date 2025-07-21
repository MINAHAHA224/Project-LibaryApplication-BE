package com.ThuVien.ThuVienAplication.model.dto.response.bookRp;

import com.ThuVien.ThuVienAplication.model.dto.request.bookRqDto.BookRqDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookRpDeleteDto {
    @JsonProperty("sachData")
    private BookRqDto bookRqDto;
}
