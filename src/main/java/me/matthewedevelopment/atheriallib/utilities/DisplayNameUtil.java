package me.matthewedevelopment.atheriallib.utilities;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;

public final class DisplayNameUtil {
    private DisplayNameUtil() {}

    /** Try ItemMeta.displayName(Component) via reflection (Paper/Adventure). */
    public static boolean tryAdventureSetter(ItemMeta meta, Component component) {
        if (meta == null || component == null) return false;
        try {
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
        } catch (Throwable ignored) {
            return false;
        }
    }

    /** Try ItemMeta.lore(List<Component>) via reflection (Paper/Adventure). */
    @SuppressWarnings("unchecked")
    public static boolean tryAdventureLoreSetter(ItemMeta meta, List<Component> lore) {
        if (meta == null) return false;
        try {
            // Prefer the Adventure setter: ItemMeta.lore(List<Component>)
            Method m;
            try {
                // Method on the interface (Paper)
                m = ItemMeta.class.getMethod("lore", List.class);
            } catch (NoSuchMethodException e) {
                m = findMethod(meta.getClass(), "lore", List.class);
            }
            if (m != null && acceptsComponentList(m)) {
                m.invoke(meta, lore); // can be null to clear
                return true;
            }
        } catch (Throwable ignored) {
            // fall through to legacy
        }

        // Legacy fallback: ItemMeta.setLore(List<String>)
        try {
            Method legacySetter = findMethod(meta.getClass(), "setLore", List.class);
            if (legacySetter != null) {
                List<String> legacy = (lore == null) ? null : toLegacyList(lore);
                legacySetter.invoke(meta, legacy);
                return true;
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    /** Try to read lore as List<Component> (convert legacy to components if needed). */
    @SuppressWarnings("unchecked")
    public static List<Component> tryAdventureLoreGetter(ItemMeta meta) {
        if (meta == null) return null;

        // Adventure getter: ItemMeta.lore() -> List<Component>
        try {
            Method getter = findZeroArg(meta.getClass(), "lore");
            if (getter != null && returnsComponentList(getter)) {
                Object res = getter.invoke(meta);
                return (List<Component>) res; // may be null
            }
        } catch (Throwable ignored) {
        }

        // Legacy getter: ItemMeta.getLore() -> List<String>
        try {
            Method legacyGetter = findZeroArg(meta.getClass(), "getLore");
            if (legacyGetter != null && legacyGetter.getReturnType() == List.class) {
                Object res = legacyGetter.invoke(meta);
                if (res instanceof List<?> raw) {
                    return fromLegacyList((List<String>) raw);
                }
            }
        } catch (Throwable ignored) {
        }

        return null;
    }

    /** Apply a transform to each lore line (Component) regardless of API; returns true if changed. */
    public static boolean mapLore(ItemMeta meta, UnaryOperator<Component> op) {
        if (meta == null || op == null) return false;

        List<Component> current = tryAdventureLoreGetter(meta);
        if (current == null) current = List.of();

        List<Component> mapped = new ArrayList<>(current.size());
        boolean changed = false;
        for (Component c : current) {
            Component n = op.apply(c == null ? Component.empty() : c);
            mapped.add(n);
            if (!Objects.equals(c, n)) changed = true;
        }

        // If no lore existed and none produced, nothing to do
        if (!changed && current.isEmpty() && mapped.isEmpty()) return false;

        return tryAdventureLoreSetter(meta, mapped);
    }

    // ---------- helpers ----------

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

    private static Method findZeroArg(Class<?> type, String name) {
        for (Method m : type.getMethods()) {
            if (m.getName().equals(name) && m.getParameterCount() == 0) return m;
        }
        return null;
    }

    private static boolean acceptsComponentList(Method m) {
        // Best-effort check: if method name is "lore" and param is List, we assume List<Component> on Paper
        if (!m.getName().equals("lore")) return false;
        Class<?>[] pts = m.getParameterTypes();
        if (pts.length != 1 || pts[0] != List.class) return false;
        // If Adventure is on classpath, this method on Paper is the Component version.
        return true;
    }

    private static boolean returnsComponentList(Method m) {
        // On Paper, lore() returns List<Component>; erased to List at runtime.
        return m.getName().equals("lore") && m.getParameterCount() == 0 && m.getReturnType() == List.class;
    }

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    private static List<String> toLegacyList(List<Component> comps) {
        if (comps == null) return null;
        List<String> out = new ArrayList<>(comps.size());
        for (Component c : comps) out.add(LEGACY.serialize(c == null ? Component.empty() : c));
        return out;
    }

    private static List<Component> fromLegacyList(List<String> lines) {
        if (lines == null) return null;
        List<Component> out = new ArrayList<>(lines.size());
        for (String s : lines) out.add(LEGACY.deserialize(s == null ? "" : s));
        return out;
    }
}
