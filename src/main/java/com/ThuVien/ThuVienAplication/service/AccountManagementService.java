package com.ThuVien.ThuVienAplication.service;

import com.ThuVien.ThuVienAplication.exception.SqlCustomException;
import com.ThuVien.ThuVienAplication.model.dto.request.account.CreateAccountRequestDto;
import com.ThuVien.ThuVienAplication.model.dto.request.account.UserToCreateAccountDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountManagementService {

    private final DataSource dataSource;

    public List<UserToCreateAccountDto> getStaffsWithoutLogin() {
        List<UserToCreateAccountDto> list = new ArrayList<>();
        String sql = "{call SP_GetStaffWithoutLogin}";
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                list.add(new UserToCreateAccountDto(
                        rs.getInt("MANV"),
                        rs.getString("HONV"),
                        rs.getString("TENNV"),
                        rs.getString("EMAIL")
                ));
            }
        } catch (SQLException e) {
            log.error("Error getting staff without login: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi lấy danh sách nhân viên chưa có tài khoản.", e);
        }
        return list;
    }

    public List<UserToCreateAccountDto> getReadersWithoutLogin() {
        List<UserToCreateAccountDto> list = new ArrayList<>();
        String sql = "{call SP_GetReadersWithoutLogin}";
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                list.add(new UserToCreateAccountDto(
                        rs.getLong("MADG"),
                        rs.getString("HODG"),
                        rs.getString("TENDG"),
                        rs.getString("EMAILDG")
                ));
            }
        } catch (SQLException e) {
            log.error("Error getting readers without login: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi lấy danh sách độc giả chưa có tài khoản.", e);
        }
        return list;
    }

    public void createAccount(CreateAccountRequestDto request) {
        String sql = "{call SP_CreateAccount(?, ?, ?, ?)}";
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, request.getLoginName());
            cs.setString(2, request.getPassword());
            cs.setString(3, request.getUserType());
            cs.setString(4, request.getUserId());
            cs.execute();
        } catch (SQLException e) {
            log.error("Error creating account for login '{}': {}", request.getLoginName(), e.getMessage(), e);
            throw new SqlCustomException(e.getMessage());
        }
    }





}