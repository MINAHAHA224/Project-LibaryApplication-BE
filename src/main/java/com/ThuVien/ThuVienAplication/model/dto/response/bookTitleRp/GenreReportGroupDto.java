package com.ThuVien.ThuVienAplication.model.dto.response.bookTitleRp;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class GenreReportGroupDto {
    private String genreName;
    private List<BookTitleDisplayDtoExcel> booksInGenre = new ArrayList<>();
    private int bookCountInGenre = 0;
    private int totalActualCopiesInGenre = 0;

    public GenreReportGroupDto(String genreName) {
        this.genreName = genreName;
    }

    public void addBook(BookTitleDisplayDtoExcel book) {
        this.booksInGenre.add(book);
        this.bookCountInGenre++;
        if (book.getSoCuonThucTe() != null) {
            this.totalActualCopiesInGenre += book.getSoCuonThucTe();
        }
    }
}