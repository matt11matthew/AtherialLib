package me.matthewedevelopment.atheriallib.config.yaml;

import lombok.extern.java.Log;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;

@Log
public class CustomItemUtil {


    public static ItemStack applyCustomItem(ItemStack item, String namespacedKey) {
        try {

            ItemMeta meta = item.getItemMeta();

            meta.setItemModel(NamespacedKey.fromString(namespacedKey));
            item.setItemMeta(meta);
        } catch (Exception e) {
        }
        return item;
    }
}
