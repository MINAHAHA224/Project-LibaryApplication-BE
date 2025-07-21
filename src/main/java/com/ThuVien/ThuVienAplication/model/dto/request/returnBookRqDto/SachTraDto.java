package com.ThuVien.ThuVienAplication.model.dto.request.returnBookRqDto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SachTraDto {
    private String masach;
    private String tensach;
    private String tinhtrang;
    private int chomuon;
    private int tinhtrangchomuon = 1;
    private String ghichu;
}
