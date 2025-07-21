package com.ThuVien.ThuVienAplication.model.dto.response.rentBookRp;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DocGiaDto {
    private String hoten;
    private Date ngaysinh;
    private String diachi;
    private String email;
    private Date ngayhethan;
    private Long madg;
    private int hoatdong;
}
