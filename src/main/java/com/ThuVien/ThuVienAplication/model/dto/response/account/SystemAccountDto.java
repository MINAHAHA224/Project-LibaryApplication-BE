package com.ThuVien.ThuVienAplication.model.dto.response.account;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SystemAccountDto {
    private String userType;
    private Long userId;
    private String fullName;
    private String loginName;
}
