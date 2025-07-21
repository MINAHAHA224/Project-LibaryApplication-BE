package com.ThuVien.ThuVienAplication.model.dto.response.staffRp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUserDto {

    private String username;


    private Integer maNV;


    private String hoTenDayDu;


    private String email;

    private String password;

}
