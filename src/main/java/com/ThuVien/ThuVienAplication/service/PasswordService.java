package com.ThuVien.ThuVienAplication.service;

import com.ThuVien.ThuVienAplication.exception.SqlCustomException;
import com.ThuVien.ThuVienAplication.model.dto.request.account.AdminResetPasswordRequestDto;
import com.ThuVien.ThuVienAplication.model.dto.response.account.SystemAccountDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PasswordService {
    private final DataSource dataSource;

    public List<SystemAccountDto> getAllCreatedLogins() {
        List<SystemAccountDto> list = new ArrayList<>();
        String sql = "{call SP_GetAllCreatedLogins}";
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                SystemAccountDto dto = new SystemAccountDto();
                dto.setUserType(rs.getString("UserType"));
                dto.setUserId(rs.getLong("UserId"));
                dto.setFullName(rs.getString("FullName"));
                dto.setLoginName(rs.getString("LoginName"));
                list.add(dto);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách tài khoản.", e);
        }
        return list;
    }

    public void adminResetPassword(AdminResetPasswordRequestDto request) {
        String sql = "{call SP_AdminResetPassword(?, ?)}";
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, request.getLoginName());
            cs.setString(2, request.getNewPassword());
            cs.execute();
        } catch (SQLException e) {
            throw new SqlCustomException(e.getMessage());
        }
    }
}