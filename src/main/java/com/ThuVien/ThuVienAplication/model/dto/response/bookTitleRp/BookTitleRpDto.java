package com.ThuVien.ThuVienAplication.model.dto.response.bookTitleRp;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookTitleRpDto {

    private String ISBN;
    private String nameBook;
    private String typeBook;
    private String authorBook;
    private String nameRelease;
    private Long price;

}
