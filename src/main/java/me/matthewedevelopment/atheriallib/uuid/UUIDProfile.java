package me.matthewedevelopment.atheriallib.uuid;

import me.matthewedevelopment.atheriallib.database.registry.DataColumn;
import me.matthewedevelopment.atheriallib.database.registry.DataColumnType;
import me.matthewedevelopment.atheriallib.database.registry.DataObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class UUIDProfile  extends DataObject<UUIDProfile> {
    private String username;
    public UUIDProfile(UUID uuid, String username) {
        super(uuid);
        this.username = username;
    }
    public UUIDProfile(UUID uuid) {
        super(uuid);
    }

    public String getUsername() {
        return username;
    }

    public UUIDProfile() {
    }

    @Override
    public String getTableName() {
        return "uuid_cache";
    }

    @Override
    public List<DataColumn> getDefaultColumns() {
        return Collections.singletonList(new DataColumn("username", DataColumnType.VARCHAR,username));
    }

    @Override
    public UUIDProfile loadResultFromSet(ResultSet resultSet) {
        try {
            this.username=resultSet.getString("username");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }
}
