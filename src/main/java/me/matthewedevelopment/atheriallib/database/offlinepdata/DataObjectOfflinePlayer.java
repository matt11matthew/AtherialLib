package me.matthewedevelopment.atheriallib.database.offlinepdata;

import me.matthewedevelopment.atheriallib.database.registry.DataColumn;
import me.matthewedevelopment.atheriallib.database.registry.DataColumnType;
import me.matthewedevelopment.atheriallib.database.registry.DataObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class DataObjectOfflinePlayer<T extends DataObjectOfflinePlayer<T>> extends DataObject<T> {





    private String username;

    public DataObjectOfflinePlayer(UUID uuid, String username) {
        super(uuid);
        this.username = username;
    }

    public DataObjectOfflinePlayer(UUID uuid) {
        super(uuid);
    }

    public DataObjectOfflinePlayer() {
    }

    public DataObjectOfflinePlayer(String username) {
        this.username = username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void loadOptionalFromRS(ResultSet resultSet) throws SQLException {

        this.username = resultSet.getString("username");
    }

    @Override
    public List<DataColumn> getOptionalColumns() {
        List<DataColumn> columns = new ArrayList<>();
        columns.add(new DataColumn("username", DataColumnType.VARCHAR, username ));
        return columns;
    }

    public String getUsername() {
        return username;
    }
}
