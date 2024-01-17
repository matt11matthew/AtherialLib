package me.matthewedevelopment.atheriallib.menu.gui;

import me.matthewedevelopment.atheriallib.AtherialLib;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import spigui.menu.SGMenu;

import java.util.*;

/**
 * Created by Matthew E on 1/11/2024 at 3:01 PM for the project MazeServer-Clans
 */
public class AtherialMenuRegistry {
    private  Map<UUID, AtherialMenu> menuHashMap = new HashMap<>();

    public AtherialMenuRegistry() {

        this.menuHashMap =new HashMap<>();
    }

    public  void start() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(AtherialLib.getInstance(),() -> updateGUI(),20L,20L);
    }

    public Map<UUID, AtherialMenu> getMenuMap() {
        return menuHashMap;
    }


    private void updateGUI() {
        List<UUID> toRemove = new ArrayList<>();

        for (UUID uuid : menuHashMap.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player==null||!player.isOnline()){
                toRemove.add(uuid);
                continue;
            }
            if (player.getOpenInventory()!=null && player.getOpenInventory().getTopInventory().getHolder() instanceof SGMenu){


                menuHashMap.get(player.getUniqueId()).firstUpdate();

            } else {
                toRemove.add(uuid);
            }

        }

        toRemove.stream().filter(uuid -> menuHashMap.containsKey(uuid)).forEach(uuid -> menuHashMap.remove(uuid));

    }

    public void destroy(Player player) {
        if (menuHashMap.containsKey(player.getUniqueId())){
            menuHashMap.remove(player.getUniqueId());
        }
    }
}

