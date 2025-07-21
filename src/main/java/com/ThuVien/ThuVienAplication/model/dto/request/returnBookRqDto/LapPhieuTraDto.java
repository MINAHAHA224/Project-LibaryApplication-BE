package com.ThuVien.ThuVienAplication.model.dto.request.returnBookRqDto;


import com.ThuVien.ThuVienAplication.model.dto.response.rentBookRp.SachChoMuonDto;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LapPhieuTraDto {
    private Long maphieu;
    private String ngaytra;
    private String manv;
   private List<SachTraDto> danhSachSach;
}
