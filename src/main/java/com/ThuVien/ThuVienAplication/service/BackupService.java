//package com.ThuVien.ThuVienAplication.service;
//
//import com.ThuVien.ThuVienAplication.exception.SqlCustomException;
//import com.ThuVien.ThuVienAplication.model.dto.request.backupRq.BackupRequestDto;
//import com.ThuVien.ThuVienAplication.model.dto.request.backupRq.RestoreRequestDto;
//import com.ThuVien.ThuVienAplication.model.dto.response.backupRp.BackupHistoryDto;
//import com.ThuVien.ThuVienAplication.model.dto.response.backupRp.DatabaseDto;
//import com.ThuVien.ThuVienAplication.storeprocedure.backup.QuerySpBackup;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import javax.sql.DataSource;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.sql.*;
//import java.text.SimpleDateFormat;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//@Service
//@Slf4j
//
//@RequiredArgsConstructor
//public class BackupService {
//
//    private final DataSource dataSource;
//
//
//    @Value("${backup.storage.path}")
//    private String backupStoragePath;
//
//    @Value("${master.datasource.url}")
//    private String masterUrl;
//    @Value("${master.datasource.username}")
//    private String masterUsername;
//    @Value("${master.datasource.password}")
//    private String masterPassword;
//
//
//
//
//    public List<DatabaseDto> handleGetListDatabase() {
//        List<DatabaseDto> listResult = new ArrayList<>();
//        try (Connection conn = dataSource.getConnection();
//             CallableStatement cs = conn.prepareCall(QuerySpBackup.Sp_Danh_sach_cac_database);
//             ResultSet rs = cs.executeQuery()) {
//            while (rs.next()) {
//                listResult.add(new DatabaseDto(rs.getString("name")));
//            }
//        } catch (SQLException e) {
//            log.error("--ER handleGetListDatabase: {}", e.getMessage(), e);
//            throw new RuntimeException("Lỗi khi lấy danh sách database: " + e.getMessage());
//        }
//        return listResult;
//    }
//
//    public boolean handleBackupDatabase(BackupRequestDto backupRequestDto, String username) {
//        log.info("User '{}' is requesting a backup for DB: {}", username, backupRequestDto.getDbName());
//        try (Connection conn = dataSource.getConnection();
//             CallableStatement cs = conn.prepareCall("{call SP_Backup_database(?, ?)}")) { // SP giờ chỉ có 2 tham số
//            cs.setString(1, backupRequestDto.getDbName());
//            cs.setBoolean(2, backupRequestDto.isOverwrite());
//            cs.execute();
//            return true;
//        } catch (SQLException e) {
//            throw new RuntimeException("Lỗi SQL khi sao lưu vào device: " + e.getMessage(), e);
//        }
//    }
//
//    public List<BackupHistoryDto> handleGetListBackupHistory(String dbName) {
//        List<BackupHistoryDto> listResult = new ArrayList<>();
//        try (Connection conn = dataSource.getConnection();
//             CallableStatement cs = conn.prepareCall(QuerySpBackup.SP_GetBackupHistory)) {
//            cs.setString(1, dbName.trim());
//            try (ResultSet rs = cs.executeQuery()) {
//                while (rs.next()) {
//                    BackupHistoryDto result = new BackupHistoryDto();
//                    result.setId(rs.getInt("id"));
//                    result.setPosition(rs.getInt("position")); // <<-- ĐỌC DỮ LIỆU MỚI
//
//                    result.setDescription(rs.getString("description"));
//                    Timestamp timestamp = rs.getTimestamp("date");
//                    if (timestamp != null) {
//                        result.setDate(timestamp.toLocalDateTime());
//                    }
//                    result.setUser(rs.getString("user"));
//                    listResult.add(result);
//                }
//            }
//        } catch (SQLException e) {
//            log.error("--ER handleGetListBackupHistory for {}: {}", dbName, e.getMessage(), e);
//            throw new RuntimeException("Lỗi khi lấy lịch sử backup: " + e.getMessage());
//        }
//        return listResult;
//    }
//
//
////    public boolean handleRestoreDatabase(RestoreRequestDto restoreRequestDto, String username) {
////        log.info("User '{}' is attempting to restore DB '{}' to backup file number {}",
////                username, restoreRequestDto.getDbName(), restoreRequestDto.getBackupFileNumber());
////
////        String dbName = restoreRequestDto.getDbName();
////        // Lấy tên device từ một hằng số hoặc cấu hình để đảm bảo nhất quán
////        String backupDeviceName = "ThuVienBackupDevice";
////
////        // Tạo một kết nối hoàn toàn mới đến master DB
////        try (Connection masterConnection = DriverManager.getConnection(masterUrl, masterUsername, masterPassword)) {
////            // Chúng ta cần Statement vì các lệnh ALTER/RESTORE không phải là CallableStatement
////            try (Statement stmt = masterConnection.createStatement()) {
////
////                // 1. Chuyển DB về chế độ SINGLE_USER để ngắt mọi kết nối
////                log.info("Step 1: Setting database [{}] to SINGLE_USER mode...", dbName);
////                String sqlSetSingleUser = String.format("ALTER DATABASE [%s] SET SINGLE_USER WITH ROLLBACK IMMEDIATE", dbName);
////                stmt.execute(sqlSetSingleUser);
////                log.info("Database is now in SINGLE_USER mode.");
////
////                // 2. Thực thi lệnh RESTORE
////                log.info("Step 2: Restoring database [{}] from device [{}] with file number {}...", dbName, backupDeviceName, restoreRequestDto.getBackupFileNumber());
////                String sqlRestore = String.format("RESTORE DATABASE [%s] FROM [%s] WITH FILE = %d, REPLACE, RECOVERY",
////                        dbName, backupDeviceName, restoreRequestDto.getBackupFileNumber());
////                stmt.execute(sqlRestore);
////                log.info("RESTORE command executed successfully.");
////
////                // 3. Chuyển DB trở lại chế độ MULTI_USER
////                log.info("Step 3: Setting database [{}] back to MULTI_USER mode...", dbName);
////                String sqlSetMultiUser = String.format("ALTER DATABASE [%s] SET MULTI_USER", dbName);
////                stmt.execute(sqlSetMultiUser);
////                log.info("Database is back in MULTI_USER mode. Restore process completed.");
////
////                return true;
////            }
////        } catch (SQLException e) {
////            log.error("Error during direct SQL restore process: {}", e.getMessage(), e);
////            // Ném lỗi ra để GlobalExceptionHandler bắt và trả về cho Frontend
////            throw new SqlCustomException(e.getMessage());
////        }
////    }
//
//    public boolean handleRestoreDatabase(RestoreRequestDto restoreRequestDto, String username) {
//        String dbName = restoreRequestDto.getDbName();
//        log.info("User '{}' is attempting to restore DB '{}'", username, dbName);
//
//        // Luôn tạo kết nối mới đến master cho các tác vụ quản trị
//        try (Connection masterConnection = DriverManager.getConnection(masterUrl, masterUsername, masterPassword);
//             Statement stmt = masterConnection.createStatement()) {
//
//            // Chuyển DB về chế độ SINGLE_USER
//            String sqlSetSingleUser = String.format("ALTER DATABASE [%s] SET SINGLE_USER WITH ROLLBACK IMMEDIATE", dbName);
//            stmt.execute(sqlSetSingleUser);
//            log.info("Database [{}] set to SINGLE_USER mode.", dbName);
//
//            // Tên device dùng chung
//            String backupDeviceName = "ThuVienBackupDevice";
//
//            if (restoreRequestDto.isPitr() && restoreRequestDto.getRestoreDateTime() != null) {
//                // === LOGIC MỚI CHO POINT-IN-TIME RECOVERY ===
//                log.info("Starting Point-in-Time Recovery process...");
//
//                // 1. Restore bản FULL backup với NORECOVERY
//                int fullBackupPosition = restoreRequestDto.getBackupFileNumber();
//                String sqlRestoreFull = String.format("RESTORE DATABASE [%s] FROM [%s] WITH FILE = %d, NORECOVERY, REPLACE",
//                        dbName, backupDeviceName, fullBackupPosition);
//                stmt.execute(sqlRestoreFull);
//                log.info("Restored FULL backup at position {}.", fullBackupPosition);
//
//                // 2. Lấy danh sách các bản Transaction Log backup cần áp dụng
//                // Cần một kết nối đến msdb để đọc lịch sử
//                List<Integer> logPositions = getTransactionLogPositions(dbName, fullBackupPosition);
//
//                // 3. Restore từng bản log với NORECOVERY và STOPAT
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
//                String stopAtString = restoreRequestDto.getRestoreDateTime().format(formatter);
//
//                for (Integer logPosition : logPositions) {
//                    String sqlRestoreLog = String.format("RESTORE LOG [%s] FROM [%s] WITH FILE = %d, NORECOVERY, STOPAT = '%s'",
//                            dbName, backupDeviceName, logPosition, stopAtString);
//                    stmt.execute(sqlRestoreLog);
//                    log.info("Restored LOG backup at position {}.", logPosition);
//                }
//
//                // Không cần bước cuối cùng vì bản log cuối cùng đã có STOPAT
//
//            } else {
//                // Logic restore normal giữ nguyên
//                log.info("Executing normal restore to backup file number {}", restoreRequestDto.getBackupFileNumber());
//                String sqlRestore = String.format("RESTORE DATABASE [%s] FROM [%s] WITH FILE = %d, REPLACE, RECOVERY",
//                        dbName, backupDeviceName, restoreRequestDto.getBackupFileNumber());
//                stmt.execute(sqlRestore);
//            }
//
//            // Cuối cùng, đưa DB về lại MULTI_USER (chỉ cần cho restore normal,
//            // nhưng chạy lại cũng không sao, restore log với recovery đã làm việc này rồi)
//            String sqlSetMultiUser = String.format("ALTER DATABASE [%s] SET MULTI_USER", dbName);
//            stmt.execute(sqlSetMultiUser);
//            log.info("Database [{}] set back to MULTI_USER mode. Process finished.", dbName);
//
//            return true;
//
//        } catch (SQLException e) {
//            log.error("Error during restore process: {}", e.getMessage(), e);
//            // Cố gắng đưa DB về multi-user nếu có lỗi nghiêm trọng
//            try (Connection masterConn = DriverManager.getConnection(masterUrl, masterUsername, masterPassword);
//                 Statement recoveryStmt = masterConn.createStatement()) {
//                String sqlSetMultiUser = String.format("ALTER DATABASE [%s] SET MULTI_USER", dbName);
//                recoveryStmt.execute(sqlSetMultiUser);
//            } catch (SQLException ex) {
//                log.error("Could not set database back to multi-user mode after failure.", ex);
//            }
//            throw new SqlCustomException(e.getMessage());
//        }
//    }
//
//
//    /**
//     * Hàm helper để lấy danh sách position của các bản Transaction Log
//     * sau một bản FULL backup nhất định.
//     */
//    private List<Integer> getTransactionLogPositions(String dbName, int afterPosition) throws SQLException {
//        List<Integer> positions = new ArrayList<>();
//        // Dùng dataSource của ứng dụng để kết nối đến DB QUANLY_THUVIEN (để đọc msdb)
//        String sql = "SELECT position FROM msdb.dbo.backupset " +
//                "WHERE database_name = ? AND type = 'L' AND position > ? " +
//                "ORDER BY position ASC";
//        try (Connection conn = dataSource.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setString(1, dbName);
//            ps.setInt(2, afterPosition);
//            try (ResultSet rs = ps.executeQuery()) {
//                while (rs.next()) {
//                    positions.add(rs.getInt("position"));
//                }
//            }
//        }
//        return positions;
//    }
//
//}



