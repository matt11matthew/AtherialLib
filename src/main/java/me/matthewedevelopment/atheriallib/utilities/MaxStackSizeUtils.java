package me.matthewedevelopment.atheriallib.utilities;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MaxStackSizeUtils {

    public static ItemStack setMaxStackSize(ItemStack itemStack, int maxStackSize) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setMaxStackSize(maxStackSize);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
