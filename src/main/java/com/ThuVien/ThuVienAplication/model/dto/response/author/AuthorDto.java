package com.ThuVien.ThuVienAplication.model.dto.response.author; // Tạo package mới
import lombok.Data;
@Data
public class AuthorDto {
    private int maTacGia;
    private String hoTenTg;
    private String diaChiTg;
    private String dienThoaiTg;
}