package com.ThuVien.ThuVienAplication.model.dto.response.readerRp;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReaderRpDto {
    private Long madg;
    private String hodg;
    private String tendg;
    private String emaildg;
    private String socmnd;
    private Integer gioitinh;
    private Date ngaysinh;
    private String diachi;
    private String dienthoai;
    private Date ngaylamthe;
    private Date ngayhethan;
    private Integer hoatdong;
}
