package com.ThuVien.ThuVienAplication.model.dto.request.bookTitleRq;

import lombok.Data;
import com.fasterxml.jackson.databind.JsonNode;

@Data
public class UndoActionDto {
    // Loại hành động gốc: "ADD", "UPDATE", "DELETE"
    private String actionType;

    // Dữ liệu liên quan, dùng JsonNode để linh hoạt
    // - Với ADD: sẽ chứa { codeBookTitle: "..." }
    // - Với UPDATE: sẽ chứa { oldData: {...}, newData: {...} }
    // - Với DELETE: sẽ chứa { originalData: {...} }
    private JsonNode data;
}