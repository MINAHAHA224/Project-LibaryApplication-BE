package com.ThuVien.ThuVienAplication.service;

import com.ThuVien.ThuVienAplication.exception.SqlCustomException;
import com.ThuVien.ThuVienAplication.model.dto.request.staffRqDto.StaffRqCreateDto;
import com.ThuVien.ThuVienAplication.model.dto.request.staffRqDto.StaffRqUpdateDto;
import com.ThuVien.ThuVienAplication.model.dto.request.staffRqDto.UndoActionDto;
import com.ThuVien.ThuVienAplication.model.dto.response.staffRp.StaffRpDto;
import com.ThuVien.ThuVienAplication.storeprocedure.staff.QuerySpStaff;
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
public class StaffService {

    private final DataSource dataSource;
    private final ObjectMapper objectMapper;


    private StaffRpDto mapResultSetToStaffRpDto(ResultSet rs) throws SQLException {
        StaffRpDto staff = new StaffRpDto();
        staff.setMaNV(rs.getInt("MANV"));
        staff.setHoNV(rs.getString("HONV").trim());
        staff.setTenNV(rs.getString("TENNV").trim());
        staff.setGioiTinh(rs.getBoolean("GIOITINH"));
        staff.setDiaChi(rs.getString("DIACHI").trim());
        staff.setDienThoai(rs.getString("DIENTHOAI").trim());
        staff.setEmail(rs.getString("EMAIL").trim());
        return staff;
    }

    public List<StaffRpDto> getAllStaff() {
        List<StaffRpDto> result = new ArrayList<>();
        String sql = QuerySpStaff.SP_Thong_ke_nhan_vien;
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                result.add(mapResultSetToStaffRpDto(rs));
            }
        } catch (SQLException e) {
            log.error("Error getting all staff: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi lấy danh sách nhân viên", e);
        }
        return result;
    }

    public StaffRpDto getStaffById(Integer id) {
        String sql = QuerySpStaff.SP_Tim_nhan_vien;
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, id);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStaffRpDto(rs);
                }
            }
        } catch (SQLException e) {
            log.error("Error getting staff by id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tìm nhân viên", e);
        }
        return null;
    }


    public StaffRpDto createStaff(StaffRqCreateDto request) {


        // Kiểm tra email đã tồn tại chưa
        String email = request.getEmail().trim();
        existByEmail(email);


        String sql = QuerySpStaff.SP_Tao_moi_nhan_vien;
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, request.getHoNV().trim());
            cs.setString(2, request.getTenNV().trim());
            cs.setBoolean(3, request.getGioiTinh());
            cs.setString(4, request.getDiaChi().trim());
            cs.setString(5, request.getDienThoai().trim());
            cs.setString(6, request.getEmail().trim());

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    Integer newId = rs.getInt("MANV");
                    return getStaffById(newId);
                }
            }
        } catch (SQLException e) {
            log.error("Error creating staff: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tạo nhân viên: " + e.getMessage(), e);
        }
        throw new RuntimeException("Không thể tạo nhân viên mới.");
    }


    public StaffRpDto updateStaff(Integer id, StaffRqUpdateDto request) {

        boolean byBassExistEmailStaff = byBassExistEmailStaff(id, request.getEmail().trim());
        if (!byBassExistEmailStaff) {
            existByEmail(request.getEmail().trim());
        }

        String sql = QuerySpStaff.SP_Cap_nhat_nhan_vien;
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, id);
            cs.setString(2, request.getHoNV().trim());
            cs.setString(3, request.getTenNV().trim());
            cs.setBoolean(4, request.getGioiTinh());
            cs.setString(5, request.getDiaChi().trim());
            cs.setString(6, request.getDienThoai().trim());
            cs.setString(7, request.getEmail().trim());
            cs.execute();
            return getStaffById(id);
        } catch (SQLException e) {
            log.error("Error updating staff {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi cập nhật nhân viên: " + e.getMessage(), e);
        }
    }

    public void deleteStaff(Integer id) {
        String sql = QuerySpStaff.SP_Xoa_nhan_vien;
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, id);
            cs.execute();
        } catch (SQLException e) {
            log.error("Error deleting staff {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi xóa nhân viên. Có thể nhân viên này đã thực hiện giao dịch.", e);
        }
    }

    public void processUndo(UndoActionDto undoAction) throws Exception {
        String actionType = undoAction.getActionType();
        JsonNode data = undoAction.getData();

        if ("ADD".equals(actionType)) {
            Integer idToDelete = data.get("maNV").asInt();
            deleteStaff(idToDelete);
        } else if ("UPDATE".equals(actionType)) {
            StaffRqUpdateDto oldData = objectMapper.treeToValue(data.get("oldData"), StaffRqUpdateDto.class);
            updateStaff(oldData.getMaNV(), oldData);
        } else if ("DELETE".equals(actionType)) {
            StaffRpDto dataToRecreate = objectMapper.treeToValue(data.get("originalData"), StaffRpDto.class);
            createStaffUndo(dataToRecreate);
        } else {
            throw new IllegalArgumentException("Loại hành động hoàn tác không hợp lệ: " + actionType);
        }
    }

    private void createStaffUndo(StaffRpDto request) throws SQLException {
        String sql = QuerySpStaff.SP_Tao_moi_nhan_vien_undo;
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, request.getMaNV());
            cs.setString(2, request.getHoNV());
            cs.setString(3, request.getTenNV());
            cs.setBoolean(4, request.getGioiTinh());
            cs.setString(5, request.getDiaChi());
            cs.setString(6, request.getDienThoai());
            cs.setString(7, request.getEmail());
            cs.execute();
        }
    }


    private boolean byBassExistEmailStaff(Integer id, String email) {


        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall("{call SP_ByBassExistEmailStaff(?,?)}")) {
            cs.setInt(1, id);
            cs.setString(2, email);
            cs.execute();
            return true;
        } catch (SQLException e) {
            log.error("Email khác nhau = {}", e.getMessage(), e);
            return false;
        }

    }


    private void existByEmail(String email) {

        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall("{call SP_ExistByEmail(?)}")) {
            cs.setString(1, email);
            cs.execute();
        } catch (SQLException e) {
            log.error("Error ExistByEmail staff: {}", e.getMessage(), e);
            throw new SqlCustomException(e.getMessage());
        }

    }

}