package com.ThuVien.ThuVienAplication.model.dto.response.bookTitleRp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookTitleDisplayDtoExcel {
    private String codeBookTitle;    // ISBN
    private String nameBook;         // TENSACH
    private String nameAuthor;       // TENTACGIA (từ SP đã là STRING_AGG)
    private String formatBook;       // KHOSACH
    private String contentBook;      // NOIDUNG
    private String picturePath;      // HINHANHPATH
    private Date dateRelease;        // NGAYXUATBAN (java.util.Date)
    private Integer editions;         // LANXUATBAN ()
    private Integer pages;            // SOTRANG
    private Long price;              // GIA
    private String namePublisher;    // NHAXB
    private String nameCodeLanguage; // NGONNGU
    private String nameCodeType;     // THELOAI (Tên thể loại)
    private String maTl;             // MATL (Mã thể loại)
    private Integer soCuonThucTe;    // SoCuonThucTe (MỚI)
}
