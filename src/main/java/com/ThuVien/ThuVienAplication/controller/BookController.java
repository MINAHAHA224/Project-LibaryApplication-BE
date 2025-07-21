package com.ThuVien.ThuVienAplication.controller;

import com.ThuVien.ThuVienAplication.model.dto.request.bookRqDto.UndoBookActionDto; // Tạo DTO này
import com.ThuVien.ThuVienAplication.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book-titles/books")
public class BookController {
    private final BookService bookService;
    // ...
    @PostMapping("/undo")
    public ResponseEntity<?> undoBookAction(@RequestBody UndoBookActionDto undoAction) {
        try {
            bookService.processUndo(undoAction);
            return ResponseEntity.ok(Map.of("message", "Hoàn tác sách thành công."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}