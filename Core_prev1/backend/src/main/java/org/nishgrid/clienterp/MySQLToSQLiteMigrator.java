package org.nishgrid.clienterp;
import java.sql.*;
import java.util.*;

public class MySQLToSQLiteMigrator {
    public static void main(String[] args) throws Exception {
        String mysqlUrl = "jdbc:mysql://:3306/nishgrid?useSSL=false&serverTimezone=UTC";
        String mysqlUser = "root";
        String mysqlPassword = "YourNewPassword";

        String sqliteUrl = "jdbc:sqlite:app.db";

        try (
                Connection mysqlConn = DriverManager.getConnection(mysqlUrl, mysqlUser, mysqlPassword);
                Connection sqliteConn = DriverManager.getConnection(sqliteUrl);
        ) {
            DatabaseMetaData meta = mysqlConn.getMetaData();
            ResultSet tables = meta.getTables(null, null, "%", new String[]{"TABLE"});

            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("Migrating table: " + tableName);

                // Get columns
                ResultSet cols = meta.getColumns(null, null, tableName, "%");
                List<String> colNames = new ArrayList<>();
                List<String> colTypes = new ArrayList<>();
                while (cols.next()) {
                    colNames.add(cols.getString("COLUMN_NAME"));
                    // Map MySQL types to SQLite types
                    String type = cols.getString("TYPE_NAME").toUpperCase();
                    switch (type) {
                        case "INT":
                        case "BIGINT":
                            colTypes.add("INTEGER");
                            break;
                        case "VARCHAR":
                        case "TEXT":
                        case "CHAR":
                        case "LONGTEXT":
                            colTypes.add("TEXT");
                            break;
                        case "BLOB":
                        case "LONGBLOB":
                            colTypes.add("BLOB");
                            break;
                        default:
                            colTypes.add("TEXT");
                    }
                }

                // Drop table if exists
                try (Statement st = sqliteConn.createStatement()) {
                    st.executeUpdate("DROP TABLE IF EXISTS \"" + tableName + "\"");
                }

                // Create table
                StringBuilder createSQL = new StringBuilder("CREATE TABLE \"" + tableName + "\" (");
                for (int i = 0; i < colNames.size(); i++) {
                    createSQL.append("\"").append(colNames.get(i)).append("\" ").append(colTypes.get(i));
                    if (i < colNames.size() - 1) createSQL.append(", ");
                }
                createSQL.append(")");
                try (Statement st = sqliteConn.createStatement()) {
                    st.executeUpdate(createSQL.toString());
                }

                // Copy data
                try (
                        Statement mysqlSt = mysqlConn.createStatement();
                        ResultSet rs = mysqlSt.executeQuery("SELECT * FROM " + tableName)
                ) {
                    String placeholders = String.join(",", Collections.nCopies(colNames.size(), "?"));
                    String insertSQL = "INSERT INTO \"" + tableName + "\" VALUES (" + placeholders + ")";
                    PreparedStatement sqlitePs = sqliteConn.prepareStatement(insertSQL);

                    while (rs.next()) {
                        for (int i = 0; i < colNames.size(); i++) {
                            Object val = rs.getObject(colNames.get(i));
                            sqlitePs.setObject(i + 1, val);
                        }
                        sqlitePs.executeUpdate();
                    }
                }
            }
        }
        System.out.println("Migration complete! app.db now contains all MySQL data.");
    }
}
