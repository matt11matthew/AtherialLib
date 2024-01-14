package me.matthewedevelopment.atheriallib.menu.gui;

import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.config.yaml.AtherialLibItem;
import me.matthewedevelopment.atheriallib.config.yaml.YamlConfig;
import me.matthewedevelopment.atheriallib.config.yaml.serializables.list.IntSimpleList;
import me.matthewedevelopment.atheriallib.item.ItemUtils;
import me.matthewedevelopment.atheriallib.utilities.AtherialTasks;
import me.matthewedevelopment.atheriallib.utilities.ChatUtils;
import org.bukkit.entity.Player;
import spigui.buttons.SGButton;
import spigui.menu.SGMenu;

public abstract class AtherialMenu<C extends YamlConfig> {
    protected Player player;
    protected SGMenu menu;







    public abstract void update();

    public AtherialMenu(Player player) {
        this.player = player;
        this.menu = null;
    }

    public void destroy(){
        AtherialLib.getInstance().getMenuRegistry().destroy(player);
    }
    public void create(C config) {

        if (AtherialLib.getInstance().getMenuRegistry().getMenuMap().containsKey(player.getUniqueId())) {

            AtherialLib.getInstance().getMenuRegistry().getMenuMap().remove(player.getUniqueId());
        }
        AtherialLib.getInstance().getMenuRegistry().getMenuMap().put(player.getUniqueId(), this);

        if (menu == null) {
            menu = generateMenu(player, config);

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

    public SGMenu getMenu() {
        return menu;
    }

    public abstract SGMenu generateMenu(Player player, C config);
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

    public void firstUpdate() {
        update();
        menu.refreshInventory(player);
    }
}
