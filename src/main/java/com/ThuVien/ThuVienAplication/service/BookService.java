package com.ThuVien.ThuVienAplication.service;

import com.ThuVien.ThuVienAplication.exception.SqlCustomException;
import com.ThuVien.ThuVienAplication.model.dto.request.bookRqDto.UndoBookActionDto;
import com.ThuVien.ThuVienAplication.model.dto.response.bookRp.BookRpDto;
import com.ThuVien.ThuVienAplication.model.dto.response.bookRp.DrawerRpDto;
import com.ThuVien.ThuVienAplication.storeprocedure.bookTitleSp.QueryGetData;
import com.ThuVien.ThuVienAplication.storeprocedure.bookTitleSp.QueryStoreProcedure;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookService {

    private final DataSource dataSource;
    private final ObjectMapper objectMapper;

    public List<DrawerRpDto> handleGetListNganTu() {
        List<DrawerRpDto> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(QueryGetData.SP_Thong_tin_Ngan_Tu);
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                DrawerRpDto dto = new DrawerRpDto();
                dto.setMaNganTu(rs.getInt("MANGANTU"));
                dto.setTenNganTu(rs.getString("KE").trim() + " - " + rs.getString("MOTA").trim());
                result.add(dto);
            }
        } catch (SQLException e) {
            log.error("Error getting drawers: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi lấy danh sách ngăn tủ", e);
        }
        return result;
    }

    public List<BookRpDto> getBooksByIsbn(String isbn) {
        List<BookRpDto> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(QueryGetData.SP_Thong_tin_Sach_DauSach)) {
            cs.setString(1, isbn.trim());
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    BookRpDto dto = new BookRpDto();
                    dto.setISBN(isbn);
                    dto.setMaSach(rs.getString("MASACH").trim());
                    dto.setTinhTrang(rs.getBoolean("TINHTRANG"));
                    dto.setChoMuon(rs.getBoolean("CHOMUON"));
                    dto.setMaNganTu(rs.getInt("MANGANTU"));
                    dto.setTenNganTu(rs.getString("KE").trim() + " - " + rs.getString("MOTA").trim());
                    dto.setStatus("ORIGINAL");
                    dto.setLaSachGoc(rs.getBoolean("LaSachGoc"));

                    result.add(dto);
                }
            }
        } catch (SQLException e) {
            log.error("Error getting books for ISBN {}: {}", isbn, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi lấy danh sách sách", e);
        }
        return result;
    }

    public BookRpDto createBook(String isbn, BookRpDto bookDto) {
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(QueryStoreProcedure.Sp_ThemSach)) {
            cs.setString(1, bookDto.getMaSach().trim());
            cs.setString(2, isbn.trim());
            cs.setBoolean(3, bookDto.isTinhTrang());
            cs.setBoolean(4, bookDto.isChoMuon());
            cs.setInt(5, bookDto.getMaNganTu());
            cs.setBoolean(6, bookDto.isLaSachGoc()); // Thêm tham số mới cho sách gốc
            cs.execute();
            // Trả về chính đối tượng vừa tạo với status mới
            bookDto.setStatus("CREATED");
            return bookDto;
        } catch (SQLException e) {
            log.error("Error creating book {} for ISBN {}: {}", bookDto.getMaSach(), isbn, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi thêm sách: " + e.getMessage(), e);
        }
    }




    public BookRpDto updateBook(String isbn, String oldBookId, BookRpDto bookDto) {


        BookRpDto currentBookState = getBookById(isbn, oldBookId); // Cần tạo hàm này
        if (currentBookState == null) {
            throw new SqlCustomException("Không tìm thấy sách để cập nhật.");
        }

        if (currentBookState.isChoMuon()) {

            if (bookDto.isTinhTrang() != currentBookState.isTinhTrang() ||
                    bookDto.getMaNganTu() != currentBookState.getMaNganTu() ||
                    !bookDto.getMaSach().equals(currentBookState.getMaSach()) )
            {
                throw new SqlCustomException("Không thể sửa thông tin sách đang được cho mượn.");
            }
        }


        String sql = "{call Sp_CapNhatSach(?, ?, ?, ?, ?, ? ,?)}"; // 7 tham số
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, bookDto.getMaSach().trim()); // MASACH_MOI
            cs.setString(2, oldBookId.trim());          // MASACH_CU
            cs.setString(3, isbn.trim());               // ISBN
            cs.setBoolean(4, bookDto.isTinhTrang());    // TINHTRANG
            cs.setBoolean(5, bookDto.isChoMuon());      // CHOMUON
            cs.setInt(6, bookDto.getMaNganTu());        // MANGANTU
            cs.setBoolean(7, bookDto.isLaSachGoc());

            cs.execute();

            // Trả về dữ liệu mới để cập nhật UI
            bookDto.setStatus("UPDATED");
            return bookDto;
        } catch (SQLException e) {
            throw new SqlCustomException(e.getMessage());
        }
    }

    public void deleteBook(String isbn, String bookId) {
        try (Connection conn = dataSource.getConnection();
             CallableStatement csCheck = conn.prepareCall(QueryStoreProcedure.Sp_Check_Sch_CT_PHIEUMUON)) {
            csCheck.setString(1, bookId.trim());
            try (ResultSet rs = csCheck.executeQuery()) {
                if (rs.next()) {
                    throw new IllegalStateException("Không thể xóa sách " + bookId + " vì đang có trong phiếu mượn.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi kiểm tra phiếu mượn: " + e.getMessage(), e);
        }

        // Nếu không, tiến hành xóa
        try (Connection conn = dataSource.getConnection();
             CallableStatement csDelete = conn.prepareCall(QueryStoreProcedure.Sp_XoaSach)) {
            csDelete.setString(1, bookId.trim());
            csDelete.setString(2, isbn.trim());
            csDelete.execute();
        } catch (SQLException e) {
            log.error("Error deleting book {} for ISBN {}: {}", bookId, isbn, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi xóa sách: " + e.getMessage(), e);
        }
    }
    public void processUndo(UndoBookActionDto undoAction) throws Exception {
        String actionType = undoAction.getActionType();
        JsonNode data = undoAction.getData();

        if ("ADD".equals(actionType)) {
            String isbn = data.get("isbn").asText();
            String bookId = data.get("maSach").asText();
            deleteBook(isbn, bookId);
        }
        else if ("UPDATE".equals(actionType)) {


            // 1. Chuyển đổi JSON thành các object DTO
            BookRpDto oldData = objectMapper.treeToValue(data.get("oldData"), BookRpDto.class);
            BookRpDto newData = objectMapper.treeToValue(data.get("newData"), BookRpDto.class);
            String isbn = data.get("isbn").asText();


            updateBook(isbn, newData.getMaSach(), oldData);

        }
        else if ("DELETE".equals(actionType)) {
            String isbn = data.get("isbn").asText();
            BookRpDto originalData = objectMapper.treeToValue(data.get("originalData"), BookRpDto.class);
            createBook(isbn, originalData);
        } else {
            throw new IllegalArgumentException("Loại hành động hoàn tác không hợp lệ.");
        }
    }

    // Hàm helper để lấy 1 sách
    public BookRpDto getBookById(String isbn, String bookId) {
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(QueryGetData.SP_GetBookById)) {

            cs.setString(1, isbn.trim());
            cs.setString(2, bookId.trim());

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    BookRpDto dto = new BookRpDto();
                    dto.setISBN(rs.getString("ISBN").trim());
                    dto.setMaSach(rs.getString("MASACH").trim());
                    dto.setTinhTrang(rs.getBoolean("TINHTRANG"));
                    dto.setChoMuon(rs.getBoolean("CHOMUON"));
                    dto.setMaNganTu(rs.getInt("MANGANTU"));
                    // Ghép tên ngăn tủ từ kết quả join
                    dto.setTenNganTu(rs.getString("KE").trim() + " - " + rs.getString("MOTA").trim());
                    // Có thể thêm status nếu cần, nhưng cho get đơn lẻ thì không cần thiết
                    // dto.setStatus("ORIGINAL");
                    return dto;
                }
            }
        } catch (SQLException e) {
            log.error("Error getting book by id - ISBN: {}, BookID: {}. Error: {}", isbn, bookId, e.getMessage(), e);
            // Ném ra lỗi runtime để tầng trên (controller/interceptor) có thể xử lý
            throw new RuntimeException("Lỗi khi truy vấn thông tin sách.", e);
        }
        // Trả về null nếu không có bản ghi nào được tìm thấy
        return null;
    }
}