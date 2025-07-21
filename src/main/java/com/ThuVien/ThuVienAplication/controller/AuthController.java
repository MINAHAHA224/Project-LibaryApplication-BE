package com.ThuVien.ThuVienAplication.controller;

import com.ThuVien.ThuVienAplication.model.dto.request.LoginDto;
import com.ThuVien.ThuVienAplication.model.dto.request.auth.ChangePasswordRequestDto;
import com.ThuVien.ThuVienAplication.model.dto.response.staffRp.CurrentUserDto;
import com.ThuVien.ThuVienAplication.service.AuthService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        try {
            Map<String, Object> loginResult = authService.loginAndGetJwt(loginDto);
            return ResponseEntity.ok(loginResult);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequestDto request, @AuthenticationPrincipal CurrentUserDto currentUserDto) {
        if (currentUserDto == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            authService.changePassword(currentUserDto.getUsername(), request);
            return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }



    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) throws MessagingException, UnsupportedEncodingException {
        String loginName = request.get("loginName");
        if (loginName == null || loginName.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tên đăng nhập không được để trống."));
        }
        try {
            authService.processForgotPassword(loginName);
            return ResponseEntity.ok(Map.of("message", "Thành công! Mật khẩu mới đã được gửi đến email liên kết với tài khoản của bạn."));
        } catch (Exception e) {
            throw e;
        }
    }
}
