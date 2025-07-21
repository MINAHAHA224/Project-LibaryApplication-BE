package com.ThuVien.ThuVienAplication.controller;

import com.ThuVien.ThuVienAplication.model.dto.request.rentBookRq.ActiveReaderDto;
import com.ThuVien.ThuVienAplication.model.dto.request.rentBookRq.AvailableBookDto;
import com.ThuVien.ThuVienAplication.model.dto.request.rentBookRq.ChiTietPhieuDayDuDto;
import com.ThuVien.ThuVienAplication.model.dto.request.rentBookRq.RentBookRqDto;
import com.ThuVien.ThuVienAplication.model.dto.response.rentBookRp.*;
import com.ThuVien.ThuVienAplication.model.dto.response.staffRp.CurrentUserDto;
import com.ThuVien.ThuVienAplication.service.RentBookService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rentals")
@Slf4j
@RequiredArgsConstructor
public class RentBookController {

    private final RentBookService rentBookService;


    @PostMapping
    public ResponseEntity<?> createRental(@RequestBody RentBookRqDto rentBookRqDto, @AuthenticationPrincipal CurrentUserDto currentUserDto) {
        log.info("User '{}' is creating a new rental ticket.", currentUserDto.getUsername());
        try {
            rentBookService.handleLapPhieuMuon(rentBookRqDto);
            return ResponseEntity.ok(Map.of("message", "Lập phiếu mượn thành công!"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (SQLException | RuntimeException e) {
            log.error("System error while creating rental ticket: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Lỗi hệ thống khi lập phiếu mượn."));
        }
    }

    // --- APIs for getting data ---
    @GetMapping
    public ResponseEntity<List<LichSuMuonDto>> getRentalHistory() {
        return ResponseEntity.ok(rentBookService.handleGetListLichSuMuon());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChiTietPhieuDayDuDto> getRentalDetail(@PathVariable Long id) {
        return ResponseEntity.ok(rentBookService.handleGetDetailPhieuMuon(id));
    }

    // --- Helper APIs for form data ---
    @GetMapping("/new-ticket-id")
    public ResponseEntity<?> getNewRentalTicketId() {
        Long lastId = rentBookService.handleGetMaPhieuMoi();
        return ResponseEntity.ok(Map.of("newTicketId", lastId + 1));
    }

    @GetMapping("/staffs")
    public ResponseEntity<List<NhanVienDto>> getStaffList() {
        return ResponseEntity.ok(rentBookService.handleGetListNhanVien());
    }

    @GetMapping("/readers/{id}")
    public ResponseEntity<DocGiaDto> getActiveReader(@PathVariable Long id) {
        return ResponseEntity.ok(rentBookService.handleGetListDocGiaActive(id));
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<SachChoMuonDto> getAvailableBook(@PathVariable String id) {
        return ResponseEntity.ok(rentBookService.handleGetSachChoMuon(id));
    }

    @GetMapping("/reports/most-borrowed-preview-data")
    public ResponseEntity<List<MostBorrowedBooksDto>> getMostBorrowedReportData(
            @RequestParam String tuNgay,
            @RequestParam String denNgay) {
        try {
            return ResponseEntity.ok(rentBookService.fetchMostBorrowedData(tuNgay, denNgay));
        } catch (ParseException e) {
            // Ném lỗi để GlobalExceptionHandler bắt
            throw new IllegalArgumentException("Định dạng ngày không hợp lệ. Vui lòng dùng YYYY-MM-DD.");
        }
    }

    @GetMapping("/reports/most-borrowed")
    public void downloadMostBorrowedReport(@RequestParam String tuNgay, @RequestParam String denNgay, HttpServletResponse response, @AuthenticationPrincipal CurrentUserDto currentUserDto) {


        if (currentUserDto == null) {
            // Xử lý trường hợp không xác thực được
            return;
        }

        log.info("User '{}' ({}) requested to download book title report.", currentUserDto.getUsername(), currentUserDto.getHoTenDayDu());

        String usernameForReport = currentUserDto.getHoTenDayDu(); // Lấy họ tên đầy đủ

        try {
            List<MostBorrowedBooksDto> data = rentBookService.fetchMostBorrowedData(tuNgay, denNgay);
            if (data.isEmpty()) {
                response.setStatus(HttpStatus.NO_CONTENT.value());
                return;
            }
            // Chuyển đổi định dạng ngày để hiển thị đẹp hơn trong file Excel
            SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdfOutput = new SimpleDateFormat("dd/MM/yyyy");
            String tuNgayFormatted = sdfOutput.format(sdfInput.parse(tuNgay));
            String denNgayFormatted = sdfOutput.format(sdfInput.parse(denNgay));

            rentBookService.generateMostBorrowedBooksExcel(data,  usernameForReport, tuNgayFormatted, denNgayFormatted, response);
        } catch (IOException | ParseException e) {
            log.error("Error generating most borrowed report: {}", e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @PostMapping("/return-book")
    public ResponseEntity<?> returnBook(@RequestBody ReturnBookRequestDto requestDto) { // Tạo DTO này
        try {
            rentBookService.traSach(requestDto.getMaphieu(), requestDto.getMasach(), requestDto.getTinhTrang(), requestDto.getMaNV());
            return ResponseEntity.ok(Map.of("message", "Trả sách thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/available-books")
    public ResponseEntity<List<AvailableBookDto>> getAvailableBooks() {
        return ResponseEntity.ok(rentBookService.getAllAvailableBooks());
    }

    @GetMapping("/active-readers")
    public ResponseEntity<List<ActiveReaderDto>> getActiveReaders() {
        return ResponseEntity.ok(rentBookService.getAllActiveReaders());
    }
}