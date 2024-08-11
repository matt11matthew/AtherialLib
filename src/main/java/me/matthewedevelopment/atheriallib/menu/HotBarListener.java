package me.matthewedevelopment.atheriallib.menu;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.matthewedevelopment.atheriallib.AtherialLib;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class HotBarListener  implements Listener {
    private Map<UUID, Integer> slotMap = new HashMap<>();
    private AtherialLib atherialLib;
    private Map<UUID, Long> DELAY_MAP = new HashMap<>();

    public HotBarListener(AtherialLib atherialLib) {
        this.atherialLib = atherialLib;
    }

    public void addDelay(Player player, long delay){
        DELAY_MAP.put(player.getUniqueId(), delay+System.currentTimeMillis());
    }
    public void removeDelay(Player player) {
        if (DELAY_MAP.containsKey(player.getUniqueId())) {
            DELAY_MAP.remove(player.getUniqueId());
        }
    }
    public boolean isDelayed(Player player) {
        if (!DELAY_MAP.containsKey(player.getUniqueId())){
            return false;
        }
        if (System.currentTimeMillis()>DELAY_MAP.get(player.getUniqueId())){
            DELAY_MAP.remove(player.getUniqueId());
            return false;
        }
        return true;
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
                if (hotBarMenu.isHidden(event.getSlot()))return;
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (HotBarMenu.hasHotBar(event.getPlayer())) {
            Item itemDrop = event.getItemDrop();
            if (itemDrop==null)return;
            ItemStack itemStack = itemDrop.getItemStack();
            if (itemStack==null||itemStack.getType()== Material.AIR)return;

            NBTItem nbtItem = new NBTItem(itemStack);
            if (nbtItem==null)return;
            if (!nbtItem.hasNBTData())return;

            if (!nbtItem.hasTag("hot_bar_item"))return;

            event.setCancelled(true);


        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        HotBarClickType clickType = null;
        if (!HotBarMenu.MENUS.containsKey(event.getPlayer().getUniqueId())){
            return;
        }
        if (event.getAction().toString().contains("RIGHT")) {
            clickType = HotBarClickType.RIGHT;
        } else if (event.getAction().toString().contains("LEFT")) {
            clickType=HotBarClickType.LEFT;
        }
        if (clickType==null)return;
        HotBarMenu hotBarMenu = HotBarMenu.get(event.getPlayer());
        if (hotBarMenu.getItemMap().containsKey(slotMap.getOrDefault(event.getPlayer().getUniqueId(), -1))) {
            if (hotBarMenu.isHidden(slotMap.get(event.getPlayer().getUniqueId())))return;
            OnHotBarAction orDefault = hotBarMenu.getActionMap().getOrDefault(slotMap.getOrDefault(event.getPlayer().getUniqueId(), -1), null);
            if (orDefault==null)return;

            if (isDelayed(event.getPlayer())) {
                event.setCancelled(true);
                event.setUseInteractedBlock(Event.Result.DENY);
                event.setUseItemInHand(Event.Result.DENY);

                return;
            }

            addDelay(event.getPlayer(), 200L);
            Optional<Block> blockOptional = (event.getAction()== Action.RIGHT_CLICK_BLOCK||event.getAction()==Action.LEFT_CLICK_BLOCK)? Optional.ofNullable(event.getClickedBlock()) : Optional.empty();
            boolean on = orDefault.on(event.getPlayer(), hotBarMenu, slotMap.get(event.getPlayer().getUniqueId()), clickType, blockOptional);
            if (!on){
                event.setCancelled(false);
                event.setUseInteractedBlock(Event.Result.ALLOW);
                event.setUseItemInHand(Event.Result.ALLOW);
            } else {
                event.setCancelled(true);
                event.setUseInteractedBlock(Event.Result.DENY);
                event.setUseItemInHand(Event.Result.DENY);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        HotBarMenu.destroy(event.getPlayer());
        removeDelay(event.getPlayer());
    }
}
