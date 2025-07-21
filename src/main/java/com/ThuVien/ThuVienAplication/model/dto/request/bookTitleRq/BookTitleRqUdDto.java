package com.ThuVien.ThuVienAplication.model.dto.request.bookTitleRq;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookTitleRqUdDto {
    @NotBlank(message = "Ma sach khong duoc de trong")
    private  String codeBookTitle ;
    @NotBlank(message = "Ten sach khong duoc de trong")
    private  String nameBook ;
    @NotNull(message = "Ten tac gia khong duoc de trong")
    private List<String> codeAuthor ;
    @NotBlank(message = "Kho sach khong duoc de trong")
    private  String formatBook ;
    @NotBlank(message = "Noi dung sach khong duoc de trong")
    private String contentBook;
    private String picturePath;
    @NotNull(message = "Ngay xuat ban khong duoc de trong")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateRelease;
    @NotNull(message = "So lan tai ban khong duoc de trong")
    private Integer editions;
    @NotNull(message = "So trang khong duoc de trong")
    private Integer pages;
    @NotNull(message = "Gia khong duoc de trong")
    private Long price;
    @NotBlank(message = "Ten nha san xuat khong duoc de trong")
    private String namePublisher;
    @NotNull(message = "Ngon ngu khong duoc de trong")
    private Integer codeLanguage;
    @NotBlank(message = "The loai khong duoc de trong")
    private String codeType;


    ///////////////////////////////////////////

    @NotBlank(message = "Ma sach moi khong duoc de trong")
    private  String codeBookTitleNew ;
    @NotBlank(message = "Ten sach moi khong duoc de trong")
    private  String nameBookNew  ;
    @NotNull(message = "Ten tac gia moi khong duoc de trong")
    private List<String> codeAuthorNew  ;
    @NotBlank(message = "Kho sach moi khong duoc de trong")
    private  String formatBookNew  ;
    @NotBlank(message = "Noi dung sach moi khong duoc de trong")
    private String contentBookNew ;
    private String picturePathNew ;
    @NotNull(message = "Ngay xuat ban moi khong duoc de trong")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date  dateReleaseNew ;
    @NotNull(message = "So lan tai ban moi khong duoc de trong")
    private Integer editionsNew ;
    @NotNull(message = "So trang moi khong duoc de trong")
    private Integer pagesNew ;
    @NotNull(message = "Gia moi khong duoc de trong")
    private Long priceNew ;
    @NotBlank(message = "Ten nha san xuat moi khong duoc de trong")
    private String namePublisherNew ;
    @NotNull(message = "Ngon ngu moi khong duoc de trong")
    private Integer codeLanguageNew ;
    @NotBlank(message = "The loai moi khong duoc de trong")
    private String codeTypeNew ;
}
