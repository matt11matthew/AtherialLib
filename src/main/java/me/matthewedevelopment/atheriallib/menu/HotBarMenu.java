package me.matthewedevelopment.atheriallib.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class HotBarMenu {
    private Map<Integer, ItemStack> itemMap;
    private Map<Integer, OnHotBarAction> actionMap;
    private HashSet<Integer> hiddenList = new HashSet<>();
    private Player player;


    public static boolean hasHotBar(Player player){
        return MENUS.containsKey(player.getUniqueId());
    }
    public static Map<UUID, HotBarMenu> MENUS = new HashMap<>();

    public Map<Integer, OnHotBarAction> getActionMap() {
        return actionMap;
    }
    public HotBarMenu set(int slot, ItemStack itemStack, OnHotBarAction onHotBarAction) {
        itemMap.put(slot,itemStack);
        actionMap.put(slot, onHotBarAction);
        return this;
    }

    public void show(Integer slot) {
        if (hiddenList.contains(slot)){
            hiddenList.remove(slot);
        }
    }
    public void hide(Integer slot) {
        if (hiddenList.contains(slot))return;
        hiddenList.add(slot);

    }
    public void remove(int slot) {
        if (itemMap.containsKey(slot)) {
            itemMap.remove(slot);
        }
        if (actionMap.containsKey(slot)) {
            actionMap.remove(slot);
        }
    }

    public boolean  isHidden(Integer slot) {
        return hiddenList.contains(slot);
    }
    public void show() {
        update();
    }

    public void update(){
        if (player!=null&&player.isOnline()){
            itemMap.forEach((integer, itemStack) -> {
                if (hiddenList.contains(integer)) {

                    player.getInventory().setItem(integer, new ItemStack(Material.AIR));
                } else {
                    player.getInventory().setItem(integer, itemStack);

                }
//                player.updateInventory();
            });
        }
    }

    public Map<Integer, ItemStack> getItemMap() {
        return itemMap;
    }

    public static HotBarMenu get(Player player) {
        return MENUS.getOrDefault(player.getUniqueId(), null);
    }

    public static void destroy(Player player) {
        if (MENUS.containsKey(player.getUniqueId())){
            HotBarMenu hotBarMenu = MENUS.get(player.getUniqueId());
            for (Integer i : hotBarMenu.itemMap.keySet()) {
                player.getInventory().setItem(i, new ItemStack(Material.AIR));
            }
            MENUS.remove(player.getUniqueId());
        }
    }

    public HotBarMenu(Player player) {
        this.itemMap = new HashMap<>();
        this.actionMap = new HashMap<>();
        this.player = player;
    }


    public static HotBarMenu create(Player player) {
        if (MENUS.containsKey(player.getUniqueId())){
            MENUS.remove(player.getUniqueId());
        }
        MENUS.put(player.getUniqueId(),new HotBarMenu(player));
        return MENUS.get(player.getUniqueId());
    }

}
