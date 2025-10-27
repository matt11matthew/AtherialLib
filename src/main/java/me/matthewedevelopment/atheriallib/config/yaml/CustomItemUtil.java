package me.matthewedevelopment.atheriallib.config.yaml;

import lombok.extern.java.Log;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;

@Log
public class CustomItemUtil {



    public static ItemStack applyCustomItem(ItemStack item, String namespacedKey) {
        if (item == null || namespacedKey == null) return item;

        try {
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return item;

            // Reflection-based access to Paper's custom model setter
            Method setItemModelMethod = meta.getClass().getMethod("setItemModel", NamespacedKey.class);
            setItemModelMethod.invoke(meta, NamespacedKey.fromString(namespacedKey));

            item.setItemMeta(meta);
        } catch (NoSuchMethodException e) {
            log.warning("ItemMeta#setItemModel not found — likely not a Paper version supporting it");
        } catch (Throwable t) {
            log.warning("Failed to set custom model via reflection: " + t.getMessage());
        }

        return item;
    }
}
