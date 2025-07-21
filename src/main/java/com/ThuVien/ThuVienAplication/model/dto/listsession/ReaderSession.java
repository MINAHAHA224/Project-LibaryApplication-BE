package com.ThuVien.ThuVienAplication.model.dto.listsession;


import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReaderSession {
    private Long madg;

    private String hodg;
    private String tendg;
    private String emaildg;
    private String socmnd;
    private Boolean gioitinh;
    private Date ngaysinh;
    private String diachi;
    private String dienthoai;
    private Date ngaylamthe;
    private Date ngayhethan;
    private Boolean hoatdong;


    private String hodgMoi;
    private String tendgMoi;
    private String emaildgMoi;
    private String socmndMoi;
    private Boolean gioitinhMoi;
    private Date ngaysinhMoi;
    private String diachiMoi;
    private String dienthoaiMoi;
    private Date ngaylamtheMoi;
    private Date ngayhethanMoi;
    private Boolean hoatdongMoi;

    private String action;


}
