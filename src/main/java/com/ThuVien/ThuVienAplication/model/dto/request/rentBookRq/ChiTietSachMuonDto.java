package com.ThuVien.ThuVienAplication.model.dto.request.rentBookRq;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChiTietSachMuonDto {
    private String masach;
    private String tensach;
    private Date ngaytra;
    private boolean daTra;
    private String nhanVienNhanSach;
}
