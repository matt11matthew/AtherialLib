package me.matthewedevelopment.atheriallib.config.yaml;

import lombok.extern.java.Log;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;

@Log
public class CustomItemUtil {

    /** Back-compat: sets only the custom model (item model) */
    public static ItemStack applyCustomItem(ItemStack item, String modelKey) {
        return applyCustomItem(item, modelKey, null);
    }

    /**
     * Sets custom model and/or tooltip style (Paper 1.21+).
     * Any null/empty key is ignored. Uses API first, then CraftMeta fallback via reflection.
     */
    public static ItemStack applyCustomItem(ItemStack item, String modelKey, String tooltipStyleKey) {
        if (item == null) return null;

        final ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        // Parse keys (ignore invalid)
        final NamespacedKey model = parseKey(modelKey, "item model");
        final NamespacedKey tooltip = parseKey(tooltipStyleKey, "tooltip style");

        boolean mutated = false;

        // 1) Try public API first
        if (model != null && tryInvokeApi(meta, "setItemModel", model)) mutated = true;
        if (tooltip != null && tryInvokeApi(meta, "setTooltipStyle", tooltip)) mutated = true;

        // 2) Fallback to Craft implementation (force accessible)
        if (model != null && !hasModel(meta, model) && tryInvokeImpl(meta, "setItemModel", model)) mutated = true;
        if (tooltip != null && !hasTooltip(meta, tooltip) && tryInvokeImpl(meta, "setTooltipStyle", tooltip)) mutated = true;

        if (mutated) item.setItemMeta(meta);
        return item;
    }

    // ---------- helpers ----------

    private static NamespacedKey parseKey(String key, String kind) {
        if (key == null || key.isEmpty()) return null;
        NamespacedKey ns = NamespacedKey.fromString(key);
        if (ns == null) log.warning("Invalid " + kind + " namespaced key: " + key);
        return ns;
    }

    private static boolean tryInvokeApi(ItemMeta meta, String method, NamespacedKey key) {
        try {
            Method m = ItemMeta.class.getMethod(method, NamespacedKey.class);
            m.invoke(meta, key);
            return true;
        } catch (NoSuchMethodException e) {
            // Older/non-Paper API
            return false;
        } catch (IllegalAccessException e) {
            // Will try impl fallback next
            return false;
        } catch (Throwable t) {
            log.warning("API call " + method + " failed: " + t.getClass().getSimpleName() + ": " + t.getMessage());
            return false;
        }
    }

    private static boolean tryInvokeImpl(ItemMeta meta, String method, NamespacedKey key) {
        try {
            Method m = meta.getClass().getDeclaredMethod(method, NamespacedKey.class);
            m.setAccessible(true);
            m.invoke(meta, key);
            return true;
        } catch (NoSuchMethodException e) {
            log.warning("Impl method " + method + " not found on " + meta.getClass().getName());
            return false;
        } catch (Throwable t) {
            log.warning("Impl call " + method + " failed: " + t.getClass().getSimpleName() + ": " + t.getMessage());
            return false;
        }
    }

    // Optional guards to avoid double work if getters exist (best-effort).
    private static boolean hasModel(ItemMeta meta, NamespacedKey expected) {
        try {
            Method g = ItemMeta.class.getMethod("getItemModel");
            Object v = g.invoke(meta);
            return expected.equals(v);
        } catch (Throwable ignored) { return false; }
    }

    private static boolean hasTooltip(ItemMeta meta, NamespacedKey expected) {
        try {
            Method g = ItemMeta.class.getMethod("getTooltipStyle");
            Object v = g.invoke(meta);
            return expected.equals(v);
        } catch (Throwable ignored) { return false; }
    }
}
