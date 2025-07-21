package com.ThuVien.ThuVienAplication.service;

import com.ThuVien.ThuVienAplication.exception.SqlCustomException;
import com.ThuVien.ThuVienAplication.model.dto.request.bookTitleRq.BookTitleRqDto;
import com.ThuVien.ThuVienAplication.model.dto.request.bookTitleRq.UndoActionDto;
import com.ThuVien.ThuVienAplication.model.dto.response.bookTitleRp.*;
import com.ThuVien.ThuVienAplication.storeprocedure.bookTitleSp.QueryGetData;
import com.ThuVien.ThuVienAplication.storeprocedure.bookTitleSp.QueryStoreProcedure;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookTitleService {

    private final DataSource dataSource;
    private final ObjectMapper objectMapper;
    private final ImagesService imagesService;



    private BookTitleDisplayDto mapResultSetToDisplayDto(ResultSet rs) throws SQLException {
        BookTitleDisplayDto item = new BookTitleDisplayDto();
        item.setCodeBookTitle(rs.getString("ISBN") != null ? rs.getString("ISBN").trim() : null);
        item.setNameBook(rs.getString("TENSACH"));
        item.setNameAuthor(rs.getString("TENTACGIA"));
        item.setFormatBook(rs.getString("KHOSACH"));
        item.setContentBook(rs.getString("NOIDUNG"));
        item.setPicturePath(rs.getString("HINHANHPATH") != null ? rs.getString("HINHANHPATH").trim() : null);
        item.setDateRelease(rs.getDate("NGAYXUATBAN"));
        item.setEditions(rs.getObject("LANXUATBAN") != null ? rs.getInt("LANXUATBAN") : null);
        item.setPages(rs.getObject("SOTRANG") != null ? rs.getInt("SOTRANG") : null);
        item.setPrice(rs.getObject("GIA") != null ? rs.getLong("GIA") : null);
        item.setNamePublisher(rs.getString("NHAXB"));
        item.setNameCodeLanguage(rs.getString("NGONNGU"));
        item.setNameCodeType(rs.getString("THELOAI"));
        item.setCodeLanguage(rs.getObject("MANGONNGU") != null ? rs.getInt("MANGONNGU") : null); // <<-- DÒNG MỚI
        item.setCodeType(rs.getString("MATL") != null ? rs.getString("MATL").trim() : null);         // <<-- DÒNG MỚI

        String authorIdsString = rs.getString("MA_TAC_GIA_GOP");
        if (authorIdsString != null && !authorIdsString.isEmpty()) {
            item.setCodeAuthor(Arrays.asList(authorIdsString.split(",")));
        } else {
            item.setCodeAuthor(new ArrayList<>()); // Gán một list rỗng nếu không có tác giả
        }
        return item;
    }

    public List<BookTitleDisplayDto> getAllBookTitles() {
        List<BookTitleDisplayDto> displayList = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(QueryGetData.SP_Thong_tin_Dau_Sach_Test);
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                displayList.add(mapResultSetToDisplayDto(rs));
            }
        } catch (SQLException e) {
            log.error("--ER getAllBookTitles: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi lấy danh sách đầu sách", e);
        }
        return displayList;
    }

    public BookTitleRqDto getBookTitleByIsbn(String isbn) {
        BookTitleRqDto dto = new BookTitleRqDto();
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(QueryGetData.SP_Thong_tin_Dau_Sach)) {
            cs.setString(1, isbn.trim());
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    dto.setCodeBookTitle(rs.getString(1) != null ? rs.getString(1).trim() : null);
                    dto.setNameBook(rs.getString(2));
                    String authorIdsString = rs.getString(3);
                    if (authorIdsString != null && !authorIdsString.isEmpty()) {
                        dto.setCodeAuthor(Arrays.asList(authorIdsString.split(",")));
                    }
                    dto.setFormatBook(rs.getString(5));
                    dto.setContentBook(rs.getString(6));
                    dto.setPicturePath(rs.getString(7));
                    dto.setDateRelease(rs.getDate(8));
                    dto.setEditions(rs.getInt(9));
                    dto.setPages(rs.getInt(10));
                    dto.setPrice(rs.getLong(11));
                    dto.setNamePublisher(rs.getString(12));
                    dto.setCodeLanguage(rs.getInt(13));
                    dto.setCodeType(rs.getString(14) != null ? rs.getString(14).trim() : null);
                    return dto;
                }
            }
        } catch (SQLException e) {
            log.error("--ER getBookTitleByIsbn for {}: {}", isbn, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tìm đầu sách với ISBN: " + isbn, e);
        }
        return null;
    }

    public List<AuthorDto> getAuthors() {
        List<AuthorDto> authors = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(QueryGetData.SP_Danh_Tac_Gia_Test);
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                authors.add(new AuthorDto(rs.getInt("MATACGIA"), rs.getString("HOTENTG")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách tác giả", e);
        }
        return authors;
    }

    public List<LanguageDto> getLanguages() {
        List<LanguageDto> languages = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(QueryGetData.SP_Data_Ngon_Ngu);
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                languages.add(new LanguageDto(rs.getInt("MANGONNGU"), rs.getString("NGONNGU")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách ngôn ngữ", e);
        }
        return languages;
    }

    public List<BookTypeDto> getBookTypes() {
        List<BookTypeDto> bookTypes = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(QueryGetData.SP_Data_The_loai);
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                bookTypes.add(new BookTypeDto(rs.getString("MATL").trim(), rs.getString("THELOAI")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách thể loại", e);
        }
        return bookTypes;
    }

    public BookTitleDisplayDto createBookTitle(BookTitleRqDto request) {
        // === KIỂM TRA TRÙNG TRƯỚC KHI GỌI SP ===
        if (isIsbnExists(request.getCodeBookTitle())) {
            throw new SqlCustomException("ISBN '" + request.getCodeBookTitle() + "' đã tồn tại. Vui lòng chọn ISBN khác.");
        }
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(QueryStoreProcedure.SP_ThemDauSach)) {



            cs.setString(1, request.getCodeBookTitle().trim());
            cs.setString(2, request.getNameBook().trim());
            cs.setString(3, String.join(",", request.getCodeAuthor()));
            cs.setString(4, request.getFormatBook().trim());
            cs.setString(5, request.getContentBook().trim());
            cs.setString(6, request.getPicturePath().trim());
            cs.setDate(7, new java.sql.Date(request.getDateRelease().getTime()));
            cs.setInt(8, request.getEditions());
            cs.setInt(9, request.getPages());
            cs.setLong(10, request.getPrice());
            cs.setString(11, request.getNamePublisher().trim());
            cs.setInt(12, request.getCodeLanguage());
            cs.setString(13, request.getCodeType().trim());

            cs.execute();
            return getDisplayDtoByIsbn(request.getCodeBookTitle());
        } catch (SQLException e) {
            log.error("--ER createBookTitle: {}", e.getMessage(), e);
            throw new SqlCustomException("Lỗi khi thêm mới đầu sách: " + e.getMessage());
        }
    }

    public BookTitleDisplayDto updateBookTitle(String originalIsbn, BookTitleRqDto request, MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            String newImageFileName = imagesService.handleUploadFile(imageFile, "dausach");
            request.setPicturePath(newImageFileName);
        }

        return updateBookTitle(originalIsbn, request);
    }

    public BookTitleDisplayDto updateBookTitle(String originalIsbn, BookTitleRqDto request) {
        // Kiểm tra trùng lặp khi ISBN thay đổi
        String newIsbn = request.getCodeBookTitle().trim();
        if (!originalIsbn.trim().equalsIgnoreCase(newIsbn) && isIsbnExists(newIsbn)) {
            throw new IllegalArgumentException("Không thể cập nhật. ISBN mới '" + newIsbn + "' đã được sử dụng.");
        }

        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(QueryStoreProcedure.SP_CapNhatDauSach)) {

            cs.setString(1, originalIsbn);
            cs.setString(2, request.getCodeBookTitle().trim());
            cs.setString(3, request.getNameBook().trim());
            cs.setString(4, request.getFormatBook().trim());
            cs.setString(5, request.getContentBook().trim());
            // Lấy picturePath trực tiếp từ DTO, có thể là ảnh mới hoặc ảnh cũ
            cs.setString(6, request.getPicturePath() != null ? request.getPicturePath().trim() : "example.jpg");
            cs.setDate(7, new java.sql.Date(request.getDateRelease().getTime()));
            cs.setInt(8, request.getEditions());
            cs.setInt(9, request.getPages());
            cs.setLong(10, request.getPrice());
            cs.setString(11, request.getNamePublisher().trim());
            cs.setInt(12, request.getCodeLanguage());
            cs.setString(13, request.getCodeType().trim());
            cs.setString(14, String.join(",", request.getCodeAuthor()));

            cs.execute();
            return getDisplayDtoByIsbn(request.getCodeBookTitle());
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật đầu sách: " + e.getMessage(), e);
        }
    }

    public BookTitleDisplayDto updateBookTitleUndo(String originalIsbn, BookTitleRqDto request, String imageFile) {
        // Kiểm tra trùng lặp khi ISBN thay đổi
        String newIsbn = request.getCodeBookTitle().trim();
        if (!originalIsbn.trim().equalsIgnoreCase(newIsbn) && isIsbnExists(newIsbn)) {
            throw new IllegalArgumentException("Không thể cập nhật. ISBN mới '" + newIsbn + "' đã được sử dụng.");
        }



        // Gọi SP cập nhật
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(QueryStoreProcedure.SP_CapNhatDauSach)) {
            cs.setString(1, originalIsbn);
            cs.setString(2, request.getCodeBookTitle().trim());
            cs.setString(3, request.getNameBook().trim());
            cs.setString(4, request.getFormatBook().trim());
            cs.setString(5, request.getContentBook().trim());
            cs.setString(6, imageFile != null ? imageFile.trim() : "example.jpg");
            cs.setDate(7, new java.sql.Date(request.getDateRelease().getTime()));
            cs.setInt(8, request.getEditions());
            cs.setInt(9, request.getPages());
            cs.setLong(10, request.getPrice());
            cs.setString(11, request.getNamePublisher().trim());
            cs.setInt(12, request.getCodeLanguage());
            cs.setString(13, request.getCodeType().trim());
            cs.setString(14, String.join(",", request.getCodeAuthor()));

            cs.execute();
            return getDisplayDtoByIsbn(request.getCodeBookTitle());
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật đầu sách: " + e.getMessage(), e);
        }
    }

    public void deleteBookTitle(String isbn) {
        String sql = "{call SP_XoaDauSachVaLienKet(?)}";
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, isbn);
            boolean hasResultSet = cs.execute();
            if (hasResultSet) {
                try (ResultSet rs = cs.getResultSet()) {
                    if (rs.next() && !rs.getBoolean("Success")) {
                        throw new IllegalStateException(rs.getString("Message"));
                    }
                }
            }
        } catch (SQLException e) {
            log.error("--ER deleteBookTitle for {}: {}", isbn, e.getMessage(), e);
            throw new RuntimeException("Lỗi SQL khi xóa đầu sách: " + e.getMessage(), e);
        }
    }

    public String updateBookImage(String isbn, MultipartFile imageFile) {
        BookTitleRqDto bookTitle = getBookTitleByIsbn(isbn); // Tái sử dụng hàm đã có
        if (bookTitle == null) {
            throw new RuntimeException("Không tìm thấy đầu sách với ISBN: " + isbn);
        }

        String newImageFileName = imagesService.handleUploadFile(imageFile, "dausach");
        if (newImageFileName.isEmpty()) {
            throw new RuntimeException("Tải ảnh lên thất bại.");
        }


        String sql = "{call SP_UpdateBookImagePath(?, ?)}";
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, isbn);
            cs.setString(2, newImageFileName);
            cs.execute();
        } catch (SQLException e) {
            log.error("Failed to update image path in DB for ISBN {}: {}", isbn, e.getMessage());
            throw new RuntimeException("Lỗi khi cập nhật đường dẫn ảnh trong CSDL.", e);
        }

        return newImageFileName;
    }

    private BookTitleDisplayDto getDisplayDtoByIsbn(String isbn) {
        for (BookTitleDisplayDto book : getAllBookTitles()) {
            if (book.getCodeBookTitle().equals(isbn)) {
                return book;
            }
        }
        return null;
    }
    public List<BookTitleDisplayDtoExcel> fetchBookTitleReportData() {
        List<BookTitleDisplayDtoExcel> allBookTitles = new ArrayList<>();
        String sql = QueryGetData.SP_Thong_tin_Dau_Sach_Test_cho_excel;
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                BookTitleDisplayDtoExcel item = new BookTitleDisplayDtoExcel();
                item.setCodeBookTitle(rs.getString("ISBN"));
                item.setNameBook(rs.getString("TENSACH"));
                item.setNameAuthor(rs.getString("TENTACGIA"));
                item.setDateRelease(rs.getDate("NGAYXUATBAN"));
                item.setPages(rs.getObject("SOTRANG") != null ? rs.getInt("SOTRANG") : null);
                item.setNameCodeLanguage(rs.getString("NGONNGU"));
                item.setNameCodeType(rs.getString("THELOAI"));
                item.setMaTl(rs.getString("MATL"));
                item.setSoCuonThucTe(rs.getObject("SoCuonThucTe") != null ? rs.getInt("SoCuonThucTe") : 0);
                allBookTitles.add(item);
            }
        } catch (SQLException e) {
            log.error("Error fetching book title report data: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi lấy dữ liệu báo cáo đầu sách", e);
        }
        return allBookTitles;
    }

    public List<GenreReportGroupDto> groupBookTitlesByGenre(List<BookTitleDisplayDtoExcel> allBookTitles) {
        if (allBookTitles == null || allBookTitles.isEmpty()) {
            return new ArrayList<>();
        }
        allBookTitles.sort(Comparator.comparing(BookTitleDisplayDtoExcel::getNameCodeType, Comparator.nullsLast(String::compareToIgnoreCase))
                .thenComparing(BookTitleDisplayDtoExcel::getNameBook, Comparator.nullsLast(String::compareToIgnoreCase)));

        Map<String, GenreReportGroupDto> groupedMap = new LinkedHashMap<>();
        for (BookTitleDisplayDtoExcel book : allBookTitles) {
            String genreName = book.getNameCodeType() != null ? book.getNameCodeType() : "Chưa phân loại";
            groupedMap.putIfAbsent(genreName, new GenreReportGroupDto(genreName));
            groupedMap.get(genreName).addBook(book);
        }
        return new ArrayList<>(groupedMap.values());
    }


    public void generateBookTitlesExcelReport(List<BookTitleDisplayDtoExcel> allBookTitles, String username, HttpServletResponse response) throws IOException {

        List<GenreReportGroupDto> groupedData = groupBookTitlesByGenre(allBookTitles);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Danh Mục Đầu Sách");

        Font titleFont = workbook.createFont(); titleFont.setBold(true); titleFont.setFontHeightInPoints((short) 16);
        Font headerInfoFont = workbook.createFont(); headerInfoFont.setBold(true); headerInfoFont.setFontHeightInPoints((short) 12);
        Font genreTitleFont = workbook.createFont(); genreTitleFont.setBold(true); genreTitleFont.setFontHeightInPoints((short) 11);
        Font tableHeaderFont = workbook.createFont(); tableHeaderFont.setBold(true);

        CellStyle titleStyle = workbook.createCellStyle(); titleStyle.setFont(titleFont); titleStyle.setAlignment(HorizontalAlignment.CENTER); titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        CellStyle headerInfoStyle = workbook.createCellStyle(); headerInfoStyle.setFont(headerInfoFont); headerInfoStyle.setAlignment(HorizontalAlignment.LEFT);
        CellStyle genreTitleStyle = workbook.createCellStyle(); genreTitleStyle.setFont(genreTitleFont); genreTitleStyle.setAlignment(HorizontalAlignment.LEFT); genreTitleStyle.setBorderBottom(BorderStyle.THIN); genreTitleStyle.setBorderTop(BorderStyle.THIN); genreTitleStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex()); genreTitleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        CellStyle tableHeaderStyle = workbook.createCellStyle(); tableHeaderStyle.setFont(tableHeaderFont); tableHeaderStyle.setBorderTop(BorderStyle.THIN); tableHeaderStyle.setBorderBottom(BorderStyle.THIN); tableHeaderStyle.setBorderLeft(BorderStyle.THIN); tableHeaderStyle.setBorderRight(BorderStyle.THIN); tableHeaderStyle.setAlignment(HorizontalAlignment.CENTER); tableHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER); tableHeaderStyle.setWrapText(true);

        CellStyle dataCellStyle = workbook.createCellStyle(); dataCellStyle.setBorderTop(BorderStyle.THIN); dataCellStyle.setBorderBottom(BorderStyle.THIN); dataCellStyle.setBorderLeft(BorderStyle.THIN); dataCellStyle.setBorderRight(BorderStyle.THIN); dataCellStyle.setAlignment(HorizontalAlignment.LEFT); dataCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); dataCellStyle.setWrapText(true); // Đảm bảo wrap text cho Tác giả

        CellStyle dateCellStyle = workbook.createCellStyle(); dateCellStyle.cloneStyleFrom(dataCellStyle); CreationHelper createHelper = workbook.getCreationHelper(); dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));

        CellStyle numberDataCellStyle = workbook.createCellStyle();
        numberDataCellStyle.cloneStyleFrom(dataCellStyle); // Kế thừa border, wrap text
        numberDataCellStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
        numberDataCellStyle.setAlignment(HorizontalAlignment.RIGHT);

        CellStyle totalRowStyle = workbook.createCellStyle(); totalRowStyle.setFont(tableHeaderFont); totalRowStyle.setAlignment(HorizontalAlignment.RIGHT); totalRowStyle.setBorderTop(BorderStyle.THIN); totalRowStyle.setBorderBottom(BorderStyle.THIN); totalRowStyle.setBorderLeft(BorderStyle.THIN); totalRowStyle.setBorderRight(BorderStyle.THIN);

        CellStyle totalNumberStyle = workbook.createCellStyle();
        totalNumberStyle.cloneStyleFrom(totalRowStyle);
        totalNumberStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
        totalNumberStyle.setAlignment(HorizontalAlignment.RIGHT);

        int currentRowNum = 0; // Khai báo cục bộ
        Row reportTitleRow = sheet.createRow(currentRowNum++); // Dòng 0
        reportTitleRow.setHeightInPoints(25);
        Cell reportTitleCell = reportTitleRow.createCell(0); reportTitleCell.setCellValue("DANH MỤC ĐẦU SÁCH"); reportTitleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(reportTitleRow.getRowNum(), reportTitleRow.getRowNum(), 0, 7));

        currentRowNum++; // Dòng trống (dòng 1)

        Row staffRow = sheet.createRow(currentRowNum++); // Dòng 2
        staffRow.createCell(0).setCellValue("Nhân viên lập báo cáo:"); staffRow.getCell(0).setCellStyle(headerInfoStyle);
        Cell staffValueCell = staffRow.createCell(1); staffValueCell.setCellValue(username != null ? username : "N/A"); staffValueCell.setCellStyle(headerInfoStyle);
        sheet.addMergedRegion(new CellRangeAddress(staffRow.getRowNum(), staffRow.getRowNum(), 1, 3));

        Row printDateRow = sheet.createRow(currentRowNum++); // Dòng 3
        printDateRow.createCell(0).setCellValue("Ngày in:"); printDateRow.getCell(0).setCellStyle(headerInfoStyle);
        Cell printDateValueCell = printDateRow.createCell(1); printDateValueCell.setCellValue(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date())); printDateValueCell.setCellStyle(headerInfoStyle);
        sheet.addMergedRegion(new CellRangeAddress(printDateRow.getRowNum(), printDateRow.getRowNum(), 1, 3));

        currentRowNum++; // Dòng trống (dòng 4)


        String[] columns = {"STT", "ISBN", "Tên sách", "Ngày XB", "Số trang", "Tác giả", "Ngôn ngữ", "Số cuốn"};
        int totalBooksOverall = 0; // Khai báo cục bộ
        int totalActualCopiesOverall = 0;
        DecimalFormat numberFormat = new DecimalFormat("#,##0");

        for (GenreReportGroupDto genreGroup : groupedData) {
            Row genreHeaderRow = sheet.createRow(currentRowNum++);
            Cell genreNameCell = genreHeaderRow.createCell(0);
            genreNameCell.setCellValue("Thể loại: " + genreGroup.getGenreName());
            genreNameCell.setCellStyle(genreTitleStyle);
            sheet.addMergedRegion(new CellRangeAddress(currentRowNum - 1, currentRowNum - 1, 0, columns.length - 1));

            Row headerDataRow = sheet.createRow(currentRowNum++);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerDataRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(tableHeaderStyle);
            }

            int sttInGenre = 1;
            // *** Lặp qua List<BookTitleDisplayDtoExcel> ***
            for (BookTitleDisplayDtoExcel book : genreGroup.getBooksInGenre()) {
                Row dataRow = sheet.createRow(currentRowNum++);
                int cellIdx = 0;

                dataRow.createCell(cellIdx++).setCellValue(sttInGenre++); // STT
                dataRow.getCell(cellIdx-1).setCellStyle(numberDataCellStyle); // STT cũng là số

                dataRow.createCell(cellIdx++).setCellValue(book.getCodeBookTitle()); // ISBN
                dataRow.getCell(cellIdx-1).setCellStyle(dataCellStyle);

                dataRow.createCell(cellIdx++).setCellValue(book.getNameBook()); // Tên sách
                dataRow.getCell(cellIdx-1).setCellStyle(dataCellStyle);

                Cell ngayXBCell = dataRow.createCell(cellIdx++);
                if (book.getDateRelease() != null) ngayXBCell.setCellValue(book.getDateRelease());
                ngayXBCell.setCellStyle(dateCellStyle);

                Cell soTrangCell = dataRow.createCell(cellIdx++);
                if (book.getPages() != null) soTrangCell.setCellValue(book.getPages());
                soTrangCell.setCellStyle(numberDataCellStyle); // *** DÙNG numberDataCellStyle ***

                dataRow.createCell(cellIdx++).setCellValue(book.getNameAuthor()); // Tác giả
                dataRow.getCell(cellIdx-1).setCellStyle(dataCellStyle); // dataCellStyle đã có wrap text

                dataRow.createCell(cellIdx++).setCellValue(book.getNameCodeLanguage()); // Ngôn ngữ
                dataRow.getCell(cellIdx-1).setCellStyle(dataCellStyle);

                Cell soCuonCell = dataRow.createCell(cellIdx++);
                // *** SỬ DỤNG getSoCuonThucTe() ***
                if (book.getSoCuonThucTe() != null) soCuonCell.setCellValue(book.getSoCuonThucTe());
                else soCuonCell.setCellValue(0);
                soCuonCell.setCellStyle(numberDataCellStyle); // *** DÙNG numberDataCellStyle ***
            }

            Row subTotalRow = sheet.createRow(currentRowNum++);
            Cell subTotalLabelCell = subTotalRow.createCell(0);
            subTotalLabelCell.setCellValue("Số đầu sách");
            CellStyle totalLabelStyle = workbook.createCellStyle();
            totalLabelStyle.cloneStyleFrom(totalRowStyle);
            totalLabelStyle.setAlignment(HorizontalAlignment.LEFT);
            subTotalLabelCell.setCellStyle(totalLabelStyle);

            sheet.addMergedRegion(new CellRangeAddress(currentRowNum - 1, currentRowNum - 1, 0, 2));
            // Fill border cho các cell đã merge
            for (int i = 1; i <= 2; i++) subTotalRow.createCell(i).setCellStyle(totalLabelStyle);


            Cell bookCountCell = subTotalRow.createCell(3);
            bookCountCell.setCellValue(genreGroup.getBookCountInGenre());
            bookCountCell.setCellStyle(totalNumberStyle);

            sheet.addMergedRegion(new CellRangeAddress(currentRowNum - 1, currentRowNum - 1, 3, columns.length - 2));
            // Fill border cho các cell đã merge
            for (int i = 4; i < columns.length - 1; i++) subTotalRow.createCell(i).setCellStyle(totalNumberStyle);


            Cell actualCopiesCountCell = subTotalRow.createCell(columns.length - 1);
            actualCopiesCountCell.setCellValue(genreGroup.getTotalActualCopiesInGenre());
            actualCopiesCountCell.setCellStyle(totalNumberStyle);

            totalBooksOverall += genreGroup.getBookCountInGenre();
            totalActualCopiesOverall += genreGroup.getTotalActualCopiesInGenre();

            currentRowNum++; // Add an empty row for spacing
        }

        Row grandTotalRow = sheet.createRow(currentRowNum++);
        Cell grandTotalLabelCell = grandTotalRow.createCell(0);
        grandTotalLabelCell.setCellValue("Số đầu sách thư viện");
        // Cần style cho label của dòng total (căn trái, bold, border)
        CellStyle grandTotalLabelStyle = workbook.createCellStyle();
        grandTotalLabelStyle.cloneStyleFrom(totalRowStyle);
        grandTotalLabelStyle.setAlignment(HorizontalAlignment.LEFT);
        grandTotalLabelCell.setCellStyle(grandTotalLabelStyle);

        sheet.addMergedRegion(new CellRangeAddress(currentRowNum - 1, currentRowNum - 1, 0, 2));
        for (int i = 1; i <= 2; i++) grandTotalRow.createCell(i).setCellStyle(grandTotalLabelStyle);


        Cell grandTotalBooksCell = grandTotalRow.createCell(3);
        // grandTotalBooksCell.setCellValue(numberFormat.format(totalBooksOverall));
        grandTotalBooksCell.setCellValue(totalBooksOverall);
        grandTotalBooksCell.setCellStyle(totalNumberStyle);
        sheet.addMergedRegion(new CellRangeAddress(currentRowNum - 1, currentRowNum - 1, 3, columns.length - 2));
        for (int i = 4; i < columns.length - 1; i++) grandTotalRow.createCell(i).setCellStyle(totalNumberStyle);


        Cell grandTotalActualCopiesCell = grandTotalRow.createCell(columns.length - 1); // Đổi tên biến
        // grandTotalActualCopiesCell.setCellValue(numberFormat.format(totalActualCopiesOverall));
        grandTotalActualCopiesCell.setCellValue(totalActualCopiesOverall); // *** SỬ DỤNG totalActualCopiesOverall ***
        grandTotalActualCopiesCell.setCellStyle(totalNumberStyle);

        // Auto-size columns (giữ nguyên)
        for (int i = 0; i < columns.length; i++) {
            if (i == 5) sheet.setColumnWidth(i, 30 * 256); // Tác giả
            else if (i == 2) sheet.setColumnWidth(i, 40 * 256); // Tên sách
            else sheet.autoSizeColumn(i);
        }
        // Điều chỉnh lại nếu cần
        sheet.setColumnWidth(0, 6 * 256); // STT
        sheet.setColumnWidth(1, 15 * 256); // ISBN
        sheet.setColumnWidth(3, 12 * 256); // Ngay XB
        sheet.setColumnWidth(4, 10 * 256); // So trang
        sheet.setColumnWidth(6, 15 * 256); // Ngon ngu
        sheet.setColumnWidth(7, 10 * 256); // So cuon


        // Write output (giữ nguyên)
        String filename = "DanhMucDauSach_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        workbook.write(response.getOutputStream());
        workbook.close();
    }


    public void processUndo(UndoActionDto undoAction) throws Exception {
        String actionType = undoAction.getActionType();
        JsonNode data = undoAction.getData();

        if ("ADD".equals(actionType)) {

            String isbnToDelete = data.get("codeBookTitle").asText();
            deleteBookTitle(isbnToDelete); // Gọi lại hàm delete đã có
        }
        else if ("UPDATE".equals(actionType)) {

            BookTitleRqDto oldData = objectMapper.treeToValue(data.get("oldData"), BookTitleRqDto.class);
            BookTitleRqDto newData = objectMapper.treeToValue(data.get("newData"), BookTitleRqDto.class);


            updateBookTitle(newData.getCodeBookTitle(), oldData);
        }
        else if ("DELETE".equals(actionType)) {

            BookTitleRqDto dataToRecreate = objectMapper.treeToValue(data.get("originalData"), BookTitleRqDto.class);
            createBookTitle(dataToRecreate); // Gọi lại hàm create
        } else {
            throw new IllegalArgumentException("Loại hành động hoàn tác không hợp lệ: " + actionType);
        }
    }


    public boolean isIsbnExists(String isbn) {
        String sql = "{ ? = call (SELECT 1 FROM DAUSACH WHERE ISBN = ?) }";
        String checkSql = "{call SP_CheckIsbnExists(?, ?)}";

        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(checkSql)) {
            cs.setString(1, isbn.trim());
            cs.registerOutParameter(2, Types.BIT);
            cs.execute();
            return cs.getBoolean(2);
        } catch (SQLException e) {
            log.error("Error checking ISBN existence for {}: {}", isbn, e.getMessage());

            throw new SqlCustomException("Lỗi khi kiểm tra ISBN");
        }
    }
}