package com.ThuVien.ThuVienAplication.model.dto.request.bookRqDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ListBookRqDto {

    @JsonProperty("sachList")
    List<BookRqDto> bookRqDtoList;
}
