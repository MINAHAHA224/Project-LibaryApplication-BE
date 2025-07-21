package com.ThuVien.ThuVienAplication.service;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExcelBackup {
    @Value("${database.url}")
    private String dbUrl;
    @Value("${database.drive}")
    private String driver;

    String user = "sa";
    String pass = "123456";
    public void exportDauSachToExcel(String filePath, String nhanVienLap , HttpServletResponse response) {
        // --- Kết nối CSDL ---



        String sql = """
            WITH SachCounts AS (
                SELECT ISBN, COUNT(*) AS SoCuonSach FROM SACH GROUP BY ISBN
            ),
            AuthorList AS (
                SELECT ts.ISBN, STRING_AGG(tg.HOTENTG, ', ') WITHIN GROUP (ORDER BY tg.HOTENTG) AS DanhSachTacGia
                FROM TACGIA_SACH ts JOIN TACGIA tg ON ts.MATACGIA = tg.MATACGIA GROUP BY ts.ISBN
            )
            SELECT
                tl.MATL, tl.THELOAI, ds.ISBN, ds.TENSACH, ds.NGAYXUATBAN, ds.SOTRANG,
                ISNULL(al.DanhSachTacGia, 'N/A') AS TacGia, ISNULL(ng.NGONNGU, 'N/A') AS NgonNgu,
                ISNULL(sc.SoCuonSach, 0) AS SoCuon
            FROM DAUSACH ds
            JOIN THELOAI tl ON ds.MATL = tl.MATL
            LEFT JOIN SachCounts sc ON ds.ISBN = sc.ISBN
            LEFT JOIN AuthorList al ON ds.ISBN = al.ISBN
            LEFT JOIN NGONNGU ng ON ds.MANGONNGU = ng.MANGONNGU
            ORDER BY tl.THELOAI, ds.TENSACH
            """;

        try (Connection conn = DriverManager.getConnection(dbUrl, user, pass);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql);
             Workbook workbook = new XSSFWorkbook();
             FileOutputStream fileOut = new FileOutputStream(filePath)) {

            Sheet sheet = workbook.createSheet("Danh mục Đầu sách");


            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle categoryHeaderStyle = createCategoryHeaderStyle(workbook);
            CellStyle boldStyle = createBoldStyle(workbook);
            CellStyle normalStyle = createNormalStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook, normalStyle);
            CellStyle numberStyle = createNumberStyle(workbook, normalStyle); // For counts
            CellStyle totalStyle = createTotalStyle(workbook); // Style for summary rows
            CellStyle totalNumberStyle = createNumberStyle(workbook, totalStyle);

            int rowNum = 1;

            // --- Header báo cáo ---
            // Dòng tiêu đề chính
            Row titleRow = sheet.createRow(rowNum++);
            titleRow.setHeightInPoints(25); // Cao hơn chút
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("DANH MỤC ĐẦU SÁCH");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 7)); // Merge 8 cột (STT -> Số cuốn)

            // Dòng nhân viên
            Row nvRow = sheet.createRow(rowNum++);
            Cell nvCell = nvRow.createCell(0); // Bắt đầu từ cột 1
            nvCell.setCellValue("Nhân viên lập báo cáo: " + (nhanVienLap != null ? nhanVienLap : ""));
            nvCell.setCellStyle(boldStyle); // In đậm
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 7));

            // Dòng ngày in
            Row dateRow = sheet.createRow(rowNum++);
            Cell dateCell = dateRow.createCell(0);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            dateCell.setCellValue("Ngày in: " + sdf.format(new Date()));
            dateCell.setCellStyle(boldStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 7));

            rowNum++; // Thêm một dòng trống

            // --- Header của bảng dữ liệu ---
            Row tableHeaderRow = sheet.createRow(rowNum++);
            String[] headers = {"STT", "ISBN", "Tên sách", "Ngày XB", "Số trang", "Tác giả", "Ngôn ngữ", "Số cuốn"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = tableHeaderRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // --- Xử lý dữ liệu từ ResultSet ---
            String currentTheLoai = null;
            int sttTrongTheLoai = 0;
            int soDauSachTrongTheLoai = 0;
            long soCuonTrongTheLoai = 0;
            int tongSoDauSachThuVien = 0;
            long tongSoCuonThuVien = 0;

            while (rs.next()) {
                String theLoai = rs.getString("THELOAI");
                String isbn = rs.getString("ISBN");
                String tenSach = rs.getString("TENSACH");
                java.sql.Date ngayXB = rs.getDate("NGAYXUATBAN");
                int soTrang = rs.getInt("SOTRANG");
                String tacGia = rs.getString("TacGia");
                String ngonNgu = rs.getString("NgonNgu");
                long soCuon = rs.getLong("SoCuon");

                // --- Kiểm tra nếu chuyển sang thể loại mới ---
                if (currentTheLoai == null || !currentTheLoai.equals(theLoai)) {
                    // Nếu không phải là thể loại đầu tiên, in dòng tổng kết cho thể loại cũ
                    if (currentTheLoai != null) {
                        writeCategoryFooter(sheet, rowNum++, soDauSachTrongTheLoai, soCuonTrongTheLoai, totalStyle, totalNumberStyle);
                        rowNum++; // Dòng trống phân cách
                    }

                    // In header cho thể loại mới
                    Row categoryRow = sheet.createRow(rowNum++);
                    Cell categoryCell = categoryRow.createCell(0); // Bắt đầu từ cột 1
                    categoryCell.setCellValue("Thể loại : " + theLoai);
                    categoryCell.setCellStyle(categoryHeaderStyle);
                    sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 7)); // Merge

                    // Reset bộ đếm cho thể loại mới
                    currentTheLoai = theLoai;
                    sttTrongTheLoai = 1; // Bắt đầu lại STT
                    soDauSachTrongTheLoai = 0;
                    soCuonTrongTheLoai = 0;
                }

                // --- In dòng dữ liệu chi tiết ---
                Row dataRow = sheet.createRow(rowNum++);
                createCell(dataRow, 0, sttTrongTheLoai++, numberStyle);      // STT
                createCell(dataRow, 1, isbn, normalStyle);            // ISBN
                createCell(dataRow, 2, tenSach, normalStyle);         // Tên sách
                createCell(dataRow, 3, ngayXB, dateStyle);            // Ngày XB
                createCell(dataRow, 4, soTrang, numberStyle);         // Số trang
                createCell(dataRow, 5, tacGia, normalStyle);          // Tác giả
                createCell(dataRow, 6, ngonNgu, normalStyle);         // Ngôn ngữ
                createCell(dataRow, 7, soCuon, numberStyle);          // Số cuốn

                // --- Cập nhật bộ đếm ---
                soDauSachTrongTheLoai++;
                soCuonTrongTheLoai += soCuon;
                tongSoDauSachThuVien++;
                tongSoCuonThuVien += soCuon;
            }

            // --- In dòng tổng kết cho thể loại CUỐI CÙNG ---
            if (currentTheLoai != null) {
                writeCategoryFooter(sheet, rowNum++, soDauSachTrongTheLoai, soCuonTrongTheLoai, totalStyle, totalNumberStyle);
            }

            rowNum++; // Dòng trống

            // --- In dòng tổng kết toàn thư viện ---
            Row finalTotalRow = sheet.createRow(rowNum++);
            Cell totalLabelCell = finalTotalRow.createCell(0);
            totalLabelCell.setCellValue("Số đầu sách thư viện");
            totalLabelCell.setCellStyle(totalStyle); // Style đậm
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 1)); // Merge 2 cột đầu

            // Giá trị tổng số đầu sách
            createCell(finalTotalRow, 2, tongSoDauSachThuVien, totalNumberStyle); // Định dạng số #,###

            // Giá trị tổng số cuốn
            createCell(finalTotalRow, 7, tongSoCuonThuVien, totalNumberStyle); // Định dạng số #,###

            // --- Tự động điều chỉnh độ rộng cột ---
            for (int i = 0; i < headers.length; i++) {
                if (i != 5 && i != 2) { // Có thể bỏ qua cột Tác giả, Tên sách nếu quá dài
                    sheet.autoSizeColumn(i);
                } else {
                    sheet.setColumnWidth(i, 8000); // Set độ rộng cố định cho Tên sách, Tác giả
                }
            }
            sheet.setColumnWidth(1, 4500); // ISBN
            sheet.setColumnWidth(3, 3500); // Ngay XB
            sheet.setColumnWidth(4, 2000); // So trang
            sheet.setColumnWidth(6, 3000); // Ngon ngu
            sheet.setColumnWidth(7, 2500); // So cuon



            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=Bao_Cao_Dau_Sach.xlsx");

            // Ghi workbook ra response
            workbook.write(response.getOutputStream());
            workbook.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    // --- Hàm tiện ích tạo cell ---
    private void createCell(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof java.sql.Date) {
            cell.setCellValue((java.sql.Date) value);
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        }
        else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    // --- Hàm tiện ích viết dòng tổng kết thể loại ---
    private void writeCategoryFooter(Sheet sheet, int rowNum, int countDauSach, long countCuon, CellStyle labelStyle, CellStyle valueStyle) {
        Row footerRow = sheet.createRow(rowNum);
        Cell labelCell = footerRow.createCell(0); // Bắt đầu từ cột 1
        labelCell.setCellValue("Số đầu sách");
        labelCell.setCellStyle(labelStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum , rowNum, 0, 1)); // Merge 2 cột đầu

        // Giá trị số đầu sách
        createCell(footerRow, 2, countDauSach, valueStyle); // Cột Tên sách

        // Giá trị số cuốn
        createCell(footerRow, 7, countCuon, valueStyle); // Cột Số cuốn
    }


    // --- Hàm tạo Styles  ---
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createCategoryHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.BLUE.getIndex()); // Màu xanh cho header thể loại
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;
    }

    private CellStyle createBoldStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private CellStyle createNormalStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER); // Căn giữa theo chiều dọc
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook, CellStyle baseStyle) {
        CellStyle style = workbook.createCellStyle();
        style.cloneStyleFrom(baseStyle); // Kế thừa border từ baseStyle
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
        style.setAlignment(HorizontalAlignment.CENTER); // Căn giữa ngày
        return style;
    }

    private CellStyle createNumberStyle(Workbook workbook, CellStyle baseStyle) {
        CellStyle style = workbook.createCellStyle();
        style.cloneStyleFrom(baseStyle); // Kế thừa border từ baseStyle
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0")); // Định dạng số
        style.setAlignment(HorizontalAlignment.RIGHT); // Căn phải cho số
        return style;
    }

    private CellStyle createTotalStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setBorderTop(BorderStyle.MEDIUM); // Viền trên đậm hơn cho dòng tổng
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
}
