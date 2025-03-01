package com.payments;

import com.payments.util.MysqlUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MysqlCleaner implements DatabaseCleaner {
    public void clean(String table) {
        try {
            final String sql = "DELETE FROM " + table;
            final Connection conn = MysqlUtil.conn();
            final PreparedStatement statement = conn.prepareStatement(sql);

            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
