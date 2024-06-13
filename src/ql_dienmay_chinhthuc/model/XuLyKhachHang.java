/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ql_dienmay_chinhthuc.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import ql_dienmay_chinhthuc.KetNoi;

/**
 *
 * @author trant
 */
public class XuLyKhachHang {

    public static String getNextCustomerId() throws SQLException {
        Connection connection = KetNoi.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT MAX(ma_kh) FROM db_khach_hang");

        String maxId = "";
        if (resultSet.next()) {
            maxId = resultSet.getString(1);
        }

        connection.close();

        if (maxId == null || maxId.isEmpty()) {
            return "KH00000001";
        } else {
            int num = Integer.parseInt(maxId.substring(2)) + 1;
            return String.format("KH%08d", num);
        }
    }

    public static void registerCustomer(String fullName, String password, String username) throws SQLException {
        Connection connection = KetNoi.getConnection();
        String nextCustomerId = getNextCustomerId();

        String sql = "INSERT INTO db_khach_hang (ma_kh, ten_kh, matkhau, sdt) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, nextCustomerId);
        preparedStatement.setString(2, fullName);
        preparedStatement.setString(3, password);
        preparedStatement.setString(4, username);

        preparedStatement.executeUpdate();
        connection.close();
    }
}
