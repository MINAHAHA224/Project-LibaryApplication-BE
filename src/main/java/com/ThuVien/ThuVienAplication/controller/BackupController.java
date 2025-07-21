package com.ThuVien.ThuVienAplication.controller;

import com.ThuVien.ThuVienAplication.model.dto.request.backupRq.BackupRequestDto;
import com.ThuVien.ThuVienAplication.model.dto.request.backupRq.RestoreRequestDto;
import com.ThuVien.ThuVienAplication.model.dto.response.backupRp.BackupHistoryDto;
import com.ThuVien.ThuVienAplication.model.dto.response.backupRp.DatabaseDto;
import com.ThuVien.ThuVienAplication.model.dto.response.staffRp.CurrentUserDto;
import com.ThuVien.ThuVienAplication.service.BackupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/backups")
@Slf4j
@RequiredArgsConstructor
public class BackupController {

    private final BackupService backupService;



    @GetMapping("/databases")
    public ResponseEntity<?> getDatabases() {

        List<DatabaseDto> listResult = this.backupService.handleGetListDatabase();
        return ResponseEntity.ok(listResult);
    }

    @PostMapping
    public ResponseEntity<?> backupDatabase(@RequestBody BackupRequestDto backupRequestDto, @AuthenticationPrincipal CurrentUserDto currentUserDto) {

        String username = currentUserDto!=null ? currentUserDto.getUsername() :"sa";
        String password = currentUserDto!=null ? currentUserDto.getPassword() :"123456";
        log.info("User '{}' is requesting a backup for DB: {}", username, backupRequestDto.getDbName());

        try {
            boolean backupSuccessful = this.backupService.handleBackupDatabase(backupRequestDto, username , password);
            if (backupSuccessful) {
                log.info("Sao lưu thành công cho DB: {}", backupRequestDto.getDbName());
                log.info("sao luu khong thanh cong thi se xoa session");

                return ResponseEntity.ok("Sao lưu thành công!");
            } else {

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Sao lưu thất bại. Vui lòng kiểm tra log server.");
            }
        } catch (Exception e) {
            log.error("Lỗi nghiêm trọng khi sao lưu DB: {} - {}", backupRequestDto.getDbName(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi sao lưu: " + e.getMessage());
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getBackupHistory(@RequestParam("dbName") String dbName) {
        List<BackupHistoryDto> listResult = this.backupService.handleGetListBackupHistory(dbName);
        if (listResult != null && !listResult.isEmpty()) {
            return ResponseEntity.ok(listResult);
        } else {
            return ResponseEntity.ok(List.of());
        }
    }

    @PostMapping("/restore")
    public ResponseEntity<?> restoreDatabase(@RequestBody RestoreRequestDto restoreRequestDto, @AuthenticationPrincipal CurrentUserDto currentUserDto) {
        String username =currentUserDto!=null ? currentUserDto.getUsername(): "Unknow";
        log.info("User '{}' is requesting to restore DB: {}", username, restoreRequestDto.getDbName());

        try {
            boolean handleRestore = this.backupService.handleRestoreDatabase(restoreRequestDto, username );
            if (handleRestore) {
                return ResponseEntity.ok("Phục hồi thành công! Vui lòng làm mới ứng dụng hoặc đăng nhập lại.");
            } else {
                throw  new RuntimeException("Phục hồi thất bại.");
            }
        } catch (Exception e) {
            log.error("Lỗi khi phục hồi DB: {} - {}", restoreRequestDto.getDbName(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi phục hồi: " + e.getMessage());
        }
    }

    @PostMapping("/log")
    public ResponseEntity<?> backupTransactionLog(@RequestBody Map<String, String> payload) {
        String dbName = payload.get("dbName");
        backupService.backupTransactionLog(dbName);
        return ResponseEntity.ok(Map.of("message", "Sao lưu Transaction Log thành công."));
    }
}