package com.ThuVien.ThuVienAplication.model.dto.response.bookTitleRp;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Validated
public class BookTitleDetailRpTest {
    private  String codeBookTitle ;

    private  String nameBook ;

    private  String nameAuthor ;

    private  String formatBook ;
    private String contentBook;

    private String picturePath;

    private Date dateRelease;


    private Integer editions;
    private Integer pages;
    private Long price;



    private String namePublisher;

    private String nameCodeLanguage;

    private String nameCodeType;


}
