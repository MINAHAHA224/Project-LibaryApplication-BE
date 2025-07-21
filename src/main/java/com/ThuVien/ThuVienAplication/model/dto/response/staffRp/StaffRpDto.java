package com.ThuVien.ThuVienAplication.model.dto.response.staffRp;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class StaffRpDto {
    private Integer maNV;
    private String hoNV;
    private String tenNV;
    private Boolean gioiTinh;
    private String diaChi;
    private String dienThoai;
    private String email;

}
