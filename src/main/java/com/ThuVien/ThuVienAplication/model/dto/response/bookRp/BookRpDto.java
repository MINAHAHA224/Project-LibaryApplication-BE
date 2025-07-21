package com.ThuVien.ThuVienAplication.model.dto.response.bookRp;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookRpDto {
    private String ISBN;

    private String maSachCu;
    private boolean tinhTrangCu;
    private boolean choMuonCu;
    private int maNganTuCu;
    private String tenNganTuCu;

    private String maSach;
    private boolean tinhTrang;
    private boolean choMuon;
    private int maNganTu;
    private String tenNganTu;

    private boolean laSachGoc;

    private String status;

}
