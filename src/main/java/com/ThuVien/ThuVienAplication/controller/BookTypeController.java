package com.ThuVien.ThuVienAplication.controller;

import com.ThuVien.ThuVienAplication.model.dto.request.bookType.UndoActionDto;
import com.ThuVien.ThuVienAplication.model.dto.response.bookTitleRp.BookTypeDto;
import com.ThuVien.ThuVienAplication.service.BookTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/book-types")
@RequiredArgsConstructor
public class BookTypeController {

    private final BookTypeService bookTypeService;

    @GetMapping
    public ResponseEntity<List<BookTypeDto>> getAll() {
        return ResponseEntity.ok(bookTypeService.getAllBookTypes());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody BookTypeDto dto) {

            bookTypeService.createBookType(dto);
            return new ResponseEntity<>(dto, HttpStatus.CREATED);

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody BookTypeDto dto) {
        try {
            // id từ path là mã cũ, dto.getId() là mã mới người dùng nhập
            bookTypeService.updateBookType(id, dto);

            // Trả về dữ liệu đã được cập nhật
            BookTypeDto updatedData = new BookTypeDto(dto.getId(), dto.getName());
            return ResponseEntity.ok(updatedData);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        try {
            bookTypeService.deleteBookType(id);
            return ResponseEntity.ok(Map.of("message", "Xóa thành công thể loại " + id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/undo")
    public ResponseEntity<?> undoBookTypeAction(@RequestBody UndoActionDto undoAction) {
        try {
            bookTypeService.processUndo(undoAction);
            return ResponseEntity.ok(Map.of("message", "Hoàn tác hành động thể loại thành công."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Lỗi khi hoàn tác: " + e.getMessage()));
        }
    }
}