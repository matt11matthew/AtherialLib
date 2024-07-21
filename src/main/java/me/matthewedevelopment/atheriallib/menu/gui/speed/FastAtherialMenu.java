package me.matthewedevelopment.atheriallib.menu.gui.speed;

import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.config.yaml.AtherialLibItem;
import me.matthewedevelopment.atheriallib.config.yaml.YamlConfig;
import me.matthewedevelopment.atheriallib.config.yaml.serializables.list.IntSimpleList;
import me.matthewedevelopment.atheriallib.item.ItemUtils;
import me.matthewedevelopment.atheriallib.utilities.AtherialTasks;
import me.matthewedevelopment.atheriallib.utilities.ChatUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import spigui.buttons.SGButton;
import spigui.menu.SGMenu;

@Deprecated
public abstract class FastAtherialMenu<C extends YamlConfig> {
    protected Player player;
    protected SGMenu menu;
    protected C c;





    public abstract boolean update();

    public boolean isOnline(){
        return player!=null&&player.isOnline();
    }
    public FastAtherialMenu(Player player, C c) {
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

        if (AtherialLib.getInstance().getFastAtherialMenuRegistry().getMenuMap().containsKey(player.getUniqueId())) {
            FastAtherialMenu fastAtherialMenu = AtherialLib.getInstance().getFastAtherialMenuRegistry().getMenuMap().get(player.getUniqueId());
            if (!fastAtherialMenu.getClass().getSimpleName().equals(this.getClass().getSimpleName())){

                fastAtherialMenu.onRealClose();
            }
            AtherialLib.getInstance().getFastAtherialMenuRegistry().getMenuMap().remove(player.getUniqueId());
        }
        AtherialLib.getInstance().getFastAtherialMenuRegistry().getMenuMap().put(player.getUniqueId(), this);

        if (menu == null) {
            menu = generateMenu(player);

        }

        open();
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

    public abstract void onRealClose();

    public SGMenu getMenu() {
        return menu;
    }

    public abstract SGMenu generateMenu(Player player);
    public void open() {

        AtherialTasks.runAsync(() -> {
            while (ItemUtils.isEmpty(menu.getInventory())){

                if (!ItemUtils.isEmpty(menu.getInventory())){
                    AtherialTasks.runSync(() -> {

                        player.openInventory(menu.getInventory());
                    });
                    break;
                }
            }
            if (!ItemUtils.isEmpty(menu.getInventory())){
                AtherialTasks.runSync(() -> {

                    player.openInventory(menu.getInventory());
                });
            }
        });
    }

    private boolean updating = false;

    public boolean isUpdating() {
        return updating;
    }

    public void firstUpdate() {
        updating = true;
        if (update()){

            menu.refreshInventory(player);
        }
        updating = false;
    }




}
