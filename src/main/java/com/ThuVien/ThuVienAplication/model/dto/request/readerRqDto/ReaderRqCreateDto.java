package com.ThuVien.ThuVienAplication.model.dto.request.readerRqDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Valid
public class ReaderRqCreateDto {

    @NotBlank(message = "Họ độc giả không được để trống")
    private String hodg;

    @NotBlank(message = "Tên độc giả không được để trống")
    private String tendg;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String emaildg;

    @NotBlank(message = "Số CMND không được để trống")
    @Pattern(regexp = "\\d{9,12}", message = "CMND/CCCD phải từ 9 đến 12 chữ số")
    private String socmnd;

    @NotNull(message = "Giới tính không được để trống")
    private Boolean gioitinh;

    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date ngaysinh;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String diachi;

    @NotBlank(message = "Điện thoại không được để trống")
    @Pattern(regexp = "\\d{10,11}", message = "Số điện thoại phải có 10 hoặc 11 chữ số")
    private String dienthoai;

    @NotNull(message = "Ngày làm thẻ không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date ngaylamthe;



    @NotNull(message = "Ngày hết hạn không được để trống")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Future(message = "Ngày hết hạn phải là ngày trong tương lai")
    private Date ngayhethan;

    @NotNull(message = "Trạng thái hoạt động không được để trống")
    private Boolean hoatdong;
}
