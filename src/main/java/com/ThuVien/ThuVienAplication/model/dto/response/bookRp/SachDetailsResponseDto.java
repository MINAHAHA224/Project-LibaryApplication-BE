package com.ThuVien.ThuVienAplication.model.dto.response.bookRp;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SachDetailsResponseDto {
    private List<BookRpDto> sachList;
    private boolean hasPendingChanges;
}
