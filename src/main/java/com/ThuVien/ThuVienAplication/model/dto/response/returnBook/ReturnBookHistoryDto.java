package com.ThuVien.ThuVienAplication.model.dto.response.returnBook;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReturnBookHistoryDto {
    private Long maphieu;
    private String tendocgia;
    private Date ngaytra;
    private Long sosach;
    private Long hinhthuc;
    private String tennv;
}
