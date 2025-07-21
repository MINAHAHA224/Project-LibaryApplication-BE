package com.ThuVien.ThuVienAplication.model.dto.request.rentBookRq;

import lombok.*;
import java.util.Date;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChiTietPhieuDayDuDto {
    private Long maphieu;
    private String tendocgia;
    private Date ngaymuon;
    private int hinhthuc;
    private String tennv;
    private List<ChiTietSachMuonDto> danhSachSach;
}
