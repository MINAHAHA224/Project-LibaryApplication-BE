package com.ThuVien.ThuVienAplication.controller;

import com.ThuVien.ThuVienAplication.model.dto.request.account.CreateAccountRequestDto;
import com.ThuVien.ThuVienAplication.model.dto.request.account.UserToCreateAccountDto;
import com.ThuVien.ThuVienAplication.service.AccountManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountManagementController {

    private final AccountManagementService accountService;

    @GetMapping("/staffs-no-login")
    public ResponseEntity<List<UserToCreateAccountDto>> getStaffsWithoutLogin() {
        return ResponseEntity.ok(accountService.getStaffsWithoutLogin());
    }

    @GetMapping("/readers-no-login")
    public ResponseEntity<List<UserToCreateAccountDto>> getReadersWithoutLogin() {
        return ResponseEntity.ok(accountService.getReadersWithoutLogin());
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@RequestBody CreateAccountRequestDto request) {
        accountService.createAccount(request);
        return ResponseEntity.ok(Map.of("message", "Tạo tài khoản '" + request.getLoginName() + "' thành công."));
    }




}