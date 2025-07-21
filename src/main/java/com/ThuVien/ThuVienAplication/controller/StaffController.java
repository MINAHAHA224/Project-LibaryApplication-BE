package com.ThuVien.ThuVienAplication.controller;

import com.ThuVien.ThuVienAplication.model.dto.request.staffRqDto.StaffRqCreateDto;
import com.ThuVien.ThuVienAplication.model.dto.request.staffRqDto.StaffRqUpdateDto;
import com.ThuVien.ThuVienAplication.model.dto.request.staffRqDto.UndoActionDto;
import com.ThuVien.ThuVienAplication.model.dto.response.staffRp.StaffRpDto;
import com.ThuVien.ThuVienAplication.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/staffs")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @GetMapping
    public ResponseEntity<List<StaffRpDto>> getAllStaff() {
        return ResponseEntity.ok(staffService.getAllStaff());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffRpDto> getStaffById(@PathVariable Integer id) {
        StaffRpDto staff = staffService.getStaffById(id);
        return (staff != null) ? ResponseEntity.ok(staff) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> createStaff(@Valid @RequestBody StaffRqCreateDto request) {
        try {
            StaffRpDto createdStaff = staffService.createStaff(request);
            return new ResponseEntity<>(createdStaff, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStaff(@PathVariable Integer id, @Valid @RequestBody StaffRqUpdateDto request) {
        try {
            StaffRpDto updatedStaff = staffService.updateStaff(id, request);
            return ResponseEntity.ok(updatedStaff);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStaff(@PathVariable Integer id) {
        try {
            staffService.deleteStaff(id);
            return ResponseEntity.ok(Map.of("message", "Xóa nhân viên " + id + " thành công."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/undo")
    public ResponseEntity<?> undoStaffAction(@RequestBody UndoActionDto undoAction) {
        try {
            staffService.processUndo(undoAction);
            return ResponseEntity.ok(Map.of("message", "Hoàn tác hành động nhân viên thành công."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Lỗi khi hoàn tác: " + e.getMessage()));
        }
    }
}