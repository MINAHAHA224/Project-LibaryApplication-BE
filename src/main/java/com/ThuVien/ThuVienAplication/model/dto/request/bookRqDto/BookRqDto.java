package com.ThuVien.ThuVienAplication.model.dto.request.bookRqDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookRqDto {
    @JsonProperty("MASACHCU")
    private String maSachCu;
    @JsonProperty("TINHTRANGCU")
    private boolean tinhTrangCu;
    @JsonProperty("CHOMUONCU")
    private boolean choMuonCu;
    @JsonProperty("MANGANTUCU")
    private int maNganTuCu;


    @JsonProperty("MASACH")
    private String maSach;
    @JsonProperty("TINHTRANG")
    private boolean tinhTrang;
    @JsonProperty("CHOMUON")
    private boolean choMuon;
    @JsonProperty("MANGANTU")
    private int maNganTu;

    @JsonProperty("ACTIONOLD")
    private String action;
}
