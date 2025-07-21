package com.ThuVien.ThuVienAplication.service;

import com.ThuVien.ThuVienAplication.exception.SqlCustomException;
import com.ThuVien.ThuVienAplication.model.dto.request.LoginDto;
import com.ThuVien.ThuVienAplication.model.dto.request.account.AdminResetPasswordRequestDto;
import com.ThuVien.ThuVienAplication.model.dto.request.auth.ChangePasswordRequestDto;
import com.ThuVien.ThuVienAplication.model.dto.response.staffRp.CurrentUserDto;
import com.ThuVien.ThuVienAplication.utils.JwtUtil; // Sẽ tạo class này
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;



@Service
public class AuthService {

    @Value("${database.url}")
    private String url;

    @Value("${database.drive}")
    private String driver;

    private final JwtUtil jwtUtil; // Sẽ inject
    private final DataSource dataSource;

    private  final MailerService mailerService;
    private final PasswordService passwordService;

    public AuthService(JwtUtil jwtUtil, DataSource dataSource, MailerService mailerService, PasswordService passwordService) {
        this.jwtUtil = jwtUtil;
        this.dataSource = dataSource;
        this.mailerService = mailerService;
        this.passwordService = passwordService;
    }

    public Map<String, Object> loginAndGetJwt(LoginDto loginDto) throws Exception {
        try (Connection userConnection = DriverManager.getConnection(url, loginDto.getUsername(), loginDto.getPassword())) {

            CurrentUserDto userInfo = new CurrentUserDto();
            String sql = "{call SP_GetStaffInfoByCurrentLogin}";
            try (CallableStatement cs = userConnection.prepareCall(sql);
                 ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    userInfo.setMaNV(rs.getInt("MANV"));
                    userInfo.setHoTenDayDu(rs.getString("HoTenDayDu"));
                    userInfo.setUsername(rs.getString("LoginName"));
                    userInfo.setPassword( loginDto.getPassword());
                } else {
                    throw new Exception("Không tìm thấy thông tin nhân viên tương ứng với tài khoản đăng nhập.");
                }
            }

            String jwt = jwtUtil.generateToken(userInfo);

            Map<String, Object> result = new HashMap<>();
            result.put("token", jwt);
            result.put("user", userInfo);
                return result;

        } catch (SQLException | ClassNotFoundException e) {
            throw new Exception("Tên đăng nhập hoặc mật khẩu không chính xác.");
        }
    }

    public void changePassword(String loginName, ChangePasswordRequestDto request) throws SQLException {
        try (Connection conn = DriverManager.getConnection(url, loginName, request.getOldPassword())) {
        } catch (SQLException e) {
            throw new IllegalArgumentException("Mật khẩu cũ không chính xác.");
        }

        String sql = "{call SP_ChangePassword(?, ?, ?)}";
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, loginName);
            cs.setString(2, request.getOldPassword()); // SP có thể không cần, nhưng truyền vào cho đầy đủ
            cs.setString(3, request.getNewPassword());
            cs.execute();
        }
    }


    public void processForgotPassword(String loginName) throws MessagingException, UnsupportedEncodingException {
        String email = getEmailByLoginName(loginName);
        if (email == null || email.isEmpty()) {
            throw new SqlCustomException("Tên đăng nhập không tồn tại hoặc chưa được liên kết với email.");
        }

        String newPassword = RandomStringUtils.randomAlphanumeric(8);

        passwordService.adminResetPassword(new AdminResetPasswordRequestDto(loginName, newPassword));

        mailerService.sendConfirmLink(email, newPassword , loginName);
    }

    private String getEmailByLoginName(String loginName) {
        if ( loginName == null || loginName.isEmpty() ) {
            throw new RuntimeException("Email không được để trống.");
        }
        String sql = "{call SP_GetEmailByLoginName(?)}";
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, loginName.trim());
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("EMAIL");
                }
            }
        } catch (SQLException e) {
            throw new SqlCustomException(e.getMessage());
        }
        return null;
    }

}