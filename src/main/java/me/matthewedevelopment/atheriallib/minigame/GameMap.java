package me.matthewedevelopment.atheriallib.minigame;

import me.matthewedevelopment.atheriallib.database.registry.DataColumn;
import me.matthewedevelopment.atheriallib.database.registry.DataColumnType;
import me.matthewedevelopment.atheriallib.database.registry.DataObject;
import me.matthewedevelopment.atheriallib.utilities.location.AtherialLocation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * Created by Matthew E on 6/16/2024 at 2:00 PM for the project AtherialLib
 */
public class GameMap extends DataObject<GameMap> {
    private String name;
    private GameMapData gameMapData;

    private String zipFileName;

    private boolean editing;

    private UUID editSessionId;

    public AtherialLocation getLobbySpawn() {
        return lobbySpawn;
    }

//    public Class getGameClass() {
//        return gameClass;
//    }
//
//    public Class getEditClass() {
//        return editClass;
//    }

    private AtherialLocation lobbySpawn;
//    private Class gameClass;
//    private Class editClass;

    public GameMap(UUID uuid, String name) {
        super(uuid);
        this.name = name;
        this.lobbySpawn = null;
//        this.editClass=editClass;
//        this.gameClass = gameClass;

        this.zipFileName="TBD";



        try {
            gameMapData= (GameMapData) GameMapHandler.get().getGameDataClass().getConstructor(GameMap.class).newInstance(this);
        } catch (Exception e) {
            e.printStackTrace();;

        }
    }

    public GameMapData getGameMapData() {
        return gameMapData;
    }

    public boolean hasSpawn() {
        return lobbySpawn!=null;
    }

    public void setLobbySpawn(AtherialLocation lobbySpawn) {
        this.lobbySpawn = lobbySpawn;
    }



    public GameMap() {
        try {
            gameMapData= (GameMapData) GameMapHandler.get().getGameDataClass().getConstructor(GameMap.class).newInstance(this);
        } catch (Exception e) {
            e.printStackTrace();;

        }
    }

    public void save() {
        GameMapRegistry.get().updateAsync(this, () -> {});
    }
    public UUID getUUID() {
        return getUuid();
    }

    public String getZipFileName() {
        return zipFileName;
    }

    public void setZipFileName(String zipFileName) {
        this.zipFileName = zipFileName;
    }

    @Override
    public String getTableName() {
        return "games";
    }

    @Override
    public List<DataColumn> getDefaultColumns() {
        List<DataColumn> columns =new ArrayList<>();
        columns.addAll(Arrays.asList(
                new DataColumn("name", DataColumnType.VARCHAR,name),
                new DataColumn("zipFileName", DataColumnType.VARCHAR,zipFileName),
                new DataColumn("spawn", DataColumnType.VARCHAR, lobbySpawn)));

        columns.addAll(gameMapData.getColumns());
        return columns;
    }

    public String getName() {
        return name;
    }

    @Override
    public GameMap loadResultFromSet(ResultSet resultSet) {
        try {
            this.name = resultSet.getString("name");
            this.zipFileName = resultSet.getString("zipFileName");
            this.lobbySpawn = AtherialLocation.fromString(resultSet.getString("spawn"));

            gameMapData.load(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }



    public boolean isEditing() {
        return editing;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
    }

    public UUID getEditSessionId() {
        return editSessionId;
    }

    public void setEditSessionId(UUID editSessionId) {
        this.editSessionId = editSessionId;
    }
}
