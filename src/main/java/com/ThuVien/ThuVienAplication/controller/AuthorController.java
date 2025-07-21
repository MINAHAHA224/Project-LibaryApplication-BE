package com.ThuVien.ThuVienAplication.controller;

import com.ThuVien.ThuVienAplication.model.dto.request.author.UndoActionDto;
import com.ThuVien.ThuVienAplication.model.dto.response.author.AuthorDto;
import com.ThuVien.ThuVienAplication.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    public ResponseEntity<List<AuthorDto>> getAll() {
        return ResponseEntity.ok(authorService.getAllAuthors());
    }

    @PostMapping
    public ResponseEntity<AuthorDto> create(@RequestBody AuthorDto dto) {
        AuthorDto createdAuthor = authorService.createAuthor(dto);
        return new ResponseEntity<>(createdAuthor, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorDto> update(@PathVariable int id, @RequestBody AuthorDto dto) {
        AuthorDto updatedAuthor = authorService.updateAuthor(id, dto);
        return ResponseEntity.ok(updatedAuthor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.ok(Map.of("message", "Xóa tác giả thành công."));
    }

    @PostMapping("/undo")
    public ResponseEntity<?> undo(@RequestBody UndoActionDto undoAction) {
        try {
            authorService.processUndo(undoAction);
            return ResponseEntity.ok(Map.of("message", "Hoàn tác thành công."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}