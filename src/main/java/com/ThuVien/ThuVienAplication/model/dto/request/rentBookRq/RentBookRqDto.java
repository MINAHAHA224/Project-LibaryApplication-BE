package com.ThuVien.ThuVienAplication.model.dto.request.rentBookRq;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RentBookRqDto {
    private String maphieu;
    private String madg;
    private String ngaymuon;
    private String hinhthuc;
    private String manv;
    private List<String> danhSachSach;
}
