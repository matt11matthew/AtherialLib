package me.matthewedevelopment.atheriallib.database.registry.db;

import me.matthewedevelopment.atheriallib.database.registry.DataColumn;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public interface DatabaseTableManagerCustomData {
 void createOrUpdateTable(Connection connection, String tableName, List<DataColumn> columns) throws SQLException;
    boolean tableExists(Connection connection, String tableName) throws SQLException;
    void modifyExistingTable(Statement statement, String tableName, List<DataColumn> columns) throws SQLException;

}
