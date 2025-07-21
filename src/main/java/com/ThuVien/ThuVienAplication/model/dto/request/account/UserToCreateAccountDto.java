package com.ThuVien.ThuVienAplication.model.dto.request.account;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserToCreateAccountDto {
    private Object id;
    private String ho;
    private String ten;
    private String email;
}
