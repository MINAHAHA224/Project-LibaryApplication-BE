package com.ThuVien.ThuVienAplication.model.dto.response.bookTitleRp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookTitleDisplayDto {
    private String codeBookTitle;
    private String nameBook;
    private String nameAuthor;
    private String formatBook;
    private String contentBook;
    private String picturePath;
    private Date dateRelease;
    private Integer editions;
    private Integer pages;
    private Long price;
    private String namePublisher;

    private String nameCodeLanguage;
    private String nameCodeType;


    private Integer codeLanguage;
    private String codeType;
    private List<String> codeAuthor;
}
