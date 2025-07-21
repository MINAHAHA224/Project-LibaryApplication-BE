package com.ThuVien.ThuVienAplication.model.dto.request.readerRqDto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class UndoActionDto {
    private String actionType; // "ADD", "UPDATE", "DELETE"
    private JsonNode data;
}