package me.matthewedevelopment.atheriallib.utilities;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;
import java.util.OptionalInt;

/**
 * Compile against Spigot 1.8 safely, but at runtime:
 * - If ItemMeta has setMaxStackSize(int) / getMaxStackSize(), use them via reflection.
 * - Otherwise, no-ops (set) and falls back to Material defaults (get).
 *
 * Thread-safe, cached reflection lookups.
 */
public final class StackSizeCompat {
    private StackSizeCompat() {}

    // Cached reflective methods (null when unavailable on this runtime)
    private static volatile Method META_SET_MAX_STACK; // ItemMeta#setMaxStackSize(int)
    private static volatile Method META_GET_MAX_STACK; // ItemMeta#getMaxStackSize()

    private static void ensureResolved() {
        if (META_SET_MAX_STACK != null || META_GET_MAX_STACK != null) return;

        synchronized (StackSizeCompat.class) {
            if (META_SET_MAX_STACK != null || META_GET_MAX_STACK != null) return;
            try {
                // Look up on the runtime ItemMeta *class* we actually have
                Class<?> itemMetaClass = ItemMeta.class;

                // Newer APIs (Paper/Spigot) expose these:
                // - public void setMaxStackSize(int)
                // - public int  getMaxStackSize()
                try {
                    META_SET_MAX_STACK = itemMetaClass.getMethod("setMaxStackSize", int.class);
                } catch (NoSuchMethodException ignored) {
                    META_SET_MAX_STACK = null;
                }

                try {
                    META_GET_MAX_STACK = itemMetaClass.getMethod("getMaxStackSize");
                } catch (NoSuchMethodException ignored) {
                    META_GET_MAX_STACK = null;
                }
            } catch (Throwable ignored) {
                // Keep methods null; we’ll gracefully no-op.
                META_SET_MAX_STACK = null;
                META_GET_MAX_STACK = null;
            }
        }
    }

    /**
     * Try to set a per-item max stack size using reflection.
     * @return true if the runtime supports it and we set it; false if unsupported or failed.
     */
    public static boolean setPerItemMaxStackSize(ItemStack item, int size) {
        if (item == null || size < 1) return false;
        ensureResolved();

        ItemMeta meta = item.getItemMeta();
        if (meta == null || META_SET_MAX_STACK == null) return false;

        try {
            META_SET_MAX_STACK.invoke(meta, size);
            item.setItemMeta(meta);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    /**
     * Get a per-item max stack size if the runtime exposes it.
     * If not available or not set, returns OptionalInt.empty().
     */
    public static OptionalInt getPerItemMaxStackSize(ItemStack item) {
        if (item == null) return OptionalInt.empty();
        ensureResolved();

        ItemMeta meta = item.getItemMeta();
        if (meta == null || META_GET_MAX_STACK == null) return OptionalInt.empty();

        try {
            Object value = META_GET_MAX_STACK.invoke(meta);
            if (value instanceof Integer) {
                int v = (Integer) value;
                if (v > 0) return OptionalInt.of(v);
            }
            return OptionalInt.empty();
        } catch (Throwable ignored) {
            return OptionalInt.empty();
        }
    }

    /**
     * Convenience: return the effective stack size for this ItemStack.
     * Prefers per-item size when available; otherwise falls back to Material default.
     */
    public static int getEffectiveStackSize(ItemStack item) {
        OptionalInt perItem = getPerItemMaxStackSize(item);
        if (perItem.isPresent()) return perItem.getAsInt();
        if (item == null) return 64;

        Material type = item.getType();
        return (type != null ? type.getMaxStackSize() : 64);
    }

    /**
     * Convenience: copy another material's default max stack size onto this ItemStack (if supported at runtime).
     * @return true if per-item size was set; false if unsupported or failed.
     */
    public static boolean setFromMaterialDefault(ItemStack item, Material from) {
        if (item == null || from == null) return false;
        return setPerItemMaxStackSize(item, from.getMaxStackSize());
    }
}
