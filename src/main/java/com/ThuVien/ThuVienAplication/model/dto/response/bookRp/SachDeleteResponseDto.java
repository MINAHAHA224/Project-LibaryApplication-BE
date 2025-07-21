package com.ThuVien.ThuVienAplication.model.dto.response.bookRp;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SachDeleteResponseDto {

    private boolean success;
    private String message;

    private boolean hasPendingChanges;
}
