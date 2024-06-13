/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ql_dienmay_chinhthuc;

/**
 *
 * @author trant
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class KetNoi {

    // Thay đổi các thông tin sau để phù hợp với cấu hình của bạn
    private static final String url = "jdbc:sqlserver://ADONIS:1433;databaseName=QL_Dienmay_ChinhThuc";
    private static final String user = "sa";
    private static final String password = "123";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Đăng ký JDBC driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            // Kết nối đến cơ sở dữ liệu
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối đến cơ sở dữ liệu SQL Server!");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Không thể tìm thấy JDBC Driver!");
            e.printStackTrace();
        }
        return connection;
    }
}
