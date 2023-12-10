package me.matthewedevelopment.atheriallib.nms;

import org.bukkit.inventory.ItemStack;

/**
 * Created by Matthew Eisenberg on 7/4/2018 at 3:10 PM for the project atherialapi
 */
public interface NbtAPI {
    Version getVersion();

    VersionProvider getVersionProvider();

    ItemStack setTagInt(ItemStack itemStack, String key, int value);

    int getTagInt(ItemStack itemStack, String key);

    String getTagString(ItemStack itemStack, String key);

    boolean hasTagKey(ItemStack itemStack, String key);

    ItemStack removeTag(ItemStack itemStack, String key);

    ItemStack setTagString(ItemStack itemStack, String key, String value);
}
