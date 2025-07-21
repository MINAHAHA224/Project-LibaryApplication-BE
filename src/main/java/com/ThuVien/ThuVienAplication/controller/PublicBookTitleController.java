package com.ThuVien.ThuVienAplication.controller;

import com.ThuVien.ThuVienAplication.model.dto.response.bookRp.BookRpDto;
import com.ThuVien.ThuVienAplication.model.dto.response.bookTitleRp.BookTitleDisplayDto;
import com.ThuVien.ThuVienAplication.service.BookService;
import com.ThuVien.ThuVienAplication.service.BookTitleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/book-titles")
@RequiredArgsConstructor
public class PublicBookTitleController {

    private final BookTitleService bookTitleService;
    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookTitleDisplayDto>> getAllBookTitles() {
        return ResponseEntity.ok(bookTitleService.getAllBookTitles());
    }

    @GetMapping("/{isbn}/books")
    public ResponseEntity<List<BookRpDto>> getBooksOfTitle(@PathVariable String isbn) {
        return ResponseEntity.ok(bookService.getBooksByIsbn(isbn));
    }
}