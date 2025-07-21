package com.ThuVien.ThuVienAplication.controller;

import com.ThuVien.ThuVienAplication.exception.SqlCustomException;
import com.ThuVien.ThuVienAplication.model.dto.request.bookTitleRq.BookTitleRqDto;
import com.ThuVien.ThuVienAplication.model.dto.request.bookTitleRq.UndoActionDto;
import com.ThuVien.ThuVienAplication.model.dto.response.bookRp.BookRpDto;
import com.ThuVien.ThuVienAplication.model.dto.response.bookRp.DrawerRpDto;
import com.ThuVien.ThuVienAplication.model.dto.response.bookTitleRp.*;
import com.ThuVien.ThuVienAplication.model.dto.response.staffRp.CurrentUserDto;
import com.ThuVien.ThuVienAplication.service.BookService;
import com.ThuVien.ThuVienAplication.service.BookTitleService;
import com.ThuVien.ThuVienAplication.service.ImagesService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/book-titles")
@Slf4j
@RequiredArgsConstructor
public class BookTitleController {

    private final BookTitleService bookTitleService;
    private final BookService bookService;
    private final ImagesService imagesService;



    @GetMapping
    public ResponseEntity<List<BookTitleDisplayDto>> getAllBookTitles() {
        return ResponseEntity.ok(bookTitleService.getAllBookTitles());
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<?> getBookTitleByIsbn(@PathVariable String isbn) {
        BookTitleRqDto bookTitle = bookTitleService.getBookTitleByIsbn(isbn);
        return bookTitle != null ? ResponseEntity.ok(bookTitle) : ResponseEntity.notFound().build();
    }

    @PostMapping("/{isbn}/upload-image")
    public ResponseEntity<?> uploadBookImage(
            @PathVariable String isbn,
            @RequestParam("imageFile") MultipartFile imageFile
    ) {
        try {
            String newImagePath = bookTitleService.updateBookImage(isbn, imageFile);
            return ResponseEntity.ok(Map.of(
                    "message", "Cập nhật ảnh thành công!",
                    "imagePath", newImagePath
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lỗi khi cập nhật ảnh: " + e.getMessage()));
        }
    }


    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> createBookTitle(@Valid @RequestPart("bookTitleData") BookTitleRqDto request,
                                             @RequestPart(value = "imageFile", required = false) MultipartFile file) {
        try {
            if (file != null && !file.isEmpty()) {
                request.setPicturePath(imagesService.handleUploadFile(file, "dausach"));
            } else {
                request.setPicturePath("example.jpg");
            }
            BookTitleDisplayDto createdBook = bookTitleService.createBookTitle(request);
            return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping(value = "/{isbn}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateBookTitle(
            @PathVariable String isbn,
            @Valid @RequestPart("bookTitleData") BookTitleRqDto request,
            @RequestPart(value = "imageFile", required = false) MultipartFile file
    ) {
        try {

            BookTitleDisplayDto updatedBook = bookTitleService.updateBookTitle(isbn, request, file);
            return ResponseEntity.ok(updatedBook);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<?> deleteBookTitle(@PathVariable String isbn) {
        try {
            bookTitleService.deleteBookTitle(isbn);
            return ResponseEntity.ok(Map.of("message", "Xóa đầu sách " + isbn + " thành công."));
        } catch (IllegalStateException e) {
           throw new SqlCustomException("Đầu sách đang có sách con, không thể xóa.");
        } catch (RuntimeException e) {
            throw new SqlCustomException("Đầu sách đang có sách con, không thể xóa.");        }
    }


    @GetMapping("/form-data/authors")
    public ResponseEntity<List<AuthorDto>> getAuthorsForForm() {
        return ResponseEntity.ok(bookTitleService.getAuthors());
    }

    @GetMapping("/form-data/languages")
    public ResponseEntity<List<LanguageDto>> getLanguagesForForm() {
        return ResponseEntity.ok(bookTitleService.getLanguages());
    }

    @GetMapping("/form-data/book-types")
    public ResponseEntity<List<BookTypeDto>> getBookTypesForForm() {
        return ResponseEntity.ok(bookTitleService.getBookTypes());
    }



    @GetMapping("/{isbn}/books")
    public ResponseEntity<List<BookRpDto>> getBooksOfTitle(@PathVariable String isbn) {
        return ResponseEntity.ok(bookService.getBooksByIsbn(isbn));
    }

    @GetMapping("/books/drawers")
    public ResponseEntity<List<DrawerRpDto>> getDrawers() {
        return ResponseEntity.ok(bookService.handleGetListNganTu());
    }

    @PostMapping("/{isbn}/books")
    public ResponseEntity<?> createBook(@PathVariable String isbn, @RequestBody BookRpDto bookDto) {
        try {
            BookRpDto createdBook = bookService.createBook(isbn, bookDto);
            return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }



    @PutMapping("/{isbn}/books/{oldBookId}")
    public ResponseEntity<?> updateBook(
            @PathVariable String isbn,
            @PathVariable String oldBookId,
            @RequestBody BookRpDto bookDto) {
        try {
            BookRpDto updatedBook = bookService.updateBook(isbn, oldBookId, bookDto);
            return ResponseEntity.ok(updatedBook);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{isbn}/books/{bookId}")
    public ResponseEntity<?> deleteBook(@PathVariable String isbn, @PathVariable String bookId) {
        try {
            bookService.deleteBook(isbn, bookId);
            return ResponseEntity.ok(Map.of("message", "Xóa sách " + bookId + " thành công."));
        } catch (IllegalStateException e) {
            throw new SqlCustomException("Không thể xóa sách này vì nó đang được mượn.");
        } catch (RuntimeException e) {
            throw new SqlCustomException("Không thể xóa sách này vì nó đang được mượn.");
        }
    }

    @GetMapping("/report/preview-data")
    public ResponseEntity<List<BookTitleDisplayDtoExcel>> getBookTitleReportData() {
        List<BookTitleDisplayDtoExcel> data = bookTitleService.fetchBookTitleReportData();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/report/excel")
    public void downloadBookTitleReport(HttpServletResponse response, @AuthenticationPrincipal CurrentUserDto currentUserDto) {
        log.info("User '{}' requested to download book title report.", currentUserDto.getUsername());
        try {
            // Lấy username an toàn
            String username = (currentUserDto != null) ? currentUserDto.getHoTenDayDu() : "Unknown User";

            List<BookTitleDisplayDtoExcel> data = bookTitleService.fetchBookTitleReportData();
            if (data.isEmpty()) {
                response.setStatus(HttpStatus.NO_CONTENT.value());
                return;
            }

            bookTitleService.generateBookTitlesExcelReport(data, username, response);
        } catch (Exception e) {
            log.error("Error generating book title report for download: {}", e.getMessage(), e);

            try {
                response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi khi tạo file Excel.");
            } catch (IOException ioException) {
                log.error("Could not send error response to client", ioException);
            }
        }
    }


    @PostMapping("/undo")
    public ResponseEntity<?> undoLastAction(@RequestBody UndoActionDto undoAction) {
        try {
            bookTitleService.processUndo(undoAction);
            return ResponseEntity.ok(Map.of("message", "Hoàn tác thành công."));
        } catch (Exception e) {
            log.error("Undo failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lỗi khi hoàn tác: " + e.getMessage()));
        }
    }
}