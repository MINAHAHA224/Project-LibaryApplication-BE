package com.ThuVien.ThuVienAplication.controller;

import com.ThuVien.ThuVienAplication.exception.SqlCustomException;
import com.ThuVien.ThuVienAplication.model.dto.request.readerRqDto.ReaderRqCreateDto;
import com.ThuVien.ThuVienAplication.model.dto.request.readerRqDto.ReaderRqUpdateDto;
import com.ThuVien.ThuVienAplication.model.dto.request.readerRqDto.UndoActionDto;
import com.ThuVien.ThuVienAplication.model.dto.response.readerRp.ReaderRpDto;
import com.ThuVien.ThuVienAplication.model.dto.response.rentBookRp.OverdueBorrowingDto;
import com.ThuVien.ThuVienAplication.model.dto.response.staffRp.CurrentUserDto;
import com.ThuVien.ThuVienAplication.service.ReaderService;
import com.ThuVien.ThuVienAplication.service.RentBookService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/readers")
@RequiredArgsConstructor
public class ReaderController {

    private final ReaderService readerService;
    private final RentBookService rentBookService;

    @GetMapping
    public ResponseEntity<List<ReaderRpDto>> getAllReaders() {
        return ResponseEntity.ok(readerService.getAllReaders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReaderRpDto> getReaderById(@PathVariable Long id) {
        ReaderRpDto reader = readerService.getReaderById(id);
        return (reader != null) ? ResponseEntity.ok(reader) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> createReader(@Valid @RequestBody ReaderRqCreateDto request) {
        try {
            ReaderRpDto createdReader = readerService.createReader(request);
            return new ResponseEntity<>(createdReader, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReader(@PathVariable Long id, @Valid @RequestBody ReaderRqUpdateDto request) {
        try {
            ReaderRpDto updatedReader = readerService.updateReader(id, request);
            return ResponseEntity.ok(updatedReader);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReader(@PathVariable Long id) {
        try {
            readerService.deleteReader(id);
            return ResponseEntity.ok(Map.of("message", "Xóa độc giả " + id + " thành công."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    // --- API for Report ---
    @GetMapping("/report/excel")
    public void downloadReadersReport(HttpServletResponse response, @AuthenticationPrincipal CurrentUserDto currentUserDto) {
        log.info("User '{}' requested readers report.", currentUserDto.getUsername());
        try {
            List<ReaderRpDto> data = readerService.getAllReaders();
            if (data.isEmpty()) {
                response.setStatus(HttpStatus.NO_CONTENT.value());
                return;
            }
            readerService.generateReadersExcelReport(data, currentUserDto.getHoTenDayDu(), response);
        } catch (Exception e) {
            log.error("Error generating readers report: {}", e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @PostMapping("/undo")
    public ResponseEntity<?> undoReaderAction(@RequestBody UndoActionDto undoAction) {
        try {
            readerService.processUndo(undoAction);
            return ResponseEntity.ok(Map.of("message", "Hoàn tác hành động độc giả thành công."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Lỗi khi hoàn tác: " + e.getMessage()));
        }
    }



    @GetMapping("/reports/overdue-preview-data")
    public ResponseEntity<List<OverdueBorrowingDto>> getOverdueReportData() {
        return ResponseEntity.ok(rentBookService.fetchOverdueData());
    }

    @GetMapping("/reports/overdue-borrowings")
    public void downloadOverdueBorrowingsReport(HttpServletResponse response, @AuthenticationPrincipal CurrentUserDto currentUserDto) {
        log.info("User '{}' requested overdue borrowings report.", currentUserDto.getUsername());
        try {
            String username = (currentUserDto != null) ? currentUserDto.getHoTenDayDu() : "sa";
            List<OverdueBorrowingDto> data = rentBookService.fetchOverdueData();
            if (data.isEmpty()) {
                response.setStatus(HttpStatus.NO_CONTENT.value());
                return;
            }

            rentBookService.generateOverdueBorrowingsExcel(data, username, response);
        } catch (Exception e) {
           throw new SqlCustomException("Loi xuat Excel: " + e.getMessage());
        }
    }
}