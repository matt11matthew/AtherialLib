package me.matthewedevelopment.atheriallib.database.registry.db;

import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.database.registry.DataColumn;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class MySQLDatabaseTableManagerCustom implements DatabaseTableManagerCustomData {

    public  void createOrUpdateTable(Connection connection, String tableName, List<DataColumn> columns) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            // Check if the table already exists
            boolean tableExists = tableExists(connection, tableName);

            if (tableExists) {
                // Modify existing table (e.g., add new columns)
                modifyExistingTable(statement, tableName, columns);
            } else {
                // Create a new table
                createNewTable(statement, tableName, columns);
            }
        }
    }

    public   boolean tableExists(Connection connection, String tableName) throws SQLException {
        // Use MySQL-specific SQL syntax to check if the table exists
        String query = "SHOW TABLES LIKE '" + tableName + "'";
        java.sql.ResultSet resultSet = connection.createStatement().executeQuery(query);
        boolean next = resultSet.next();
        if (AtherialLib.getInstance().isDebug()){
            if (next){

                AtherialLib.getInstance().getLogger().info("TABLE " + tableName + " EXISTS");
            } else {
                AtherialLib.getInstance().getLogger().info("TABLE " + tableName + " DOESNT EXISTS");

            }
        }
        return next;
    }

    public   void createNewTable(Statement statement, String tableName, List<DataColumn> columns) throws SQLException {
        StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        query.append(tableName).append(" (");

        for (DataColumn column : columns) {
            query.append(column.getName()).append(" ").append(column.getTypeToString()).append(", ");
        }

        query.setLength(query.length() - 2); // Remove the trailing comma and space
        query.append(");");
        if (AtherialLib.getInstance().isDebug()){

            AtherialLib.getInstance().getLogger().info(query.toString());
        }

        statement.execute(query.toString());
    }

    public   void modifyExistingTable(Statement statement, String tableName, List<DataColumn> columns) throws SQLException {
        for (DataColumn column : columns) {
            // Check if the column already exists in the table
            if (!columnExists(statement, tableName, column.getName())) {
                // If it doesn't exist, add the column to the table
                addColumnToTable(statement, tableName, column);
            }
        }
    }

    public boolean columnExists(Statement statement, String tableName, String columnName) throws SQLException {
        // Use MySQL-specific SQL syntax to check if the column exists
        String query = "SHOW COLUMNS FROM " + tableName + " LIKE '" + columnName + "'";
        if (AtherialLib.getInstance().isDebug()){

            AtherialLib.getInstance().getLogger().info(query);
        }
        java.sql.ResultSet resultSet = statement.executeQuery(query);
        return resultSet.next();
    }


    public void addColumnToTable(Statement statement, String tableName, DataColumn column) throws SQLException {
        // Use MySQL-specific SQL syntax to add a new column to the table with a default value
        String query = "ALTER TABLE " + tableName + " ADD COLUMN " + column.getName() + " " + column.getTypeToString();

        // Check if the column has a default value and add it to the query
        if (column.getValue() != null) {
            query += " DEFAULT " + column.getValue();
        }

        if (AtherialLib.getInstance().isDebug()) {
            AtherialLib.getInstance().getLogger().info(query);
        }

        statement.execute(query);
    }
}
