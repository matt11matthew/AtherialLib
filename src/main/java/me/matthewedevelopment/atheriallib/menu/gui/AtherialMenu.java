package me.matthewedevelopment.atheriallib.menu.gui;

import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.config.yaml.AtherialLibItem;
import me.matthewedevelopment.atheriallib.config.yaml.YamlConfig;
import me.matthewedevelopment.atheriallib.config.yaml.serializables.list.IntSimpleList;
import me.matthewedevelopment.atheriallib.item.ItemUtils;
import me.matthewedevelopment.atheriallib.utilities.AtherialTasks;
import me.matthewedevelopment.atheriallib.utilities.ChatUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import spigui.buttons.SGButton;
import spigui.buttons.SGButtonListener;
import spigui.menu.SGMenu;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
@Deprecated
public abstract class AtherialMenu<C extends YamlConfig> {
    protected Player player;
    protected SGMenu menu;
    protected C c;

    private boolean slow =false;

    public boolean isSlow() {

        return slow;
    }
    public  <E> List<E> getPageItems(List<E> allItems, int amountPerPage, int currentPage) {
        if (amountPerPage <= 0) {
            throw new IllegalArgumentException("amountPerPage must be greater than zero");
        }
        if (currentPage <= 0) {
            throw new IllegalArgumentException("currentPage must be greater than zero");
        }

        int startIndex = (currentPage - 1) * amountPerPage;
        if (startIndex >= allItems.size()) {
            return Collections.emptyList(); // Return an empty list if the start index is beyond the list size
        }

        int endIndex = Math.min(startIndex + amountPerPage, allItems.size());
        return allItems.subList(startIndex, endIndex);
    }
    public  <E> int getMaxPage(List<E> items, int amountPerPage) {
        if (amountPerPage <= 0) {
            throw new IllegalArgumentException("amountPerPage must be greater than zero");
        }

        // No need for a loop - calculate the maximum page directly
        int totalItems = items.size();
        return (totalItems == 0) ? 1 : (int) Math.ceil((double) totalItems / amountPerPage);
    }
    public void setSlow(boolean slow) {
        this.slow = slow;
    }

    public abstract void update();

    public boolean isOnline(){
        return player!=null&&player.isOnline();
    }
    public AtherialMenu(Player player, C c) {
        this.player = player;
        this.menu = null;
        this.c = c;
    }


    public void clearMenu() {
        for (int i = 0; i < (menu.getRowsPerPage() * 9) - 1; i++) {
            menu.setButton(i,new SGButton(new ItemStack(Material.AIR)));
        }
    }
    public void destroy(){
        AtherialLib.getInstance().getMenuRegistry().destroy(player);
    }

    public void create() {
        if (AtherialLib.getInstance().getMenuRegistry().getMenuMap().containsKey(player.getUniqueId())) {
            AtherialLib.getInstance().getMenuRegistry().getMenuMap().get(player.getUniqueId()).onRealClose();
            AtherialLib.getInstance().getMenuRegistry().getMenuMap().remove(player.getUniqueId());
        }
        AtherialLib.getInstance().getMenuRegistry().getMenuMap().put(player.getUniqueId(), this);
        if (menu == null) {
            needsUpdate = true;
            menu = generateMenu(player);
            firstUpdate();

        }

        open();
    }

    public void forceUpdate() {
        setNeedsUpdate(true);
        firstUpdate();
    }

    public int getRows() {
        return 1;
    }

    public  String getTitle() {
        return "title";
    }

    public String colorize(String input) {
        if (menu==null) {
            return ChatUtils.colorize(input);
        }
        int curPage = menu.getCurrentPage()+1;

        return ChatUtils.colorize(input)
                .replace("%page%",curPage+"")
                .replace("%max_page%", menu.getMaxPage()+"")
                .replace("{page}",curPage+"")
                .replace("{max_page}", menu.getMaxPage()+"");
    }

    public void setBackground(IntSimpleList slots, AtherialLibItem background) {
        if (menu==null)return;
        for (Integer slot : slots) {
            menu.setButton(slot, new SGButton(background.build()));
        }
    }

    public void setBackground(int page, IntSimpleList slots, AtherialLibItem background) {
        if (menu==null)return;
        for (Integer slot : slots) {
            menu.setButton(page, slot, new SGButton(background.build()));
        }
    }
    public void set2DItem(int col, int row, SGButton b ){
        menu.setButton(row * 9 + col, b);
    }

    public  void onRealClose(){}

    public SGMenu getMenu() {
        return menu;
    }

    public  SGMenu generateMenu(Player player) {
        SGMenu sgMenu = AtherialLib.getInstance().getMenu().create(this, getTitle(), getRows());
        return sgMenu;
    }
    /** Optional: provide an Adventure Component title. If null, getTitle() string is used. */
    public Component getTitleComponent() {
        return null; // subclasses can override
    }
    private static boolean tryOpenWithComponentTitle(Player player, Inventory inv, Component title) {
        if (player == null || inv == null || title == null) return false;
        try {
            // Paper API: Player#openInventory(Inventory, Component)
            Method m = Player.class.getMethod("openInventory", Inventory.class, Component.class);
            m.invoke(player, inv, title);
            return true;
        } catch (NoSuchMethodException ignored) {
            // Not on this platform/version
            return false;
        } catch (Throwable t) {
            return false;
        }
    }


    public void open() {
        AtherialTasks.runAsync(() -> {
            while (ItemUtils.isEmpty(menu.getInventory())) {
                if (!ItemUtils.isEmpty(menu.getInventory())) {
                    AtherialTasks.runSync(() -> {
                        // Prefer component title if provided and supported
                        Component compTitle = getTitleComponent();
                        if (compTitle != null && tryOpenWithComponentTitle(player, menu.getInventory(), compTitle)) {
                            return;
                        }
                        player.openInventory(menu.getInventory());
                    });
                    return;
                }
            }
            AtherialTasks.runSync(() -> {
                Component compTitle = getTitleComponent();
                if (compTitle != null && tryOpenWithComponentTitle(player, menu.getInventory(), compTitle)) {
                    return;
                }
                player.openInventory(menu.getInventory());
            });
        });
    }

    public void set(int slot, ItemStack itemStack, SGButtonListener listener) {
        menu.setButton(slot, new SGButton(itemStack).withListener(listener));
    }

    public void set(int slot, ItemStack itemStack) {
        menu.setButton(slot, new SGButton(itemStack));
    }

    private boolean updating = false;

    public boolean isUpdating() {
        return updating;
    }

    public void firstUpdate() {
        updating = true;
        update();
        menu.refreshInventory(player);
        updating = false;
        needsUpdate  = false;
    }

    private boolean needsUpdate;

    public  void setNeedsUpdate(boolean needsUpdate) {
        this.needsUpdate = needsUpdate;
    }

    public boolean needsUpdate() {
        return needsUpdate;
    }
}
