package com.ThuVien.ThuVienAplication.model.dto.request.account;

import lombok.*;



@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AdminResetPasswordRequestDto {
    private String loginName;
    private String newPassword;


}
