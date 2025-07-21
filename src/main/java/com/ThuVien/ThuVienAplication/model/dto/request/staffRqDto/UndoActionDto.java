package com.ThuVien.ThuVienAplication.model.dto.request.staffRqDto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class UndoActionDto {
    private String actionType;
    private JsonNode data;
}