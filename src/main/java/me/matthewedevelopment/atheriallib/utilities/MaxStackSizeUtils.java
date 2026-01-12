package me.matthewedevelopment.atheriallib.utilities;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;

public class MaxStackSizeUtils {

    @Getter
    @Setter
    private static MaxStackSizeProvider maxStackSizeProvider;


    public static abstract class MaxStackSizeProvider {
        public abstract ItemStack setMaxStackSize(ItemStack itemStack, int maxStackSize);

    }

    public static ItemStack setMaxStackSize(ItemStack itemStack, int maxStackSize) {
        if (maxStackSizeProvider != null) {
            return maxStackSizeProvider.setMaxStackSize(itemStack, maxStackSize);
        }
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