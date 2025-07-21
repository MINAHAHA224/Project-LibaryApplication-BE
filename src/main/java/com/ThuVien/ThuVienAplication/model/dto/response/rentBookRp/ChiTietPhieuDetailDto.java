package com.ThuVien.ThuVienAplication.model.dto.response.rentBookRp;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChiTietPhieuDetailDto {
        private Long maphieu;
        private String tendocgia;
        private String ngaymuon;
        private int hinhthuc;
        private String tennv;
        private int trangthaimuon;

        List<SachChoMuonDto> danhSachSach;

}
