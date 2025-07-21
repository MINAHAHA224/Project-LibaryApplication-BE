package com.ThuVien.ThuVienAplication.model.dto.request.bookRqDto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class UndoBookActionDto {
    // Loại hành động gốc: "ADD", "UPDATE", "DELETE"
    private String actionType;

    // Dữ liệu liên quan, dùng JsonNode để linh hoạt
    // - Với ADD: sẽ chứa { codeBookTitle: "..." }
    // - Với UPDATE: sẽ chứa { oldData: {...}, newData: {...} }
    // - Với DELETE: sẽ chứa { originalData: {...} }
    private JsonNode data;
}
