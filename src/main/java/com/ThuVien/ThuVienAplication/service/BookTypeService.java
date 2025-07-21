package com.ThuVien.ThuVienAplication.service;

import com.ThuVien.ThuVienAplication.exception.SqlCustomException;
import com.ThuVien.ThuVienAplication.model.dto.request.bookType.UndoActionDto;
import com.ThuVien.ThuVienAplication.model.dto.response.bookTitleRp.BookTypeDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookTypeService {
    private final DataSource dataSource;
    private final ObjectMapper objectMapper; // Inject ObjectMapper
    public List<BookTypeDto> getAllBookTypes() {
        List<BookTypeDto> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall("{call SP_GetAllBookTypes}");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                list.add(new BookTypeDto(rs.getString("MATL").trim(), rs.getString("THELOAI")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách thể loại.", e);
        }
        return list;
    }

    public void createBookType(BookTypeDto dto) {
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall("{call SP_CreateBookType(?, ?)}")) {
            cs.setString(1, dto.getId());
            cs.setString(2, dto.getName());
            cs.execute();
        } catch (SQLException e) {
            log.error("Error creating book type: {}", e.getMessage(), e);
            throw new SqlCustomException(e.getMessage());
        }
    }

    public void updateBookType(String oldId, BookTypeDto dto) {
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall("{call SP_UpdateBookType(?, ?, ?)}")) {
            cs.setString(1, oldId.trim());        // @MATL_CU
            cs.setString(2, dto.getId().trim());   // @MATL_MOI
            cs.setString(3, dto.getName().trim()); // @THELOAI_MOI
            cs.execute();
        } catch (SQLException e) {
            throw new SqlCustomException(e.getMessage());
        }
    }

    public void deleteBookType(String id) {
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall("{call SP_DeleteBookType(?)}")) {
            cs.setString(1, id);
            cs.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void processUndo(UndoActionDto undoAction) throws Exception {
        String actionType = undoAction.getActionType();
        JsonNode data = undoAction.getData();

        if ("ADD".equals(actionType)) {
            // Hành động gốc là ADD, giờ phải DELETE
            String idToDelete = data.get("id").asText();
            deleteBookType(idToDelete);
        }
        else if ("UPDATE".equals(actionType)) {
            // Hành động gốc là UPDATE, giờ phải UPDATE lại bằng oldData
            BookTypeDto oldData = objectMapper.treeToValue(data.get("oldData"), BookTypeDto.class);
            BookTypeDto newData = objectMapper.treeToValue(data.get("newData"), BookTypeDto.class);

            // Hoàn tác: tìm theo id MỚI và cập nhật thành id CŨ
            updateBookType(newData.getId(), oldData);
        }
        else if ("DELETE".equals(actionType)) {
            // Hành động gốc là DELETE, giờ phải ADD lại
            BookTypeDto dataToRecreate = objectMapper.treeToValue(data.get("originalData"), BookTypeDto.class);
            createBookType(dataToRecreate);
        } else {
            throw new IllegalArgumentException("Loại hành động hoàn tác không hợp lệ: " + actionType);
        }
    }
}