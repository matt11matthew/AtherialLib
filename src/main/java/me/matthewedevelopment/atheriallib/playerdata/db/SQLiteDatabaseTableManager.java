package me.matthewedevelopment.atheriallib.playerdata.db;

import me.matthewedevelopment.atheriallib.playerdata.ProfileColumn;
import me.matthewedevelopment.atheriallib.playerdata.ProfileColumnType;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SQLiteDatabaseTableManager implements DatabaseTableManager {

    @Override
    public void createOrUpdateTable(Connection connection, String tableName, List<ProfileColumn> columns) throws SQLException {
        List<ProfileColumn> newCols = new ArrayList<>();

        newCols.add(new ProfileColumn("uuid", ProfileColumnType.VARCHAR, ""));
        for (ProfileColumn column : columns) {
            if (column.getName().equalsIgnoreCase("uuid")){

                continue;
            }
            newCols.add(column);
        }

        try (Statement statement = connection.createStatement()) {
            // Check if the table already exists
            boolean tableExists = tableExists(connection, tableName);

            if (tableExists) {
                // Modify existing table (e.g., add new columns)
                modifyExistingTable(statement, tableName, newCols);
            } else {
                // Create a new table
                createNewTable(statement, tableName, newCols);
            }
        }
    }

    @Override
    public boolean tableExists(Connection connection, String tableName) throws SQLException {
        // Use SQLite-specific SQL syntax to check if the table exists
        String query = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "'";
        java.sql.ResultSet resultSet = connection.createStatement().executeQuery(query);
        return resultSet.next();
    }

    @Override
    public void modifyExistingTable(Statement statement, String tableName, List<ProfileColumn> columns) throws SQLException {
//        columns.add(new ProfileColumn("uuid", "VARCHAR", ""));
        for (ProfileColumn column : columns) {
            // Check if the column already exists in the table
            if (!columnExists(statement, tableName, column.getName())) {
                // If it doesn't exist, add the column to the table
                addColumnToTable(statement, tableName, column);
            }
        }
    }

    private static void createNewTable(Statement statement, String tableName, List<ProfileColumn> columns) throws SQLException {
//        columns.add(new ProfileColumn("uuid", "VARCHAR", ""));
        StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        query.append(tableName).append(" (");

        for (ProfileColumn column : columns) {
            query.append(column.getName()).append(" ").append(column.getType()).append(", ");
        }

        query.setLength(query.length() - 2); // Remove the trailing comma and space
        query.append(")");

        statement.execute(query.toString());
    }

    private static boolean columnExists(Statement statement, String tableName, String columnName) throws SQLException {
        // Use SQLite-specific SQL syntax to check if the column exists

        String query = "PRAGMA table_info(" + tableName + ")";
        java.sql.ResultSet resultSet = statement.executeQuery(query);

        while (resultSet.next()) {
            String existingColumnName = resultSet.getString("name");
            if (existingColumnName.equals(columnName)) {
                return true;
            }
        }

        return false;
    }

    private static void addColumnToTable(Statement statement, String tableName, ProfileColumn column) throws SQLException {
        // Use SQLite-specific SQL syntax to add a new column to the table
        String query = "ALTER TABLE " + tableName + " ADD COLUMN " + column.getName() + " " + column.getType();
        statement.execute(query);
    }
}
