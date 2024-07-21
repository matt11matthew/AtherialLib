package me.matthewedevelopment.atheriallib.database.registry.db;

import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.database.registry.DataColumn;
import me.matthewedevelopment.atheriallib.database.registry.DataColumnType;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SQLiteDatabaseTableManagerCustom implements DatabaseTableManagerCustomData {

    @Override
    public void createOrUpdateTable(Connection connection, String tableName, List<DataColumn> columns) throws SQLException {
        List<DataColumn> newCols = new ArrayList<>();
        boolean hasUUID = false;
        newCols.add(new DataColumn("uuid", DataColumnType.VARCHAR, ""));
        for (DataColumn column : columns) {
            if (column.getName().equalsIgnoreCase("uuid")){

                continue;
            }
            newCols.add(column);
        }


        try (Statement statement = connection.createStatement()) {
            // Check if the table already exists
            boolean tableExists = tableExists(connection, tableName);

            if (tableExists) {
                if (AtherialLib.getInstance().isDebug()){
                    AtherialLib.getInstance().getLogger().info("TABLE NOT EXISTS?");
                }
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
    public void modifyExistingTable(Statement statement, String tableName, List<DataColumn> columns) throws SQLException {
//        columns.add(new ProfileColumn("uuid", "VARCHAR", ""));
        for (DataColumn column : columns) {
            // Check if the column already exists in the table
            if (!columnExists(statement, tableName, column.getName())) {
                // If it doesn't exist, add the column to the table
                addColumnToTable(statement, tableName, column);
            }
        }
    }

    private static void createNewTable(Statement statement, String tableName, List<DataColumn> columns) throws SQLException {
//        columns.add(new ProfileColumn("uuid", "VARCHAR", ""));
        StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        query.append(tableName).append(" (");

        for (DataColumn column : columns) {
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

    private static void addColumnToTable(Statement statement, String tableName, DataColumn column) throws SQLException {
        // Use SQLite-specific SQL syntax to add a new column to the table
        String query = "ALTER TABLE " + tableName + " ADD COLUMN " + column.getName() + " " + column.getType().toString();
        statement.execute(query);
    }
}

