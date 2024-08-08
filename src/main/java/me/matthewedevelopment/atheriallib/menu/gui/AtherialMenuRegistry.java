package me.matthewedevelopment.atheriallib.menu.gui;

import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.menu.gui.events.AtherialMenuCloseEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import spigui.menu.SGMenu;

import java.util.*;

/**
 * Created by Matthew E on 1/11/2024 at 3:01 PM for the project MazeServer-Clans
 */
@Deprecated
public class AtherialMenuRegistry {
    private  Map<UUID, AtherialMenu> menuHashMap = new HashMap<>();

    public AtherialMenuRegistry() {

        this.menuHashMap =new HashMap<>();
    }

    public  void start() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(AtherialLib.getInstance(),() -> {
            updateGUI(true);
        },40L,40L);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(AtherialLib.getInstance(),() -> {
            updateGUI(false);
        },15L,15L);
    }

    public Map<UUID, AtherialMenu> getMenuMap() {
        return menuHashMap;
    }


    private void updateGUI(boolean slow) {
        List<UUID> toRemove = new ArrayList<>();

        for (UUID uuid : menuHashMap.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player==null||!player.isOnline()){
                toRemove.add(uuid);
                continue;
            }
            if (player.getOpenInventory()!=null && player.getOpenInventory().getTopInventory().getHolder() instanceof SGMenu){


                AtherialMenu atherialMenu = menuHashMap.get(player.getUniqueId());
                if (atherialMenu.isSlow()){

                    if (slow) {
                        atherialMenu.firstUpdate();
                    }
                } else {
                    if (!slow) {
                        atherialMenu.firstUpdate();

                    }
                }

            } else {

                toRemove.add(uuid);
            }

        }

        toRemove.stream().filter(uuid -> menuHashMap.containsKey(uuid)).forEach(uuid -> {
            AtherialMenuCloseEvent atherialMenuCloseEvent  = new AtherialMenuCloseEvent(menuHashMap.get(uuid));
            Bukkit.getPluginManager().callEvent(atherialMenuCloseEvent);
            menuHashMap.get(uuid).onRealClose();
            menuHashMap.remove(uuid);
        });

    }

    public void destroy(Player player) {
        if (menuHashMap.containsKey(player.getUniqueId())){
            menuHashMap.get(player.getUniqueId()).onRealClose();
            menuHashMap.remove(player.getUniqueId());
        }
    }
}

