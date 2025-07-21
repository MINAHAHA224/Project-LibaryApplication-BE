package com.ThuVien.ThuVienAplication.model.dto.listsession;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookTitleSession implements Serializable {

    private  String codeBookTitle ;
    private  String nameBook ;
    private List<String> codeAuthor ;
    private String nameAuthor;
    private  String formatBook ;
    private String contentBook;
    private String picturePath;
    private Date dateRelease;
    private Integer editions;
    private Integer pages;
    private Long price;
    private String namePublisher;
    private Integer codeLanguage;
    private String nameCodeLanguage;
    private String codeType;
    private String nameCodeType;

    private  String codeBookTitleNew ;
    private  String nameBookNew  ;
    private  List<String> codeAuthorNew  ;
    private String nameAuthorNew ;
    private  String formatBookNew  ;
    private String contentBookNew ;
    private String picturePathNew;
    private Date dateReleaseNew ;
    private Integer editionsNew ;
    private Integer pagesNew ;
    private Long priceNew ;
    private String namePublisherNew ;
    private Integer codeLanguageNew ;
    private String nameCodeLanguageNew ;
    private String codeTypeNew ;
    private String nameCodeTypeNew ;


    private String action;

}
