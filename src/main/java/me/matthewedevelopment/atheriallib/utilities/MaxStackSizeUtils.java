package me.matthewedevelopment.atheriallib.utilities;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MaxStackSizeUtils {

    public static ItemStack setMaxStackSize(ItemStack itemStack, int maxStackSize) {
        try {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta == null) return itemStack;

            // Reflectively call ItemMeta#setMaxStackSize(int)
            Method method = meta.getClass().getMethod("setMaxStackSize", int.class);
            method.setAccessible(true);
            method.invoke(meta, maxStackSize);

            itemStack.setItemMeta(meta);
        } catch (NoSuchMethodException ignored) {
            // Method doesn't exist on this server version
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemStack;
    }

}
