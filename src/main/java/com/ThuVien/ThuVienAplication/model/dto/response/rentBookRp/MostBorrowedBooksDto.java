package com.ThuVien.ThuVienAplication.model.dto.response.rentBookRp;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MostBorrowedBooksDto {
    private String isbn;
    private String tenSach;
    private String tacGia; // MỚI: Chuỗi các tác giả, cách nhau bằng dấu phẩy
    private String theLoai; // MỚI
    private Integer soLuotMuon;
    private String ghiChu; // MỚI: Tạm thời có thể là null
}
