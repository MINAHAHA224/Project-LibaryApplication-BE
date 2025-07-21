package com.ThuVien.ThuVienAplication.service;

import com.ThuVien.ThuVienAplication.model.dto.request.rentBookRq.*;
import com.ThuVien.ThuVienAplication.model.dto.response.rentBookRp.*;
import com.ThuVien.ThuVienAplication.storeprocedure.rentBook.QuerySpRentBook;
import com.ThuVien.ThuVienAplication.utils.DateUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;


@Slf4j
@Service
@RequiredArgsConstructor
public class RentBookService {
    private final DataSource dataSource;

    public Long handleGetMaPhieuMoi() {
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(QuerySpRentBook.SP_Count_so_ma_phieu);
             ResultSet rs = cs.executeQuery()) {
            if (rs.next()) {
                return rs.getLong("SOMAPHIEU");
            }
        } catch (SQLException e) {
            log.error("Error getting new rental ticket ID: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi lấy mã phiếu mượn mới", e);
        }
        return 0L;
    }

    public List<NhanVienDto> handleGetListNhanVien() {
        List<NhanVienDto> listResult = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(QuerySpRentBook.SP_List_nhan_vien);
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                listResult.add(new NhanVienDto(rs.getInt("MANV"), rs.getString("HOTEN").trim()));
            }
        } catch (SQLException e) {
            log.error("Error getting staff list: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi lấy danh sách nhân viên", e);
        }
        return listResult;
    }

    public List<LichSuMuonDto> handleGetListLichSuMuon() {
        List<LichSuMuonDto> listResult = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(QuerySpRentBook.SP_Lich_su_muon);
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                LichSuMuonDto dto = new LichSuMuonDto();
                dto.setMaphieu(rs.getLong("MAPHIEU"));
                dto.setTendocgia(rs.getString("HOTENDG").trim());
                dto.setNgaymuon(rs.getDate("NGAYMUON"));
                dto.setHinhthuc(rs.getBoolean("HINHTHUC") ? "1" : "0");
                dto.setSosach(rs.getInt("SOSACH"));
                dto.setTennv(rs.getString("TENNV").trim());
                dto.setTrangthaitra(rs.getBoolean("TRANGTHAITRA")); // <<-- ĐẢM BẢO CÓ DÒNG NÀY

                listResult.add(dto);
            }
        } catch (SQLException e) {
            log.error("Error getting rental history: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi lấy lịch sử mượn sách", e);
        }
        return listResult;
    }

    public DocGiaDto handleGetListDocGiaActive(Long madg) {
        DocGiaDto docGiaDto = new DocGiaDto();
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(QuerySpRentBook.SP_Tim_doc_gia_active)) {
            cs.setLong(1, madg);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    docGiaDto.setHoten(rs.getString("HOTEN").trim());
                    docGiaDto.setNgaysinh(rs.getDate("NGAYSINH"));
                    docGiaDto.setDiachi(rs.getString("DIACHI").trim());
                    docGiaDto.setEmail(rs.getString("EMAILDG").trim());
                    docGiaDto.setNgayhethan(rs.getDate("NGAYHETHAN"));
                    docGiaDto.setHoatdong(rs.getBoolean("HOATDONG") ? 1 : 0);
                    docGiaDto.setMadg(madg);
                }
            }
        } catch (SQLException e) {
            log.error("Error getting active reader {}: {}", madg, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tìm độc giả", e);
        }
        return docGiaDto;
    }

    public SachChoMuonDto handleGetSachChoMuon(String maSach) {
        SachChoMuonDto sachDto = new SachChoMuonDto();
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(QuerySpRentBook.SP_Tim_sach)) {
            cs.setString(1, maSach);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    sachDto.setMasach(rs.getString("MASACH").trim());
                    sachDto.setTensach(rs.getString("TENSACH"));
                    sachDto.setChomuon(rs.getBoolean("CHOMUON") ? 1 : 0);
                    sachDto.setTinhtrang(rs.getBoolean("TINHTRANG") ? 1 : 0);
                }
            }
        } catch (SQLException e) {
            log.error("Error getting book {}: {}", maSach, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tìm sách", e);
        }
        return sachDto;
    }



    public ChiTietPhieuDayDuDto handleGetDetailPhieuMuon(Long maphieu) {
        ChiTietPhieuDayDuDto chiTietPhieu = new ChiTietPhieuDayDuDto();
        List<ChiTietSachMuonDto> danhSachSach = new ArrayList<>();

        // Cần thêm SP mới vào file Query
        String sql = "{call SP_ChiTietPhieuMuon_DayDu(?)}";

        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setLong(1, maphieu);

            boolean hasFirstResultSet = cs.execute();
            if (hasFirstResultSet) {
                try (ResultSet rsInfo = cs.getResultSet()) {
                    if (rsInfo.next()) {
                        chiTietPhieu.setMaphieu(rsInfo.getLong("MAPHIEU"));
                        chiTietPhieu.setTendocgia(rsInfo.getString("HOTENDG"));
                        chiTietPhieu.setNgaymuon(rsInfo.getTimestamp("NGAYMUON"));
                        chiTietPhieu.setHinhthuc(rsInfo.getBoolean("HINHTHUC") ? 1 : 0);
                        chiTietPhieu.setTennv(rsInfo.getString("TENNV"));
                    }
                }
            }

            if (cs.getMoreResults()) {
                try (ResultSet rsBooks = cs.getResultSet()) {
                    while (rsBooks.next()) {
                        ChiTietSachMuonDto sach = new ChiTietSachMuonDto();
                        sach.setMasach(rsBooks.getString("MASACH").trim());
                        sach.setTensach(rsBooks.getString("TENSACH"));
                        sach.setNgaytra(rsBooks.getTimestamp("NGAYTRA"));
                        sach.setDaTra(rsBooks.getBoolean("DaTra"));
                        sach.setNhanVienNhanSach(rsBooks.getString("NhanVienNhanSach"));
                        danhSachSach.add(sach);
                    }
                }
            }

            chiTietPhieu.setDanhSachSach(danhSachSach);

        } catch (SQLException e) {
            log.error("Error getting full rental detail for ticket {}: {}", maphieu, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi lấy chi tiết phiếu mượn", e);
        }

        return chiTietPhieu;
    }

    private Long checkExistDocGiaPhieuMuonChuaTraCoMayCuon(Long madg) {
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(QuerySpRentBook.SP_Kiem_tra_doc_gia_phieu_muon_chua_tra)) {
            cs.setLong(1, madg);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("SOSACH");
                }
            }
        } catch (SQLException e) {
            log.error("Error checking unreturned books for reader {}: {}", madg, e.getMessage(), e);
        }
        return 0L;
    }

    public void handleLapPhieuMuon(RentBookRqDto rentBookRqDto) throws SQLException {
        if (rentBookRqDto.getMadg() == null || rentBookRqDto.getMadg().isEmpty()) {
            throw new IllegalStateException("Mã độc giả không được để trống.");
        }
        Long soSachGuiVe = (long) rentBookRqDto.getDanhSachSach().size();
        Long madg = Long.valueOf(rentBookRqDto.getMadg());
        Long soSachChuaTra = checkExistDocGiaPhieuMuonChuaTraCoMayCuon(madg);

        if (soSachChuaTra > 0 && soSachChuaTra + soSachGuiVe > 3) {
            throw new IllegalStateException("Độc giả đang mượn " + soSachChuaTra + " cuốn. Không thể mượn thêm (vượt quá 3).");
        } else if (soSachGuiVe > 3) {
            throw new IllegalStateException("Số sách mượn không được vượt quá 3 cuốn.");
        }

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Insert into PHIEUMUON
                try (CallableStatement csPhieuMuon = conn.prepareCall(QuerySpRentBook.SP_Lap_phieu_muon)) {
                    csPhieuMuon.setLong(1, Long.parseLong(rentBookRqDto.getMaphieu()));
                    csPhieuMuon.setLong(2, madg);
                    csPhieuMuon.setBoolean(3, "1".equals(rentBookRqDto.getHinhthuc()));
                    csPhieuMuon.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
                    csPhieuMuon.setInt(5, Integer.parseInt(rentBookRqDto.getManv()));
                    csPhieuMuon.execute();
                }

                // Insert into CT_PHIEUMUON
                for (String maSach : rentBookRqDto.getDanhSachSach()) {
                    try (CallableStatement csPhieuMuonSach = conn.prepareCall(QuerySpRentBook.SP_Sach_cua_phieu_muon)) {
                        csPhieuMuonSach.setLong(1, Long.parseLong(rentBookRqDto.getMaphieu()));
                        csPhieuMuonSach.setString(2, maSach.trim());
                        csPhieuMuonSach.setBoolean(3, true); // TinhTrangMuon
                        csPhieuMuonSach.execute();
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                log.error("Error creating rental ticket, transaction rolled back: {}", e.getMessage(), e);
                throw new RuntimeException("Lỗi khi lập phiếu mượn, giao dịch đã được hủy bỏ.", e);
            }
        }
    }

    public List<OverdueBorrowingDto> fetchOverdueData() {
        List<OverdueBorrowingDto> overdueList = new ArrayList<>();
        String sql = QuerySpRentBook.SP_DanhSachDocGiaMuonSachQuaHan_GopTatCa;
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                OverdueBorrowingDto item = new OverdueBorrowingDto();
                item.setSoCmnd(rs.getString("SOCMND"));
                item.setHoDg(rs.getString("HODG"));
                item.setTenDg(rs.getString("TENDG"));
                item.setDienThoai(rs.getString("DIENTHOAI"));
                item.setEmailDg(rs.getString("EMAILDG"));
                item.setMaSachGop(rs.getString("MaSachGop"));
                item.setTenSachGop(rs.getString("TenSachGop"));
                item.setNgayMuonGop(rs.getString("NgayMuonGop"));
                item.setSoNgayMuonQuaHanGop(rs.getString("SoNgayQuaHanGop"));
                overdueList.add(item);
            }
        } catch (SQLException e) {
            log.error("Error fetching overdue data: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi lấy dữ liệu sách quá hạn", e);
        }
        return overdueList;
    }

    public void generateOverdueBorrowingsExcel(List<OverdueBorrowingDto> overdueList, String username, HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sách Quá Hạn");
        Font titleFont = workbook.createFont(); titleFont.setBold(true); titleFont.setFontHeightInPoints((short) 16);
        Font headerInfoFont = workbook.createFont(); headerInfoFont.setBold(true); headerInfoFont.setFontHeightInPoints((short) 12);
        Font tableHeaderFont = workbook.createFont(); tableHeaderFont.setBold(true);
        Font totalFont = workbook.createFont(); totalFont.setBold(true); totalFont.setFontHeightInPoints((short) 11);
        CellStyle titleStyle = workbook.createCellStyle(); titleStyle.setFont(titleFont); titleStyle.setAlignment(HorizontalAlignment.CENTER); titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        CellStyle headerInfoStyle = workbook.createCellStyle(); headerInfoStyle.setFont(headerInfoFont); headerInfoStyle.setAlignment(HorizontalAlignment.LEFT);
        CellStyle tableHeaderStyle = workbook.createCellStyle(); tableHeaderStyle.setFont(tableHeaderFont); tableHeaderStyle.setBorderTop(BorderStyle.THIN); tableHeaderStyle.setBorderBottom(BorderStyle.THIN); tableHeaderStyle.setBorderLeft(BorderStyle.THIN); tableHeaderStyle.setBorderRight(BorderStyle.THIN); tableHeaderStyle.setAlignment(HorizontalAlignment.CENTER); tableHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER); tableHeaderStyle.setWrapText(true);
        CellStyle dataCellStyle = workbook.createCellStyle(); dataCellStyle.setBorderTop(BorderStyle.THIN); dataCellStyle.setBorderBottom(BorderStyle.THIN); dataCellStyle.setBorderLeft(BorderStyle.THIN); dataCellStyle.setBorderRight(BorderStyle.THIN); dataCellStyle.setAlignment(HorizontalAlignment.LEFT); dataCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); dataCellStyle.setWrapText(true);
        CellStyle sttNumberStyle = workbook.createCellStyle(); sttNumberStyle.cloneStyleFrom(dataCellStyle); sttNumberStyle.setAlignment(HorizontalAlignment.RIGHT); sttNumberStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0")); sttNumberStyle.setWrapText(false);
        CellStyle stringNumberDataStyle = workbook.createCellStyle(); stringNumberDataStyle.cloneStyleFrom(dataCellStyle); stringNumberDataStyle.setAlignment(HorizontalAlignment.RIGHT);
        CellStyle totalLabelStyle = workbook.createCellStyle(); totalLabelStyle.setFont(totalFont); totalLabelStyle.setAlignment(HorizontalAlignment.LEFT); totalLabelStyle.setBorderTop(BorderStyle.THIN);
        CellStyle totalValueStyle = workbook.createCellStyle(); totalValueStyle.setFont(totalFont); totalValueStyle.setAlignment(HorizontalAlignment.RIGHT); totalValueStyle.setBorderTop(BorderStyle.THIN); totalValueStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
        int currentRowNum = 0;
        Row reportTitleRow = sheet.createRow(currentRowNum++); reportTitleRow.setHeightInPoints(25); Cell reportTitleCell = reportTitleRow.createCell(0); reportTitleCell.setCellValue("DANH SÁCH ĐỘC GIẢ MƯỢN SÁCH QUÁ HẠN"); reportTitleCell.setCellStyle(titleStyle); sheet.addMergedRegion(new CellRangeAddress(reportTitleRow.getRowNum(), reportTitleRow.getRowNum(), 0, 8));
        currentRowNum++;
        Row staffRow = sheet.createRow(currentRowNum++); staffRow.createCell(0).setCellValue("Nhân viên lập báo cáo:"); staffRow.getCell(0).setCellStyle(headerInfoStyle); Cell staffValueCell = staffRow.createCell(1); staffValueCell.setCellValue(username); staffValueCell.setCellStyle(headerInfoStyle); sheet.addMergedRegion(new CellRangeAddress(staffRow.getRowNum(), staffRow.getRowNum(), 1, 3));
        Row printDateRow = sheet.createRow(currentRowNum++); printDateRow.createCell(0).setCellValue("Ngày in:"); printDateRow.getCell(0).setCellStyle(headerInfoStyle); Cell printDateValueCell = printDateRow.createCell(1); printDateValueCell.setCellValue(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())); printDateValueCell.setCellStyle(headerInfoStyle); sheet.addMergedRegion(new CellRangeAddress(printDateRow.getRowNum(), printDateRow.getRowNum(), 1, 3));
        currentRowNum++;
        String[] columns = {"STT", "Số CMND", "Họ tên", "Số ĐT", "Email", "Mã sách", "Tên sách", "Ngày mượn", "Số ngày mượn quá hạn"};
        Row headerDataRow = sheet.createRow(currentRowNum++);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerDataRow.createCell(i); cell.setCellValue(columns[i]); cell.setCellStyle(tableHeaderStyle);
        }
        int stt = 1;
        BiConsumer<String, Cell> setCellValueWithNewLines = (text, cell) -> {
            if (text != null && !text.isEmpty()) cell.setCellValue(text.replace(",", "\n"));
            else cell.setCellValue("");
        };
        for (OverdueBorrowingDto item : overdueList) {
            Row dataRow = sheet.createRow(currentRowNum++);
            int cellIdx = 0;
            dataRow.createCell(cellIdx++).setCellValue(stt++); dataRow.getCell(cellIdx-1).setCellStyle(sttNumberStyle);
            dataRow.createCell(cellIdx++).setCellValue(item.getSoCmnd()); dataRow.getCell(cellIdx-1).setCellStyle(dataCellStyle);
            dataRow.createCell(cellIdx++).setCellValue(item.getHoDg() + " " + item.getTenDg()); dataRow.getCell(cellIdx-1).setCellStyle(dataCellStyle);
            dataRow.createCell(cellIdx++).setCellValue(item.getDienThoai()); dataRow.getCell(cellIdx-1).setCellStyle(dataCellStyle);
            dataRow.createCell(cellIdx++).setCellValue(item.getEmailDg()); dataRow.getCell(cellIdx-1).setCellStyle(dataCellStyle);
            Cell maSachCell = dataRow.createCell(cellIdx++); setCellValueWithNewLines.accept(item.getMaSachGop(), maSachCell); maSachCell.setCellStyle(dataCellStyle);
            Cell tenSachCell = dataRow.createCell(cellIdx++); setCellValueWithNewLines.accept(item.getTenSachGop(), tenSachCell); tenSachCell.setCellStyle(dataCellStyle);
            Cell ngayMuonCell = dataRow.createCell(cellIdx++); setCellValueWithNewLines.accept(item.getNgayMuonGop(), ngayMuonCell); ngayMuonCell.setCellStyle(dataCellStyle);
            Cell soNgayQuaHanCell = dataRow.createCell(cellIdx++); setCellValueWithNewLines.accept(item.getSoNgayMuonQuaHanGop(), soNgayQuaHanCell); soNgayQuaHanCell.setCellStyle(stringNumberDataStyle);
            int maxLines = (item.getMaSachGop() != null) ? Math.max(1, item.getMaSachGop().split(",").length) : 1;
            dataRow.setHeightInPoints((float) (maxLines * 15 + (maxLines > 1 ? 5 : 0)));
        }
        currentRowNum++;
        Row totalRow = sheet.createRow(currentRowNum++); Cell totalLabelCell = totalRow.createCell(0); totalLabelCell.setCellValue("Tổng số độc giả:"); totalLabelCell.setCellStyle(totalLabelStyle); sheet.addMergedRegion(new CellRangeAddress(currentRowNum -1, currentRowNum -1, 0, 1)); for(int i=1; i<=1; i++) totalRow.createCell(i).setCellStyle(totalLabelStyle);
        Cell totalValueCell = totalRow.createCell(2); totalValueCell.setCellValue(overdueList.size()); totalValueCell.setCellStyle(totalValueStyle); for(int i=3; i<columns.length; i++) totalRow.createCell(i).setCellStyle(totalValueStyle);
        sheet.setColumnWidth(0, 6 * 256); sheet.setColumnWidth(1, 15 * 256); sheet.setColumnWidth(2, 25 * 256); sheet.setColumnWidth(3, 15 * 256); sheet.setColumnWidth(4, 25 * 256); sheet.setColumnWidth(5, 20 * 256); sheet.setColumnWidth(6, 40 * 256); sheet.setColumnWidth(7, 15 * 256); sheet.setColumnWidth(8, 20 * 256);
        String filename = "DocGiaMuonSachQuaHan_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        workbook.write(response.getOutputStream());
        workbook.close();
    }


    public List<MostBorrowedBooksDto> fetchMostBorrowedData(String tuNgayStr, String denNgayStr) throws ParseException {
        List<MostBorrowedBooksDto> bookList = new ArrayList<>();
        SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date tuNgayUtil = sdfInput.parse(tuNgayStr);
        java.util.Date denNgayUtil = sdfInput.parse(denNgayStr);
        java.sql.Date tuNgaySql = new java.sql.Date(tuNgayUtil.getTime());
        java.sql.Date denNgaySql = new java.sql.Date(denNgayUtil.getTime());

        String sql = QuerySpRentBook.SP_ThongKeDauSachMuonNhieu_Full;
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setDate(1, tuNgaySql);
            cs.setDate(2, denNgaySql);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    MostBorrowedBooksDto item = new MostBorrowedBooksDto();
                    item.setIsbn(rs.getString("ISBN"));
                    item.setTenSach(rs.getString("TENSACH"));
                    item.setTacGia(rs.getString("TacGia"));
                    item.setTheLoai(rs.getString("TheLoai"));
                    item.setSoLuotMuon(rs.getInt("SoLuotMuon"));
                    item.setGhiChu(rs.getString("GhiChu"));
                    bookList.add(item);
                }
            }
        } catch (SQLException e) {
            log.error("Error fetching most borrowed data: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi lấy dữ liệu sách mượn nhiều", e);
        }
        return bookList;
    }

    public void generateMostBorrowedBooksExcel(List<MostBorrowedBooksDto> bookList, String username, String tuNgay, String denNgay, HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Đầu Sách Mượn Nhiều");
        Font titleFont = workbook.createFont(); titleFont.setBold(true); titleFont.setFontHeightInPoints((short) 16);
        Font headerInfoFont = workbook.createFont(); headerInfoFont.setBold(true); headerInfoFont.setFontHeightInPoints((short) 12);
        Font tableHeaderFont = workbook.createFont(); tableHeaderFont.setBold(true);
        Font totalFont = workbook.createFont(); totalFont.setBold(true); totalFont.setFontHeightInPoints((short) 11);
        CellStyle titleStyle = workbook.createCellStyle(); titleStyle.setFont(titleFont); titleStyle.setAlignment(HorizontalAlignment.CENTER); titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        CellStyle headerInfoStyle = workbook.createCellStyle(); headerInfoStyle.setFont(headerInfoFont); headerInfoStyle.setAlignment(HorizontalAlignment.LEFT);
        CellStyle tableHeaderStyle = workbook.createCellStyle(); tableHeaderStyle.setFont(tableHeaderFont); tableHeaderStyle.setBorderTop(BorderStyle.THIN); tableHeaderStyle.setBorderBottom(BorderStyle.THIN); tableHeaderStyle.setBorderLeft(BorderStyle.THIN); tableHeaderStyle.setBorderRight(BorderStyle.THIN); tableHeaderStyle.setAlignment(HorizontalAlignment.CENTER); tableHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER); tableHeaderStyle.setWrapText(true);
        CellStyle dataCellStyle = workbook.createCellStyle(); dataCellStyle.setBorderTop(BorderStyle.THIN); dataCellStyle.setBorderBottom(BorderStyle.THIN); dataCellStyle.setBorderLeft(BorderStyle.THIN); dataCellStyle.setBorderRight(BorderStyle.THIN); dataCellStyle.setAlignment(HorizontalAlignment.LEFT); dataCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); dataCellStyle.setWrapText(true);
        CellStyle numberCellStyle = workbook.createCellStyle(); numberCellStyle.cloneStyleFrom(dataCellStyle); numberCellStyle.setAlignment(HorizontalAlignment.RIGHT); numberCellStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0")); numberCellStyle.setWrapText(false);
        CellStyle totalLabelStyle = workbook.createCellStyle(); totalLabelStyle.setFont(totalFont); totalLabelStyle.setAlignment(HorizontalAlignment.LEFT);
        CellStyle totalValueStyle = workbook.createCellStyle(); totalValueStyle.setFont(totalFont); totalValueStyle.setAlignment(HorizontalAlignment.RIGHT); totalValueStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
        int currentRowNum = 0;
        Row reportTitleRow = sheet.createRow(currentRowNum++); reportTitleRow.setHeightInPoints(25); Cell reportTitleCell = reportTitleRow.createCell(0); reportTitleCell.setCellValue("DANH MỤC ĐẦU SÁCH ĐƯỢC MƯỢN NHIỀU"); reportTitleCell.setCellStyle(titleStyle); sheet.addMergedRegion(new CellRangeAddress(reportTitleRow.getRowNum(), reportTitleRow.getRowNum(), 0, 6));
        Row periodRow = sheet.createRow(currentRowNum++); periodRow.createCell(0).setCellValue("TỪ NGÀY: " + tuNgay); periodRow.getCell(0).setCellStyle(headerInfoStyle); sheet.addMergedRegion(new CellRangeAddress(periodRow.getRowNum(), periodRow.getRowNum(), 0, 2)); Cell denNgayLabelCell = periodRow.createCell(3); denNgayLabelCell.setCellValue("ĐẾN NGÀY: " + denNgay); denNgayLabelCell.setCellStyle(headerInfoStyle); sheet.addMergedRegion(new CellRangeAddress(periodRow.getRowNum(), periodRow.getRowNum(), 3, 6));
        currentRowNum++;
        Row staffRow = sheet.createRow(currentRowNum++); staffRow.createCell(0).setCellValue("Nhân viên lập báo cáo:"); staffRow.getCell(0).setCellStyle(headerInfoStyle); Cell staffValueCell = staffRow.createCell(2); staffValueCell.setCellValue(username); staffValueCell.setCellStyle(headerInfoStyle); sheet.addMergedRegion(new CellRangeAddress(staffRow.getRowNum(), staffRow.getRowNum(), 2, 4));
        Row printDateRow = sheet.createRow(currentRowNum++); printDateRow.createCell(0).setCellValue("Ngày in:"); printDateRow.getCell(0).setCellStyle(headerInfoStyle); Cell printDateValueCell = printDateRow.createCell(2); printDateValueCell.setCellValue(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())); printDateValueCell.setCellStyle(headerInfoStyle); sheet.addMergedRegion(new CellRangeAddress(printDateRow.getRowNum(), printDateRow.getRowNum(), 2, 4));
        currentRowNum++;
        String[] columns = {"STT", "ISBN", "Tên sách", "Tác giả", "Thể loại", "Số lượt mượn", "Ghi chú"};
        Row headerDataRow = sheet.createRow(currentRowNum++);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerDataRow.createCell(i); cell.setCellValue(columns[i]); cell.setCellStyle(tableHeaderStyle);
        }
        int stt = 1;
        for (MostBorrowedBooksDto item : bookList) {
            Row dataRow = sheet.createRow(currentRowNum++);
            int cellIdx = 0;
            dataRow.createCell(cellIdx++).setCellValue(stt++); dataRow.getCell(cellIdx-1).setCellStyle(numberCellStyle);
            dataRow.createCell(cellIdx++).setCellValue(item.getIsbn()); dataRow.getCell(cellIdx-1).setCellStyle(dataCellStyle);
            dataRow.createCell(cellIdx++).setCellValue(item.getTenSach()); dataRow.getCell(cellIdx-1).setCellStyle(dataCellStyle);
            Cell tacGiaCell = dataRow.createCell(cellIdx++); String tacGiaStr = item.getTacGia(); if (tacGiaStr != null) { tacGiaCell.setCellValue(tacGiaStr.replace(", ", ",\n")); } tacGiaCell.setCellStyle(dataCellStyle);
            dataRow.createCell(cellIdx++).setCellValue(item.getTheLoai()); dataRow.getCell(cellIdx-1).setCellStyle(dataCellStyle);
            Cell luotMuonCell = dataRow.createCell(cellIdx++); if (item.getSoLuotMuon() != null) { luotMuonCell.setCellValue(item.getSoLuotMuon()); } luotMuonCell.setCellStyle(numberCellStyle);
            dataRow.createCell(cellIdx++).setCellValue(item.getGhiChu()); dataRow.getCell(cellIdx-1).setCellStyle(dataCellStyle);
            int linesForTacGia = (tacGiaStr != null && !tacGiaStr.isEmpty()) ? tacGiaStr.split(",").length : 1; if (linesForTacGia > 1) { dataRow.setHeightInPoints((float) (linesForTacGia * 15)); }
        }
        currentRowNum++;
        Row totalBooksRow = sheet.createRow(currentRowNum++); Cell totalBooksLabelCell = totalBooksRow.createCell(0); totalBooksLabelCell.setCellValue("Tổng số đầu sách:"); totalBooksLabelCell.setCellStyle(totalLabelStyle); sheet.addMergedRegion(new CellRangeAddress(currentRowNum - 1, currentRowNum - 1, 0, 4)); for(int i = 1; i <= 4; i++) totalBooksRow.createCell(i).setCellStyle(totalLabelStyle);
        Cell totalBooksValueCell = totalBooksRow.createCell(5); totalBooksValueCell.setCellValue(bookList.size()); totalBooksValueCell.setCellStyle(totalValueStyle); totalBooksRow.createCell(6).setCellStyle(totalValueStyle);
        sheet.setColumnWidth(0, 6 * 256); sheet.setColumnWidth(1, 18 * 256); sheet.setColumnWidth(2, 35 * 256); sheet.setColumnWidth(3, 30 * 256); sheet.setColumnWidth(4, 20 * 256); sheet.setColumnWidth(5, 15 * 256); sheet.setColumnWidth(6, 20 * 256);
        String filename = "DauSachMuonNhieu_" + tuNgay.replace("/", "") + "_" + denNgay.replace("/", "") + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        workbook.write(response.getOutputStream());
        workbook.close();
    }


    public void traSach(Long maphieu, String masach, String tinhTrang, Integer maNV) throws SQLException {
        String sql = "{call SP_TraSach(?, ?, ?, ?, ?)}";
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setLong(1, maphieu);
            cs.setString(2, masach);
            cs.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            cs.setString(4, tinhTrang);
            cs.setInt(5, maNV);
            cs.execute();
        }
    }



    public List<AvailableBookDto> getAllAvailableBooks() {
        List<AvailableBookDto> list = new ArrayList<>();
        String sql = "{call SP_GetAllAvailableBooks}";
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                AvailableBookDto dto = new AvailableBookDto();
                dto.setMasach(rs.getString("MASACH").trim());
                dto.setTensach(rs.getString("TENSACH"));
                dto.setIsbn(rs.getString("ISBN").trim());
                list.add(dto);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách sách có sẵn", e);
        }
        return list;
    }

    public List<ActiveReaderDto> getAllActiveReaders() {
        List<ActiveReaderDto> list = new ArrayList<>();
        String sql = "{call SP_GetAllActiveReaders}";
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                ActiveReaderDto dto = new ActiveReaderDto();
                dto.setMadg(rs.getLong("MADG"));
                dto.setHodg(rs.getString("HODG"));
                dto.setTendg(rs.getString("TENDG"));
                dto.setSocmnd(rs.getString("SOCMND"));
                list.add(dto);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách độc giả hợp lệ", e);
        }
        return list;
    }

}
