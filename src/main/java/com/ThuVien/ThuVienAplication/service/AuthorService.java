package com.ThuVien.ThuVienAplication.service;

import com.ThuVien.ThuVienAplication.exception.SqlCustomException;
import com.ThuVien.ThuVienAplication.model.dto.request.author.UndoActionDto;
import com.ThuVien.ThuVienAplication.model.dto.response.author.AuthorDto;
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
public class AuthorService {

    private final DataSource dataSource;
    private final ObjectMapper objectMapper;

    private AuthorDto mapResultSetToDto(ResultSet rs) throws SQLException {
        AuthorDto dto = new AuthorDto();
        dto.setMaTacGia(rs.getInt("MATACGIA"));
        dto.setHoTenTg(rs.getString("HOTENTG"));
        dto.setDiaChiTg(rs.getString("DIACHITG"));
        dto.setDienThoaiTg(rs.getString("DIENTHOAITG"));
        return dto;
    }

    public List<AuthorDto> getAllAuthors() {
        List<AuthorDto> list = new ArrayList<>();
        String sql = "{call SP_GetAllAuthors}";
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToDto(rs));
            }
        } catch (SQLException e) {
            log.error("Error getting all authors: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi lấy danh sách tác giả.", e);
        }
        return list;
    }

    public AuthorDto getAuthorById(int id) {
        String sql = "{call SP_GetAuthorById(?)}";
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, id);
            try (ResultSet rs = cs.executeQuery()) {
                if(rs.next()) {
                    return mapResultSetToDto(rs);
                }
            }
        } catch (SQLException e) {
            log.error("Error getting author by id {}: {}", id, e.getMessage(), e);
        }
        return null;
    }

    public AuthorDto createAuthor(AuthorDto dto) {
        String sql = "{call SP_CreateAuthor(?, ?, ?)}";
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, dto.getHoTenTg());
            cs.setString(2, dto.getDiaChiTg());
            cs.setString(3, dto.getDienThoaiTg());
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDto(rs);
                }
            }
        } catch (SQLException e) {
            throw new SqlCustomException(e.getMessage());
        }
        throw new RuntimeException("Không thể tạo tác giả mới.");
    }

    public AuthorDto updateAuthor(int id, AuthorDto dto) {
        String sql = "{call SP_UpdateAuthor(?, ?, ?, ?)}";
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, id);
            cs.setString(2, dto.getHoTenTg());
            cs.setString(3, dto.getDiaChiTg());
            cs.setString(4, dto.getDienThoaiTg());
            cs.executeUpdate();
            // Trả về dữ liệu đã cập nhật
            dto.setMaTacGia(id);
            return dto;
        } catch (SQLException e) {
            throw new SqlCustomException(e.getMessage());
        }
    }

    public void deleteAuthor(int id) {
        String sql = "{call SP_DeleteAuthor(?)}";
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, id);
            cs.execute();
        } catch (SQLException e) {
            throw new SqlCustomException(e.getMessage());
        }
    }

    public void processUndo(UndoActionDto undoAction) throws Exception {
        String actionType = undoAction.getActionType();
        JsonNode data = undoAction.getData();

        if ("ADD".equals(actionType)) {
            // Hành động gốc là ADD, giờ phải DELETE
            int idToDelete = data.get("maTacGia").asInt();
            deleteAuthor(idToDelete);
        }
        else if ("UPDATE".equals(actionType)) {
            // Hành động gốc là UPDATE, giờ phải UPDATE lại bằng oldData
            AuthorDto oldData = objectMapper.treeToValue(data.get("oldData"), AuthorDto.class);
            updateAuthor(oldData.getMaTacGia(), oldData);
        }
        else if ("DELETE".equals(actionType)) {
            // Hành động gốc là DELETE, giờ phải ADD lại
            AuthorDto dataToRecreate = objectMapper.treeToValue(data.get("originalData"), AuthorDto.class);
            createAuthor(dataToRecreate);
        } else {
            throw new IllegalArgumentException("Loại hành động hoàn tác không hợp lệ: " + actionType);
        }
    }
}