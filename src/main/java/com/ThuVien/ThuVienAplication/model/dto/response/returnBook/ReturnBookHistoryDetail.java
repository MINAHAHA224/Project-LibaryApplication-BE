package com.ThuVien.ThuVienAplication.model.dto.response.returnBook;

import com.ThuVien.ThuVienAplication.model.dto.response.rentBookRp.SachChoMuonDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReturnBookHistoryDetail {
    private Long maphieu;
    private String tendocgia;
    private String ngaymuon;
    private String ngaytra;
    private int hinhthuc;
    private String tennv;
    private int trangthaimuon;

    List<SachChoMuonDto> danhSachSach;
}
