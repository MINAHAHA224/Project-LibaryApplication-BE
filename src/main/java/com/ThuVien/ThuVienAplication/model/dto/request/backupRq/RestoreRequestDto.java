package com.ThuVien.ThuVienAplication.model.dto.request.backupRq;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RestoreRequestDto {
    @NotBlank(message = "Tên cơ sở dữ liệu không được để trống")
    private String dbName;

    private Integer backupFileNumber; // Có thể null nếu là PITR

    private boolean pitr; // true cho Point-in-Time Recovery

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime restoreDateTime; // "YYYY-MM-DDTHH:mm:ss", có thể null nếu không phải PITR

}
