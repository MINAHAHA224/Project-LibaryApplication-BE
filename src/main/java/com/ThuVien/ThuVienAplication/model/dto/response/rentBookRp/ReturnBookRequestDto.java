package com.ThuVien.ThuVienAplication.model.dto.response.rentBookRp;

import lombok.*;

import java.sql.Timestamp;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReturnBookRequestDto {

    private Long maphieu;
    private String masach;
    private String tinhTrang;
    private Integer maNV;
}
