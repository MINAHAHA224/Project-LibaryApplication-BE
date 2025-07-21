package com.ThuVien.ThuVienAplication.model.dto.listsession;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StaffSession {

    private int maNV;

    private String hoNV;
    private String tenNV;
    private Boolean gioiTinh;
    private String diaChi;
    private String dienThoai;
    private String email;


    private String hoNVMoi;
    private String tenNVMoi;
    private Boolean gioiTinhMoi;
    private String diaChiMoi;
    private String dienThoaiMoi;
    private String emailMoi;

    private String action;
}
