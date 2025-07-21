package com.ThuVien.ThuVienAplication.model.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginDto {


    private String username;
    private String password;
    private String role;
}
