package com.ThuVien.ThuVienAplication.service;

import com.ThuVien.ThuVienAplication.exception.SqlCustomException;
import com.ThuVien.ThuVienAplication.model.dto.request.readerRqDto.ReaderRqCreateDto;
import com.ThuVien.ThuVienAplication.model.dto.request.readerRqDto.ReaderRqUpdateDto;
import com.ThuVien.ThuVienAplication.model.dto.request.readerRqDto.UndoActionDto;
import com.ThuVien.ThuVienAplication.model.dto.response.readerRp.ReaderRpDto;
import com.ThuVien.ThuVienAplication.storeprocedure.reader.QuerySpReader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReaderService {

    private final DataSource dataSource;
    private final ObjectMapper objectMapper;


    private ReaderRpDto mapResultSetToReaderRpDto(ResultSet rs) throws SQLException {
        ReaderRpDto reader = new ReaderRpDto();
        reader.setMadg(rs.getLong("MADG"));
        reader.setHodg(rs.getString("HODG").trim());
        reader.setTendg(rs.getString("TENDG").trim());
        reader.setEmaildg(rs.getString("EMAILDG").trim());
        reader.setSocmnd(rs.getString("SOCMND").trim());
        reader.setGioitinh(rs.getInt("GIOITINH"));
        reader.setNgaysinh(rs.getDate("NGAYSINH"));
        reader.setDiachi(rs.getString("DIACHI").trim());
        reader.setDienthoai(rs.getString("DIENTHOAI").trim());
        reader.setNgaylamthe(rs.getDate("NGAYLAMTHE"));
        reader.setNgayhethan(rs.getDate("NGAYHETHAN"));
        reader.setHoatdong(rs.getInt("HOATDONG"));
        return reader;
    }

    public List<ReaderRpDto> getAllReaders() {
        List<ReaderRpDto> result = new ArrayList<>();
        String sql = QuerySpReader.SP_Thong_ke_doc_gia;
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                result.add(mapResultSetToReaderRpDto(rs));
            }
        } catch (SQLException e) {
            log.error("Error getting all readers: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi lấy danh sách độc giả", e);
        }
        return result;
    }

    public ReaderRpDto getReaderById(Long id) {
        String sql = QuerySpReader.SP_Tim_doc_gia;
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setLong(1, id);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReaderRpDto(rs);
                }
            }
        } catch (SQLException e) {
            log.error("Error getting reader by id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tìm độc giả", e);
        }
        return null;
    }

    public ReaderRpDto createReader(ReaderRqCreateDto request) {

        // check email tồn tại
        String email = request.getEmaildg().trim();
        existByEmail(email);


        String sql = QuerySpReader.SP_Tao_moi_doc_gia;
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, request.getHodg().trim());
            cs.setString(2, request.getTendg().trim());
            cs.setString(3, request.getEmaildg().trim());
            cs.setString(4, request.getSocmnd().trim());
            cs.setBoolean(5, request.getGioitinh());
            cs.setDate(6, new java.sql.Date(request.getNgaysinh().getTime()));
            cs.setString(7, request.getDiachi().trim());
            cs.setString(8, request.getDienthoai().trim());
            cs.setDate(9, new java.sql.Date(request.getNgaylamthe().getTime()));
            cs.setDate(10, new java.sql.Date(request.getNgayhethan().getTime()));
            cs.setBoolean(11, request.getHoatdong());

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    Long newId = rs.getLong("NewMADG");
                    return getReaderById(newId);
                }
            }
        } catch (SQLException e) {
            log.error("Error creating reader: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tạo độc giả: " + e.getMessage(), e);
        }
        throw new RuntimeException("Không thể tạo độc giả mới.");
    }

    public ReaderRpDto updateReader(Long id, ReaderRqUpdateDto request) {

       boolean byBassExistEmailReader = byBassExistEmailReader(id ,request.getEmaildg().trim() );
       if ( !byBassExistEmailReader){
           existByEmail(request.getEmaildg().trim());
       }

        String sql = QuerySpReader.SP_Cap_nhat_doc_gia;
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setLong(1, id);
            cs.setString(2, request.getHodg().trim());
            cs.setString(3, request.getTendg().trim());
            cs.setString(4, request.getEmaildg().trim());
            cs.setString(5, request.getSocmnd().trim());
            cs.setBoolean(6, request.getGioitinh());
            cs.setDate(7, new java.sql.Date(request.getNgaysinh().getTime()));
            cs.setString(8, request.getDiachi().trim());
            cs.setString(9, request.getDienthoai().trim());
            cs.setDate(10, new java.sql.Date(request.getNgaylamthe().getTime()));
            cs.setDate(11, new java.sql.Date(request.getNgayhethan().getTime()));
            cs.setBoolean(12, request.getHoatdong());
            cs.execute();
            return getReaderById(id);
        } catch (SQLException e) {
            log.error("Error updating reader {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi cập nhật độc giả: " + e.getMessage(), e);
        }
    }

    public void deleteReader(Long id) {
        String sql = QuerySpReader.SP_Xoa_doc_gia;
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setLong(1, id);
            cs.execute();
        } catch (SQLException e) {
            log.error("Error deleting reader {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi xóa độc giả. Có thể độc giả đang có phiếu mượn chưa trả.", e);
        }
    }


    public void generateReadersExcelReport(List<ReaderRpDto> readers, String username, HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Danh Sách Độc Giả");
        // --- Font Styles ---
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);

        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);

        Font tableHeaderFont = workbook.createFont();
        tableHeaderFont.setBold(true);
        tableHeaderFont.setColor(IndexedColors.BLACK.getIndex());


        // --- Cell Styles ---
        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle headerInfoStyle = workbook.createCellStyle();
        headerInfoStyle.setFont(headerFont);
        headerInfoStyle.setAlignment(HorizontalAlignment.LEFT);

        CellStyle tableHeaderStyle = workbook.createCellStyle();
        tableHeaderStyle.setFont(tableHeaderFont);
        tableHeaderStyle.setBorderTop(BorderStyle.THIN);
        tableHeaderStyle.setBorderBottom(BorderStyle.THIN);
        tableHeaderStyle.setBorderLeft(BorderStyle.THIN);
        tableHeaderStyle.setBorderRight(BorderStyle.THIN);
        tableHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
        tableHeaderStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        tableHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);


        CellStyle dataCellStyle = workbook.createCellStyle();
        dataCellStyle.setBorderTop(BorderStyle.THIN);
        dataCellStyle.setBorderBottom(BorderStyle.THIN);
        dataCellStyle.setBorderLeft(BorderStyle.THIN);
        dataCellStyle.setBorderRight(BorderStyle.THIN);
        dataCellStyle.setAlignment(HorizontalAlignment.LEFT);

        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.cloneStyleFrom(dataCellStyle);
        CreationHelper createHelper = workbook.getCreationHelper();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));



        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("DANH SÁCH ĐỘC GIẢ");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 7));


        Row staffRow = sheet.createRow(2); // Dòng 1 để trống
        Cell staffLabelCell = staffRow.createCell(0);
        staffLabelCell.setCellValue("Nhân viên lập báo cáo:");
        Cell staffValueCell = staffRow.createCell(1);
        staffValueCell.setCellValue(username != null ? username : "N/A");
        staffLabelCell.setCellStyle(headerInfoStyle);
        staffValueCell.setCellStyle(headerInfoStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(2, 2, 1, 3));


        Row dateRow = sheet.createRow(3);
        Cell dateLabelCell = dateRow.createCell(0);
        dateLabelCell.setCellValue("Ngày in:");
        Cell dateValueCell = dateRow.createCell(1);
        dateValueCell.setCellValue(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date()));
        dateLabelCell.setCellStyle(headerInfoStyle);
        dateValueCell.setCellStyle(headerInfoStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(3, 3, 1, 3));


        Row headerDataRow = sheet.createRow(5);
        String[] columns = {"STT", "Họ tên", "Số CMND", "Phái", "Địa chỉ", "Số ĐT", "Ngày làm thẻ", "Trạng thái"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerDataRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(tableHeaderStyle);
        }

        int rowNum = 6; // Dữ liệu bắt đầu từ dòng 6
        int stt = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (ReaderRpDto reader : readers) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(stt++);
            row.getCell(0).setCellStyle(dataCellStyle);

            row.createCell(1).setCellValue(reader.getHodg() + " " + reader.getTendg());
            row.getCell(1).setCellStyle(dataCellStyle);

            row.createCell(2).setCellValue(reader.getSocmnd());
            row.getCell(2).setCellStyle(dataCellStyle);

            String gioiTinhStr = "";
            if (reader.getGioitinh() != null) {
                gioiTinhStr = reader.getGioitinh() == 1 ? "Nam" : (reader.getGioitinh() == 0 ? "Nữ" : "Khác");
            }
            row.createCell(3).setCellValue(gioiTinhStr);
            row.getCell(3).setCellStyle(dataCellStyle);

            row.createCell(4).setCellValue(reader.getDiachi());
            row.getCell(4).setCellStyle(dataCellStyle);

            row.createCell(5).setCellValue(reader.getDienthoai());
            row.getCell(5).setCellStyle(dataCellStyle);

            Cell ngayLamTheCell = row.createCell(6);
            if (reader.getNgaylamthe() != null) {
                ngayLamTheCell.setCellValue(reader.getNgaylamthe());
            }
            ngayLamTheCell.setCellStyle(dateCellStyle);


            String trangThai = "";
            if (reader.getHoatdong() != null) {
                trangThai = reader.getHoatdong() == 0 ? "Bị Khóa" : "Hoạt động";
            }
            row.createCell(7).setCellValue(trangThai);
            row.getCell(7).setCellStyle(dataCellStyle);
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }
        sheet.setColumnWidth(1, 25 * 256); // Họ tên
        sheet.setColumnWidth(4, 30 * 256); // Địa chỉ


        String filename = "DanhSachDocGia_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        workbook.write(response.getOutputStream());
        workbook.close();
    }


    public void processUndo(UndoActionDto undoAction) throws Exception {
        String actionType = undoAction.getActionType();
        JsonNode data = undoAction.getData();

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

        if ("ADD".equals(actionType)) {
            Long idToDelete = data.get("madg").asLong();
            deleteReader(idToDelete);
        }
        else if ("UPDATE".equals(actionType)) {
            ReaderRqUpdateDto oldData = objectMapper.treeToValue(data.get("oldData"), ReaderRqUpdateDto.class);
            updateReader(oldData.getMadg(), oldData);
        }
        else if ("DELETE".equals(actionType)) {
            ReaderRpDto dataToRecreate = objectMapper.treeToValue(data.get("originalData"), ReaderRpDto.class);
            createReaderUndo(dataToRecreate); // Gọi một hàm undo riêng
        } else {
            throw new IllegalArgumentException("Loại hành động hoàn tác không hợp lệ: " + actionType);
        }
    }

    private void createReaderUndo(ReaderRpDto request) throws SQLException {
        String sql = QuerySpReader.SP_Tao_moi_doc_gia_undo;
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setLong(1, request.getMadg());
            cs.setString(2, request.getHodg());
            cs.setString(3, request.getTendg());
            cs.setString(4, request.getEmaildg());
            cs.setString(5, request.getSocmnd());
            cs.setBoolean(6, request.getGioitinh() == 1);
            cs.setDate(7, new java.sql.Date(request.getNgaysinh().getTime()));
            cs.setString(8, request.getDiachi());
            cs.setString(9, request.getDienthoai());
            cs.setDate(10, new java.sql.Date(request.getNgaylamthe().getTime()));
            cs.setDate(11, new java.sql.Date(request.getNgayhethan().getTime()));
            cs.setBoolean(12, request.getHoatdong() == 1);
            cs.execute();
        }
    }



    private boolean byBassExistEmailReader(Long id, String email) {


        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall("{call SP_ByBassExistEmailReader(?,?)}")) {
            cs.setLong(1, id);
            cs.setString(2, email);
            cs.execute();
            return true;
        } catch (SQLException e) {
            log.error("Email khác nhau độc giả = {}", e.getMessage(), e);
            return false;
        }

    }


    private void existByEmail(String email) {

        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall("{call SP_ExistByEmail(?)}")) {
            cs.setString(1, email);
            cs.execute();
        } catch (SQLException e) {
            log.error("Error ExistByEmail staff: {}", e.getMessage(), e);
            throw new SqlCustomException(e.getMessage());
        }

    }




}