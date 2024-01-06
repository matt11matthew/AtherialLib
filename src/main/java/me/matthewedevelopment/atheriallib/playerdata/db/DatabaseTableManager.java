package me.matthewedevelopment.atheriallib.playerdata.db;

import me.matthewedevelopment.atheriallib.playerdata.ProfileColumn;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public interface DatabaseTableManager {
 void createOrUpdateTable(Connection connection, String tableName, List<ProfileColumn> columns) throws SQLException;
    boolean tableExists(Connection connection, String tableName) throws SQLException;
    void modifyExistingTable(Statement statement, String tableName, List<ProfileColumn> columns) throws SQLException;

}