package com.ThuVien.ThuVienAplication.service;

import com.ThuVien.ThuVienAplication.exception.SqlCustomException;
import com.ThuVien.ThuVienAplication.model.dto.request.backupRq.BackupRequestDto;
import com.ThuVien.ThuVienAplication.model.dto.request.backupRq.RestoreRequestDto;
import com.ThuVien.ThuVienAplication.model.dto.response.backupRp.BackupHistoryDto;
import com.ThuVien.ThuVienAplication.model.dto.response.backupRp.DatabaseDto;
import com.ThuVien.ThuVienAplication.model.dto.response.staffRp.CurrentUserDto;
import com.ThuVien.ThuVienAplication.storeprocedure.backup.QuerySpBackup;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j

@RequiredArgsConstructor
public class BackupService {

    private final DataSource dataSource;

    private final ConfigurableApplicationContext context;
    private final HikariDataSource hikariDataSource;


    @Value("${backup.storage.path}")
    private String backupStoragePath;
    @Value("${database.url}")
    private String DBThuVienUrl;
    @Value("${master.datasource.url}")
    private String masterUrl;
    @Value("${master.datasource.username}")
    private String masterUsername;
    @Value("${master.datasource.password}")
    private String masterPassword;




    public List<DatabaseDto> handleGetListDatabase() {
        List<DatabaseDto> listResult = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(QuerySpBackup.Sp_Danh_sach_cac_database);
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                listResult.add(new DatabaseDto(rs.getString("name")));
            }
        } catch (SQLException e) {
            log.error("--ER handleGetListDatabase: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi lấy danh sách database: " + e.getMessage());
        }
        return listResult;
    }

    public boolean handleBackupDatabase(BackupRequestDto backupRequestDto, String username ,String password) {
        log.info("User '{}' is requesting a backup for DB: {}", username, backupRequestDto.getDbName());
        try (Connection userConnection = DriverManager.getConnection(DBThuVienUrl, username, password)) {

            try (CallableStatement cs = userConnection.prepareCall("{call SP_Backup_database(?, ?)}")) {
                cs.setString(1, backupRequestDto.getDbName());
                cs.setBoolean(2, backupRequestDto.isOverwrite());
                cs.execute();
                return true;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi SQL khi sao lưu: " + e.getMessage(), e);
        }
    }

    public List<BackupHistoryDto> handleGetListBackupHistory(String dbName) {
        List<BackupHistoryDto> listResult = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(QuerySpBackup.SP_GetBackupHistory)) {
            cs.setString(1, dbName.trim());
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    BackupHistoryDto result = new BackupHistoryDto();
                    result.setId(rs.getInt("id"));
                    result.setPosition(rs.getInt("position")); // <<-- ĐỌC DỮ LIỆU MỚI
                    result.setBackupType(rs.getString("backup_type"));
                    result.setDescription(rs.getString("description"));
                    Timestamp timestamp = rs.getTimestamp("date");
                    if (timestamp != null) {
                        result.setDate(timestamp.toLocalDateTime());
                    }
                    result.setUser(rs.getString("user"));
                    listResult.add(result);
                }
            }
        } catch (SQLException e) {
            log.error("--ER handleGetListBackupHistory for {}: {}", dbName, e.getMessage(), e);
            throw new RuntimeException("Lỗi khi lấy lịch sử backup: " + e.getMessage());
        }
        return listResult;
    }




    public void backupTransactionLog(String dbName) {
        String sql = "{call SP_BackupTransactionLog(?)}";
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, dbName);
            cs.execute();
        } catch (SQLException e) {
            throw new SqlCustomException("Lỗi khi sao lưu Transaction Log: " + e.getMessage());
        }
    }


    public void restartApplication() {

        // Tạo một thread mới để gọi API restart, tránh deadlock
        Thread restartThread = new Thread(() -> {
            try {

                Thread.sleep(2000);



                log.info("Triggering application restart...");

            } catch (Exception e) {
                log.error("Could not trigger application restart.", e);
            }
        });

        restartThread.setDaemon(false);
        restartThread.start();
    }




    public boolean handleRestoreDatabase(RestoreRequestDto restoreRequestDto, String username) {
        String dbName = restoreRequestDto.getDbName();

        log.info("User '{}' initiated restore for DB '{}'. Preparing to close connection pool.", username, dbName);
        if (restoreRequestDto.isPitr() && restoreRequestDto.getRestoreDateTime() != null) {


            if (!hikariDataSource.isClosed()) {
                hikariDataSource.close();
                log.info("Connection pool [{}] has been closed.", hikariDataSource.getPoolName());
            }


            try (Connection masterConnection = DriverManager.getConnection(masterUrl, masterUsername, masterPassword);
                 Statement stmt = masterConnection.createStatement()) {

                log.info("Executing restore process via dedicated master connection...");

                if (restoreRequestDto.isPitr() && restoreRequestDto.getRestoreDateTime() != null) {

                    String fullBackupDevice = "ThuVienBackupDevice";
                    String logBackupDevice = "ThuVienLogDevice";


                    log.info("Starting advanced Point-in-Time Recovery process...");


                    try {
                        log.info("Step 0: Performing tail-log backup...");
                        stmt.execute(String.format("BACKUP LOG [%s] TO [%s] WITH NORECOVERY", dbName, logBackupDevice));
                        log.info("Tail-log backup successful.");
                    } catch (SQLException e) {

                        log.warn("Could not perform tail-log backup. This is normal if the database is in SIMPLE recovery model or damaged. Proceeding with restore... Error: {}", e.getMessage());
                    }

                    int fullBackupPosition = restoreRequestDto.getBackupFileNumber();
                    log.info("Step 1: Restoring FULL backup from position {}...", fullBackupPosition);
                    stmt.execute(String.format("RESTORE DATABASE [%s] FROM [%s] WITH FILE = %d, NORECOVERY, REPLACE",
                            dbName, fullBackupDevice, fullBackupPosition));
                    log.info("FULL backup restored.");


                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                    String stopAtString = restoreRequestDto.getRestoreDateTime().format(formatter);

                    log.info("Step 2: Restoring LOG backups to point-in-time: {}", stopAtString);
                    stmt.execute(String.format("RESTORE LOG [%s] FROM [%s] WITH STOPAT = '%s', RECOVERY",
                            dbName, logBackupDevice, stopAtString));
                    log.info("LOG backups restored. Database is now recovered and online.");

                    stmt.execute(String.format("ALTER DATABASE [%s] SET MULTI_USER", dbName));

                }
                log.info("Database restore process completed successfully on SQL Server.");

                restartApplication();

                return true;

            } catch (SQLException e) {
                log.error("Error during restore process after closing pool: {}", e.getMessage());

                throw new SqlCustomException(e.getMessage());
            }
        }else {
            try (Connection masterConnection = DriverManager.getConnection(masterUrl, masterUsername, masterPassword);
                 ) {

                            try (Statement stmt = masterConnection.createStatement()) {

                // 1. Chuyển DB về chế độ SINGLE_USER để ngắt mọi kết nối
                log.info("Step 1: Setting database [{}] to SINGLE_USER mode...", dbName);
                String sqlSetSingleUser = String.format("ALTER DATABASE [%s] SET SINGLE_USER WITH ROLLBACK IMMEDIATE", dbName);
                stmt.execute(sqlSetSingleUser);
                log.info("Database is now in SINGLE_USER mode.");

                // 2. Thực thi lệnh RESTORE
                log.info("Step 2: Restoring database [{}] from device [{}] with file number {}...", dbName, "ThuVienBackupDevice", restoreRequestDto.getBackupFileNumber());
                String sqlRestore = String.format("RESTORE DATABASE [%s] FROM [%s] WITH FILE = %d, REPLACE, RECOVERY",
                        dbName, "ThuVienBackupDevice", restoreRequestDto.getBackupFileNumber());
                stmt.execute(sqlRestore);
                log.info("RESTORE command executed successfully.");

                // 3. Chuyển DB trở lại chế độ MULTI_USER
                log.info("Step 3: Setting database [{}] back to MULTI_USER mode...", dbName);
                String sqlSetMultiUser = String.format("ALTER DATABASE [%s] SET MULTI_USER", dbName);
                stmt.execute(sqlSetMultiUser);
                log.info("Database is back in MULTI_USER mode. Restore process completed.");

                return true;
            }
        } catch (SQLException e) {
            log.error("Error during direct SQL restore process: {}", e.getMessage(), e);
            throw new SqlCustomException(e.getMessage());
        }
        }
    }




    private void executePointInTimeRecovery(Statement stmt, RestoreRequestDto dto, String dbName) throws SQLException {
        String fullBackupDevice = "ThuVienBackupDevice";
        String logBackupDevice = "ThuVienLogDevice";

        log.info("Restoring FULL backup from position {} with NORECOVERY...", dto.getBackupFileNumber());
        stmt.execute(String.format("RESTORE DATABASE [%s] FROM [%s] WITH FILE = %d, NORECOVERY, REPLACE",
                dbName, fullBackupDevice, dto.getBackupFileNumber()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String stopAtString = dto.getRestoreDateTime().format(formatter);
        log.info("Restoring LOGs from device [{}] with STOPAT = {}...", logBackupDevice, stopAtString);
        stmt.execute(String.format("RESTORE LOG [%s] FROM [%s] WITH STOPAT = '%s', RECOVERY",
                dbName, logBackupDevice, stopAtString));
    }



    private void executeNormalRecovery(Statement stmt, RestoreRequestDto dto, String dbName, String deviceName) throws SQLException {
        log.info("Executing normal restore to backup file number {}", dto.getBackupFileNumber());
        String sqlRestore = String.format("RESTORE DATABASE [%s] FROM [%s] WITH FILE = %d, REPLACE, RECOVERY",
                dbName, deviceName, dto.getBackupFileNumber());
        stmt.execute(sqlRestore);
    }

    private void executePointInTimeRecovery(Statement stmt, RestoreRequestDto dto, String dbName, String deviceName) throws SQLException {
        log.info("Executing Point-in-Time-Recovery to {}", dto.getRestoreDateTime());

        // a. Restore FULL với NORECOVERY
        int fullBackupPosition = dto.getBackupFileNumber();
        String sqlRestoreFull = String.format("RESTORE DATABASE [%s] FROM [%s] WITH FILE = %d, NORECOVERY, REPLACE",
                dbName, deviceName, fullBackupPosition);
        stmt.execute(sqlRestoreFull);
        log.info("Restored FULL backup at position {}.", fullBackupPosition);

        // b. Lấy và restore các bản Transaction Log
        List<Integer> logPositions = getTransactionLogPositions(dbName, fullBackupPosition);
        String logDeviceName = "ThuVienLogDevice"; // Device riêng cho Log
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String stopAtString = dto.getRestoreDateTime().format(formatter);

        for (int logPosition : logPositions) {
            String sqlRestoreLog = String.format("RESTORE LOG [%s] FROM [%s] WITH FILE = %d, NORECOVERY, STOPAT = '%s'",
                    dbName, logDeviceName, logPosition, stopAtString);
            stmt.execute(sqlRestoreLog);
            log.info("Applied LOG backup at position {}.", logPosition);
        }

        // c. Hoàn tất quá trình phục hồi
        stmt.execute(String.format("RESTORE DATABASE [%s] WITH RECOVERY", dbName));
    }

    private List<Integer> getTransactionLogPositions(String dbName, int afterPosition) throws SQLException {
        List<Integer> positions = new ArrayList<>();
        String sql = "SELECT position FROM msdb.dbo.backupset WHERE database_name = ? AND type = 'L' AND position > ? ORDER BY position ASC";
        try (Connection conn = dataSource.getConnection(); // Dùng dataSource vì nó vẫn còn trong scope này
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dbName);
            ps.setInt(2, afterPosition);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    positions.add(rs.getInt("position"));
                }
            }
        }
        return positions;
    }

    private void refreshDataSourceBean() {
        log.info("Refreshing DataSource bean...");
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();

        if (beanFactory.containsSingleton("dataSource")) {
            beanFactory.destroySingletons();
            log.info("Destroyed existing dataSource singleton bean.");
        }

        // getBean sẽ kích hoạt Spring tạo lại một instance mới
        context.getBean("dataSource", HikariDataSource.class);
        log.info("New DataSource bean has been created and initialized.");
    }

}