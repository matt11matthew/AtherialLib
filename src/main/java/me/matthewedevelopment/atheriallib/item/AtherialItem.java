package me.matthewedevelopment.atheriallib.item;

import me.matthewedevelopment.atheriallib.nms.NbtAPI;
import org.bukkit.inventory.ItemStack;

public class AtherialItem {
    private ItemStack itemStack;

    public AtherialItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public static AtherialItem of(ItemStack itemStack) {
        return new AtherialItem(itemStack);
    }

    public <T> T getData(String key, Class<T> clazz) {
        NbtAPI nbtAPI = AtherialItemAPI.getAtherialLib().getVersionProvider().getNbtAPI();
        if (nbtAPI.hasTagKey(this.itemStack, key)) {
            String tagString = nbtAPI.getTagString(this.itemStack, key);
            return AtherialItemAPI.GSON.fromJson(tagString, clazz);
        }
        return null;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public <T> AtherialItem setData(String key, T t) {
        NbtAPI nbtAPI = AtherialItemAPI.getAtherialLib().getVersionProvider().getNbtAPI();
        this.itemStack = nbtAPI.setTagString(this.itemStack, key, AtherialItemAPI.GSON.toJson(t, t.getClass()));
        return this;
    }

    public <T> boolean hasData(String key, Class<T> clazz) {
        NbtAPI nbtAPI = AtherialItemAPI.getAtherialLib().getVersionProvider().getNbtAPI();
        if (nbtAPI.hasTagKey(itemStack, key)) {
            try {
                getData(key, clazz);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
}
