package me.matthewedevelopment.atheriallib.minigame;

import me.matthewedevelopment.atheriallib.database.registry.DataColumn;

import java.sql.ResultSet;
import java.util.List;

public abstract class GameMapData {
    protected GameMap gameMap;

    public GameMapData(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public GameMap getMap() {
        return gameMap;

    }

    public abstract List<DataColumn> getColumns();

    public abstract void load(ResultSet rs);
}
