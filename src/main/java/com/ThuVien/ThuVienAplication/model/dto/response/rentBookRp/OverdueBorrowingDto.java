package com.ThuVien.ThuVienAplication.model.dto.response.rentBookRp;


import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OverdueBorrowingDto {
    private String soCmnd;
    private String hoDg;
    private String tenDg;
    private String dienThoai;
    private String emailDg;
    private String maSachGop;
    private String tenSachGop;
    private String ngayMuonGop;
    private String soNgayMuonQuaHanGop;
}
