package com.ThuVien.ThuVienAplication.model.dto.request.backupRq;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BackupRequestDto {
    private String dbName;
    private boolean overwrite;
}
