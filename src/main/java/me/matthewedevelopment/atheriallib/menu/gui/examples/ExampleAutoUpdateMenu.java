package me.matthewedevelopment.atheriallib.menu.gui.examples;

import me.matthewedevelopment.atheriallib.config.yaml.YamlConfig;
import me.matthewedevelopment.atheriallib.menu.gui.AtherialMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Example menu demonstrating the auto-update feature.
 * This menu updates automatically every 1 second to show live server time and player stats.
 */
public class ExampleAutoUpdateMenu extends AtherialMenu<YamlConfig> {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private int updateCount = 0;

    public ExampleAutoUpdateMenu(Player player) {
        super(player, null);
    }

    @Override
    public String getTitle() {
        return "&a&lLive Stats Menu";
    }

    @Override
    public int getRows() {
        return 3; // 3 rows (27 slots)
    }

    @Override
    public void update() {
        // This method is called automatically every interval
        updateCount++;

        // Slot 10: Current server time (updates every second)
        set(10, createTimeItem());

        // Slot 12: Player location (updates every second)
        set(12, createLocationItem());

        // Slot 14: Update counter
        set(14, createCounterItem());

        // Slot 16: Player health and food
        set(16, createHealthItem());

        // Slot 22: Close button (static, doesn't need updating)
        if (updateCount == 1) { // Only set once
            set(22, createCloseButton(), event -> {
                player.closeInventory();
            });
        }
    }

    private ItemStack createTimeItem() {
        ItemStack item = new ItemStack(Material.CLOCK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(colorize("&e&lCurrent Time"));
            meta.setLore(Arrays.asList(
                colorize("&7Time: &f" + dateFormat.format(new Date())),
                colorize("&7Updates: &f" + updateCount),
                colorize("&a&lLIVE")
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createLocationItem() {
        ItemStack item = new ItemStack(Material.COMPASS);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(colorize("&b&lYour Location"));
            meta.setLore(Arrays.asList(
                colorize("&7World: &f" + player.getWorld().getName()),
                colorize("&7X: &f" + (int) player.getLocation().getX()),
                colorize("&7Y: &f" + (int) player.getLocation().getY()),
                colorize("&7Z: &f" + (int) player.getLocation().getZ()),
                colorize("&a&lLIVE")
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createCounterItem() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(colorize("&d&lUpdate Counter"));
            meta.setLore(Arrays.asList(
                colorize("&7Total Updates: &f" + updateCount),
                colorize("&7Interval: &f1 second"),
                colorize("&a&lLIVE")
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createHealthItem() {
        ItemStack item = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(colorize("&c&lYour Stats"));
            meta.setLore(Arrays.asList(
                colorize("&7Health: &f" + (int) player.getHealth() + "/20"),
                colorize("&7Food: &f" + player.getFoodLevel() + "/20"),
                colorize("&7Level: &f" + player.getLevel()),
                colorize("&a&lLIVE")
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createCloseButton() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(colorize("&c&lClose Menu"));
            meta.setLore(Arrays.asList(
                colorize("&7Click to close")
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Opens the menu with auto-update enabled.
     * Updates occur every 1000ms (1 second).
     */
    public void openWithAutoUpdate() {
        create(); // Create and open the menu

        // Enable auto-updates every 1 second (1000ms)
        // The menu will automatically update the time, location, and stats
        setAutoUpdateInterval(1000);
    }

    /**
     * Opens the menu with a custom update interval.
     *
     * @param intervalMs Update interval in milliseconds
     */
    public void openWithCustomInterval(long intervalMs) {
        create();
        setAutoUpdateInterval(intervalMs);
    }

    @Override
    public void onRealClose() {
        super.onRealClose(); // Important: cancels the auto-update task
        // You can add cleanup logic here
        player.sendMessage(colorize("&aMenu closed! Total updates: " + updateCount));
    }
}

