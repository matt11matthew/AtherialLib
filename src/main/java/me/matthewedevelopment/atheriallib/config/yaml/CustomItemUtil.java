package me.matthewedevelopment.atheriallib.config.yaml;

import lombok.extern.java.Log;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;

@Log
public class CustomItemUtil {

    public static ItemStack applyCustomItem(ItemStack item, String namespacedKey) {
        if (item == null || namespacedKey == null || namespacedKey.isEmpty()) return item;

        final NamespacedKey key = NamespacedKey.fromString(namespacedKey);
        if (key == null) {
            log.warning("Invalid namespaced key: " + namespacedKey);
            return item;
        }

        final ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        // 1) Prefer reflecting on the interface (prevents IllegalAccess on CraftMetaItem)
        try {
            Method api = ItemMeta.class.getMethod("setItemModel", NamespacedKey.class);
            api.invoke(meta, key);
            item.setItemMeta(meta);
            return item;
        } catch (NoSuchMethodException e) {
            log.warning("ItemMeta#setItemModel not found (older/non-Paper API?)");
        } catch (IllegalAccessException e) {
            // Fall through to impl reflection
        } catch (Throwable t) {
            log.warning("API reflection failed: " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }

        // 2) Fallback: call the Craft implementation but force accessibility
        try {
            Method impl = meta.getClass().getDeclaredMethod("setItemModel", NamespacedKey.class);
            impl.setAccessible(true); // avoid "cannot access member of CraftMetaItem"
            impl.invoke(meta, key);
            item.setItemMeta(meta);
            return item;
        } catch (Throwable t) {
            log.warning("Impl reflection failed: " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }

        // 3) Last resort: skip silently (or add your NMS/DataComponents fallback here if you want)
        return item;
    }
}
