package com.ThuVien.ThuVienAplication.controller;

import com.ThuVien.ThuVienAplication.model.dto.request.account.AdminResetPasswordRequestDto;
import com.ThuVien.ThuVienAplication.model.dto.response.account.SystemAccountDto;
import com.ThuVien.ThuVienAplication.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/passwords")
@RequiredArgsConstructor
public class PasswordController {
    private final PasswordService passwordService;

    @GetMapping("/created-logins")
    public ResponseEntity<List<SystemAccountDto>> getCreatedLogins() {
        return ResponseEntity.ok(passwordService.getAllCreatedLogins());
    }

    @PostMapping("/reset-for-user")
    public ResponseEntity<?> resetPasswordForUser(@RequestBody AdminResetPasswordRequestDto request) {
        passwordService.adminResetPassword(request);
        return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu cho tài khoản '" + request.getLoginName() + "' thành công."));
    }
}