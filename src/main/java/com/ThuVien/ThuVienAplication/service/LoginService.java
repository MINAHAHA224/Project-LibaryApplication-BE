package com.ThuVien.ThuVienAplication.service;

import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service
public class LoginService {

    public String checkLogin(String username, String password) {
        String url = "jdbc:sqlserver://DESKTOP-D4BGI46:1433;databaseName=QUANLY_THUVIEN;encrypt=true;trustServerCertificate=true";
        String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

        try {

            Class.forName(driver);


            Connection conn = DriverManager.getConnection(url, username, password);



            conn.close();
            return "";
        } catch (SQLException e) {
            return "Đăng nhập thất bại: " + e.getMessage();
        } catch (ClassNotFoundException e) {
            return "Không tìm thấy driver JDBC";
        }
    }
}
