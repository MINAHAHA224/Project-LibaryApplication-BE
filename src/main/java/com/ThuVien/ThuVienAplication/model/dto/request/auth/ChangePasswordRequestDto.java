package com.ThuVien.ThuVienAplication.model.dto.request.auth;


import lombok.Data;

@Data
public class ChangePasswordRequestDto {
    private String oldPassword;
    private String newPassword;
}
