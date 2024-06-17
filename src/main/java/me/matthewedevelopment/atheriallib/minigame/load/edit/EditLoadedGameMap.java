package me.matthewedevelopment.atheriallib.minigame.load.edit;

import me.matthewedevelopment.atheriallib.menu.HotBarMenu;
import me.matthewedevelopment.atheriallib.message.message.ActionBarMessage;
import me.matthewedevelopment.atheriallib.minigame.GameMapConfig;
import me.matthewedevelopment.atheriallib.minigame.GameMapRegistry;
import me.matthewedevelopment.atheriallib.minigame.load.GameMapMode;
import me.matthewedevelopment.atheriallib.minigame.load.LoadedGameMap;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

import static me.matthewedevelopment.atheriallib.utilities.ChatUtils.colorize;

public  abstract class EditLoadedGameMap<T extends LoadedGameMap<T>> extends LoadedGameMap<T> {

    public EditLoadedGameMap(UUID dungeon, UUID sessionId, Class<T> clazz) {
        super(dungeon, sessionId, GameMapMode.EDIT, clazz);
    }



    public static Optional<LoadedGameMap> getCurrentEditMap(Player player) {
       return GameMapRegistry.get().getLoadedDungeonMap().values()
                .stream()
                .filter(loadedDungeon -> loadedDungeon.getGameMapMode() == GameMapMode.EDIT)
                .filter(loadedDungeon -> loadedDungeon.getPlayers().contains(player)).findFirst();
    }

    @Override
    public void onWorldLoad(World world) {

    }

    @Override
    public void onSessionEnd(Player player) {
        if (  HotBarMenu.hasHotBar(player)) {
            HotBarMenu.destroy(player);
        }
    }

    @Override
    public void update() {
        GameMapConfig config = GameMapConfig.get();
        for (Player player : getPlayers()) {

            new ActionBarMessage(colorize(config.EDITING_ACTIONBAR).replace("%name%", getGameMap().getName()), 1).send(player);

        }
    }

    @Override
    public void fastUpdate() {


    }


    @Override
    public void onLoad() {
    }
}
