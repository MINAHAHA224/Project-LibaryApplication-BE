package com.ThuVien.ThuVienAplication.model.dto.request.staffRqDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StaffRqUpdateDto {

    private Integer maNV;

    @NotBlank(message = "HoNV không được để trống")
    private String hoNV;

    @NotBlank(message = "TenNV không được để trống")
    private String tenNV;

    @NotNull(message = "gioiTinh không được để trống")
    private Boolean gioiTinh;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String diaChi;

    @NotBlank(message = "Điện thoại không được để trống")
    @Pattern(
            regexp = "^(0\\d{9}|\\+84\\d{9})$",
            message = "Số điện thoại không hợp lệ (phải bắt đầu bằng 0 hoặc +84 và có 10 chữ số)"
    )
    private String dienThoai;

    @NotBlank(message = "Email không được để trống")
    @Pattern(
            regexp = "^[\\w.%+-]+@ptithcm\\.edu\\.vn$",
            message = "Email phải có định dạng hợp lệ và thuộc domain @ptithcm.edu.vn"
    )
    private String email;
}
