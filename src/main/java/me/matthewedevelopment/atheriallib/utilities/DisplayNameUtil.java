package me.matthewedevelopment.atheriallib.utilities;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.UnaryOperator;

public final class DisplayNameUtil {
    private DisplayNameUtil() {}


    /** Try ItemMeta.displayName(Component) via reflection. */
    public static boolean tryAdventureSetter(ItemMeta meta, Component component) {
        try {
            // Prefer method on the interface (exists on modern Paper),
            // but if not, scan the concrete class too.
            Class<?> compCls = Class.forName("net.kyori.adventure.text.Component");

            Method m;
            try {
                m = ItemMeta.class.getMethod("displayName", compCls);
            } catch (NoSuchMethodException e) {
                m = findMethod(meta.getClass(), "displayName", compCls);
            }
            if (m == null) return false;

            m.invoke(meta, component);
            return true;
        } catch (ClassNotFoundException ignored) {
            // Adventure not on classpath
            return false;
        } catch (Throwable t) {
            return false;
        }
    }

    /** Fallback: ItemMeta.setDisplayName(String) */
    private static void tryLegacySetter(ItemMeta meta, String legacy) {
        try {
            Method m;
            try {
                m = ItemMeta.class.getMethod("setDisplayName", String.class);
            } catch (NoSuchMethodException e) {
                m = findMethod(meta.getClass(), "setDisplayName", String.class);
            }
            if (m != null) m.invoke(meta, Objects.toString(legacy, ""));
        } catch (Throwable ignored) {
            // last resort: do nothing
        }
    }

    private static Method findMethod(Class<?> type, String name, Class<?> param) {
        for (Method m : type.getMethods()) {
            if (m.getName().equals(name)
                && m.getParameterCount() == 1
                && m.getParameterTypes()[0].isAssignableFrom(param)) {
                return m;
            }
        }
        return null;
    }
}
