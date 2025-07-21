package com.ThuVien.ThuVienAplication.service;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;


@Service
public class ExcelExporter {
    @Value("${database.url}")
    private String dbUrl;
    @Value("${database.drive}")
    private String driver;

    String user = "sa";
    String pass = "123456";

    public void exportDauSachToExcel(String nhanVienLap, HttpServletResponse response) {

        String sql = """
            WITH SachCounts AS (
                SELECT ISBN, COUNT(*) AS SoCuonSach FROM SACH GROUP BY ISBN
            ),
            AuthorList AS (
                SELECT
                    ts.ISBN,
                    STRING_AGG(tg.HOTENTG, CHAR(10)) WITHIN GROUP (ORDER BY tg.HOTENTG) AS DanhSachTacGia -- SỬA Ở ĐÂY: Dùng CHAR(10)
                FROM TACGIA_SACH ts JOIN TACGIA tg ON ts.MATACGIA = tg.MATACGIA
                GROUP BY ts.ISBN
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

        Workbook workbook = new XSSFWorkbook();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {

            Class.forName(driver);
            conn = DriverManager.getConnection(dbUrl, user, pass);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            Sheet sheet = workbook.createSheet("Danh mục Đầu sách");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle categoryHeaderStyle = createCategoryHeaderStyle(workbook);
            CellStyle boldStyle = createBoldStyle(workbook);
            CellStyle normalStyle = createNormalStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook, normalStyle);
            CellStyle numberStyle = createNumberStyle(workbook, normalStyle);
            CellStyle totalStyle = createTotalStyle(workbook);
            CellStyle totalNumberStyle = createNumberStyle(workbook, totalStyle);

            int rowNum = 0;


            rowNum++;
            Row titleRow = sheet.createRow(rowNum++);
            titleRow.setHeightInPoints(25);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("DANH MỤC ĐẦU SÁCH");
            titleCell.setCellStyle(titleStyle);

            sheet.addMergedRegion(new CellRangeAddress(titleRow.getRowNum(), titleRow.getRowNum(), 0, 7));

            // Dòng nhân viên
            Row nvRow = sheet.createRow(rowNum++);
            Cell nvCell = nvRow.createCell(0);
            nvCell.setCellValue("Nhân viên lập báo cáo: " + (nhanVienLap != null ? nhanVienLap : ""));
            nvCell.setCellStyle(boldStyle);
            sheet.addMergedRegion(new CellRangeAddress(nvRow.getRowNum(), nvRow.getRowNum(), 0, 7));

            // Dòng ngày in
            Row dateRow = sheet.createRow(rowNum++);
            Cell dateCell = dateRow.createCell(0);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            dateCell.setCellValue("Ngày in: " + sdf.format(new Date()));
            dateCell.setCellStyle(boldStyle);
            sheet.addMergedRegion(new CellRangeAddress(dateRow.getRowNum(), dateRow.getRowNum(), 0, 7));

            rowNum++; // Thêm một dòng trống

            // --- Header của bảng dữ liệu ---
            int tableHeaderRowNum = rowNum++; // Lưu lại dòng header
            Row tableHeaderRow = sheet.createRow(tableHeaderRowNum);
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
            int firstRowOfCategory = -1;

            while (rs.next()) {
                String theLoai = rs.getString("THELOAI");
                String isbn = rs.getString("ISBN");
                String tenSach = rs.getString("TENSACH");
                java.sql.Date ngayXB = rs.getDate("NGAYXUATBAN");
                int soTrang = rs.getInt("SOTRANG");
                String tacGia = rs.getString("TacGia");
                String ngonNgu = rs.getString("NgonNgu");
                long soCuon = rs.getLong("SoCuon");


                if (currentTheLoai == null || !currentTheLoai.equals(theLoai)) {

                    if (currentTheLoai != null) {
                        int footerRowNum = rowNum++;
                        writeCategoryFooter(sheet, footerRowNum, soDauSachTrongTheLoai, soCuonTrongTheLoai, totalStyle, totalNumberStyle);
                        rowNum++;
                    }

                    // In header cho thể loại mới
                    int categoryHeaderRowNum = rowNum++;
                    Row categoryRow = sheet.createRow(categoryHeaderRowNum);
                    Cell categoryCell = categoryRow.createCell(0);
                    categoryCell.setCellValue("Thể loại : " + theLoai);
                    categoryCell.setCellStyle(categoryHeaderStyle); // Style đã có border
                    for (int i = 1; i <= 7; i++) {
                        Cell emptyHeaderCell = categoryRow.createCell(i);
                        emptyHeaderCell.setCellStyle(categoryHeaderStyle);
                    }
                    sheet.addMergedRegion(new CellRangeAddress(categoryHeaderRowNum, categoryHeaderRowNum, 0, 7));

                    // Reset bộ đếm và đánh dấu dòng bắt đầu
                    currentTheLoai = theLoai;
                    sttTrongTheLoai = 1;
                    soDauSachTrongTheLoai = 0;
                    soCuonTrongTheLoai = 0;
                    firstRowOfCategory = rowNum; // Dòng data đầu tiên của thể loại này
                }

                Row dataRow = sheet.createRow(rowNum++);
                createCell(dataRow, 0, sttTrongTheLoai++, numberStyle); // STT
                createCell(dataRow, 1, isbn, normalStyle);           // ISBN
                createCell(dataRow, 2, tenSach, normalStyle);        // Tên sách
                createCell(dataRow, 3, ngayXB, dateStyle);           // Ngày XB
                createCell(dataRow, 4, soTrang, numberStyle);        // Số trang
                createCell(dataRow, 5, tacGia, normalStyle);         // Tác giả (style normal đã có wrap text)
                createCell(dataRow, 6, ngonNgu, normalStyle);        // Ngôn ngữ
                createCell(dataRow, 7, soCuon, numberStyle);         // Số cuốn

                // --- Cập nhật bộ đếm ---
                soDauSachTrongTheLoai++;
                soCuonTrongTheLoai += soCuon;
                tongSoDauSachThuVien++;
                tongSoCuonThuVien += soCuon;
            }

            // --- In dòng tổng kết cho thể loại CUỐI CÙNG ---
            if (currentTheLoai != null) {
                int footerRowNum = rowNum++;
                writeCategoryFooter(sheet, footerRowNum, soDauSachTrongTheLoai, soCuonTrongTheLoai, totalStyle, totalNumberStyle);
            }

            rowNum++; // Dòng trống

            // --- In dòng tổng kết toàn thư viện ---
            int finalTotalRowNum = rowNum++;
            Row finalTotalRow = sheet.createRow(finalTotalRowNum);
            // Ô label (merged)
            Cell totalLabelCell = finalTotalRow.createCell(0);
            totalLabelCell.setCellValue("Số đầu sách thư viện");
            totalLabelCell.setCellStyle(totalStyle);
            // Tạo ô phụ để đảm bảo border
            Cell mergedHelperCellTotal1 = finalTotalRow.createCell(1);
            mergedHelperCellTotal1.setCellStyle(totalStyle);
            sheet.addMergedRegion(new CellRangeAddress(finalTotalRowNum, finalTotalRowNum, 0, 1));

            // Giá trị tổng số đầu sách
            createCell(finalTotalRow, 2, tongSoDauSachThuVien, totalNumberStyle);


            for (int i = 3; i < 7; i++) {
                Cell emptyTotalCell = finalTotalRow.createCell(i);
                emptyTotalCell.setCellStyle(totalStyle);
            }


            createCell(finalTotalRow, 7, tongSoCuonThuVien, totalNumberStyle);


            for (int i = 0; i < headers.length; i++) {
                if (i == 5) {
                    sheet.setColumnWidth(i, 8000);
                } else if (i == 2) {
                    sheet.setColumnWidth(i, 10000);
                }
                else {
                    sheet.autoSizeColumn(i);
                }
            }

            sheet.setColumnWidth(0, 1500); // STT
            sheet.setColumnWidth(1, 4500); // ISBN
            sheet.setColumnWidth(3, 3500); // Ngay XB
            sheet.setColumnWidth(4, 2000); // So trang
            sheet.setColumnWidth(6, 3000); // Ngon ngu
            sheet.setColumnWidth(7, 2500); // So cuon


            // --- Ghi workbook ra response ---
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=Bao_Cao_Dau_Sach.xlsx");

            workbook.write(response.getOutputStream());


        } catch (Exception e) {
            e.printStackTrace();

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                response.getWriter().write("Lỗi khi xuất Excel: " + e.getMessage());
            } catch (IOException ioException) {

            }
        } finally {
            // --- Đóng tài nguyên ---
            try { if (rs != null) rs.close(); } catch (Exception e) {  }
            try { if (stmt != null) stmt.close(); } catch (Exception e) {  }
            try { if (conn != null) conn.close(); } catch (Exception e) {  }
            try { if (workbook != null) workbook.close(); } catch (Exception e) {  }
        }
    }

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
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }
        // Luôn áp dụng style nếu có
        if (style != null) {
            cell.setCellStyle(style);
        }
    }


    private void writeCategoryFooter(Sheet sheet, int rowNum, int countDauSach, long countCuon, CellStyle labelStyle, CellStyle valueStyle) {
        Row footerRow = sheet.createRow(rowNum);
        int lastCol = 7;


        Cell labelCell = footerRow.createCell(0);
        labelCell.setCellValue("Số đầu sách");
        labelCell.setCellStyle(labelStyle);

        Cell mergedHelperCell1 = footerRow.createCell(1);
        mergedHelperCell1.setCellStyle(labelStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 1));


        createCell(footerRow, 2, countDauSach, valueStyle);


        for (int i = 3; i < lastCol; i++) {
            Cell emptyCell = footerRow.createCell(i);
            emptyCell.setCellStyle(labelStyle);
        }

        createCell(footerRow, lastCol, countCuon, valueStyle);
    }



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
        style.setWrapText(true);
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

    // Sửa: Thêm borders
    private CellStyle createCategoryHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.BLUE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        // Thêm Borders
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createBoldStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    // Sửa: Thêm Wrap Text
    private CellStyle createNormalStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook, CellStyle baseStyle) {
        CellStyle style = workbook.createCellStyle();
        style.cloneStyleFrom(baseStyle);
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
        style.setAlignment(HorizontalAlignment.CENTER);

        return style;
    }


    private CellStyle createNumberStyle(Workbook workbook, CellStyle baseStyle) {
        CellStyle style = workbook.createCellStyle();
        style.cloneStyleFrom(baseStyle);
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
        style.setAlignment(HorizontalAlignment.RIGHT);

        return style;
    }


    private CellStyle createTotalStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        return style;
    }
}
