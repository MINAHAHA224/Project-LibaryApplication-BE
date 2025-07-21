package com.ThuVien.ThuVienAplication.model.dto.response.bookTitleRp;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookTitleRpDetailDto {
    private  String codeBookTitle ;

    private  String nameBook ;

    private  Integer codeAuthor ;
    private  String nameAuthor ;

    private  String formatBook ;
    private String contentBook;

    // different file response String
    private String picturePath;

    private Date dateRelease;

    private Integer editions;
    private Integer pages;
    private Long price;



    private String namePublisher;
    private Integer codeLanguage;
    private String codeType;

}
