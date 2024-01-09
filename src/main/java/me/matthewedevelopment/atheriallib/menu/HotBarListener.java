package me.matthewedevelopment.atheriallib.menu;

import me.matthewedevelopment.atheriallib.AtherialLib;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HotBarListener  implements Listener {
    private Map<UUID, Integer> slotMap = new HashMap<>();
    private AtherialLib atherialLib;

    public HotBarListener(AtherialLib atherialLib) {
        this.atherialLib = atherialLib;
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerItemHeldEvent event) {
        slotMap.put(event.getPlayer().getUniqueId(),event.getNewSlot());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory()!=null&&event.getClickedInventory().getType()== InventoryType.PLAYER){
            if (!HotBarMenu.MENUS.containsKey(event.getWhoClicked().getUniqueId())){
                return;
            }
            HotBarMenu hotBarMenu = HotBarMenu.get((Player) event.getWhoClicked());
            if (hotBarMenu.getItemMap().containsKey(event.getSlot())){
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        HotBarClickType clickType = null;
        if (!HotBarMenu.MENUS.containsKey(event.getPlayer().getUniqueId())){
            return;
        }
        if (event.getAction().toString().contains("RIGHT")){
            clickType = HotBarClickType.RIGHT;
        } else if (event.getAction().toString().contains("LEFT")) {
            clickType=HotBarClickType.LEFT;
        }
        if (clickType==null)return;
        HotBarMenu hotBarMenu = HotBarMenu.get(event.getPlayer());
        if (hotBarMenu.getItemMap().containsKey(slotMap.getOrDefault(event.getPlayer().getUniqueId(), -1))) {
            OnHotBarAction orDefault = hotBarMenu.getActionMap().getOrDefault(slotMap.getOrDefault(event.getPlayer().getUniqueId(), -1), null);
            if (orDefault==null)return;
            event.setCancelled(true);
            event.setUseInteractedBlock(Event.Result.DENY);
            event.setUseItemInHand(Event.Result.DENY);

            orDefault.on(event.getPlayer(),hotBarMenu, slotMap.get(event.getPlayer().getUniqueId()),clickType);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        HotBarMenu.destroy(event.getPlayer());

    }
}
