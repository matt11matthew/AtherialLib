package me.matthewedevelopment.atheriallib.config.yaml;

import lombok.extern.java.Log;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

@Log
public class CustomItemUtil {

    private static final boolean SUPPORTS_CUSTOM_ITEM;
    private static final Method CUSTOM_ITEM_METHOD;
    private static final Method META_METHOD;

    static {
        Method customItemMethod = null;
        Method itemMetaMethod = null;
        boolean supports = false;

        try {
            // Try to access the Paper customItem method
            Class<?> itemStackClass = Class.forName("org.bukkit.inventory.ItemStack");
            itemMetaMethod = itemStackClass.getMethod("getItemMeta");
            Class<?> itemMetaClass = Class.forName("org.bukkit.inventory.meta.ItemMeta");
            customItemMethod = itemMetaClass.getMethod("setCustomItem", String.class);
            supports = true;
        } catch (Throwable ignored) {
            // Running on old version like 1.8.9
            log.warning("(1) Failed to create custom item api");
        }

        CUSTOM_ITEM_METHOD = customItemMethod;
        META_METHOD = itemMetaMethod;
        SUPPORTS_CUSTOM_ITEM = supports;
    }

    /**
     * Attempts to apply a customItem (e.g. avoma:back_button) to an existing item.
     * Safe to call on 1.8.9+, will do nothing if unsupported.
     */
    public static ItemStack applyCustomItem(ItemStack item, String namespacedKey) {
        if (!SUPPORTS_CUSTOM_ITEM || item == null) return item;

        try {
            Object meta = META_METHOD.invoke(item);
            if (meta != null) {
                CUSTOM_ITEM_METHOD.invoke(meta, namespacedKey);
                item.setItemMeta((org.bukkit.inventory.meta.ItemMeta) meta);
            }
        } catch (Throwable ignored) {
            log.warning("(2) Failed to apply custom item: " + namespacedKey);
            // Fail silently on unsupported versions or reflection errors
        }

        return item;
    }
}
