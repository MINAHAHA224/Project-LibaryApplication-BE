package com.ThuVien.ThuVienAplication.model.dto.response.rentBookRp;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SachChoMuonDto {

    private String masach;
    private String tensach;
    private int tinhtrang;
    private int chomuon;
    private int tinhtrangchomuon = 1;
    private String ghichu;
}
