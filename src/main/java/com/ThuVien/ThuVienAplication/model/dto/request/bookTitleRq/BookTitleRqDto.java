package com.ThuVien.ThuVienAplication.model.dto.request.bookTitleRq;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Validated
public class BookTitleRqDto {
    @NotBlank(message = "Ma sach khong duoc de trong")
    private  String codeBookTitle ;

    @NotBlank(message = "Ten sach khong duoc de trong")
    private  String nameBook ;

    @NotNull(message = "Ten tac gia khong duoc de trong")
    private List<String> codeAuthor ;

    private String nameAuthor;

    @NotBlank(message = "Kho sach khong duoc de trong")
    private  String formatBook ;
    @NotBlank(message = "Noi dung sach khong duoc de trong")
    private String contentBook;

    private String picturePath;

    @NotNull(message = "Ngày xuất bản không được để trống")
    @JsonFormat(pattern = "yyyy-MM-dd")
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



}



