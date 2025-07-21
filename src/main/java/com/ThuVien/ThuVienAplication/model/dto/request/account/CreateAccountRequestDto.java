package com.ThuVien.ThuVienAplication.model.dto.request.account;

import lombok.Data;

@Data
public class CreateAccountRequestDto {
    private String loginName;
    private String password;
    private String userType; // 'NHANVIEN' hoặc 'DOCGIA'
    private String userId;   // MANV hoặc MADG
}
