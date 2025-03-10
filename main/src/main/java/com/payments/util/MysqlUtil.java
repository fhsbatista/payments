package com.payments.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlUtil {
    private static final String URL = "jdbc:mysql://db:3306/test";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static Connection conn() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
