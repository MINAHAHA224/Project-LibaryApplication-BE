package com.ThuVien.ThuVienAplication.model.dto.response.rentBookRp;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LichSuMuonDto {
    private Long maphieu;
    private String tendocgia;
    private Date ngaymuon;
    private String hinhthuc;
    private int sosach;
    private String tennv;
    private boolean trangthaitra;

}
