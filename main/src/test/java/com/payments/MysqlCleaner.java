package com.payments;

import com.payments.util.MysqlUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MysqlCleaner implements DatabaseCleaner {
    public void clean(String table) {
        try(final Connection conn = MysqlUtil.conn()) {
            final String sql = "DELETE FROM " + table;
            final PreparedStatement statement = conn.prepareStatement(sql);

            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
