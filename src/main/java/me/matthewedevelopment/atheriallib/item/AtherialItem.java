package me.matthewedevelopment.atheriallib.item;

import com.google.gson.JsonSyntaxException;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

public class AtherialItem {
    private ItemStack itemStack;

    public AtherialItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public static AtherialItem of(ItemStack itemStack) {
        return new AtherialItem(itemStack);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Read JSON-serialized value from NBT and deserialize to clazz.
     */
    public <T> T getData(String key, Class<T> clazz) {
        if (itemStack == null) return null;
        NBTItem nbt = new NBTItem(itemStack);
        if (!nbt.hasKey(key)) return null;

        String json = nbt.getString(key);
        if (json == null || json.isEmpty()) return null;

        try {
            return AtherialItemAPI.GSON.fromJson(json, clazz);
        } catch (JsonSyntaxException ex) {
            return null;
        }
    }

    /**
     * Serialize value as JSON and store under key in NBT.
     * Returns this for chaining. Updates internal ItemStack reference.
     */
    public <T> AtherialItem setData(String key, T value) {
        if (itemStack == null) return this;

        // true = direct modification mode (avoid cloning surprises on some server jars)
        NBTItem nbt = new NBTItem(itemStack, true);
        String json = (value == null) ? null : AtherialItemAPI.GSON.toJson(value);

        if (json == null) {
            // remove key if you prefer cleanup; otherwise skip
            if (nbt.hasKey(key)) nbt.removeKey(key);
        } else {
            nbt.setString(key, json);
        }

        // Persist mutated handle back to our field
        this.itemStack = nbt.getItem();
        return this;
    }

    /**
     * Quick existence + parse check.
     */
    public <T> boolean hasData(String key, Class<T> clazz) {
        if (itemStack == null) return false;

        NBTItem nbt = new NBTItem(itemStack);
        if (!nbt.hasKey(key)) return false;

        try {
            String json = nbt.getString(key);
            if (json == null || json.isEmpty()) return false;
            AtherialItemAPI.GSON.fromJson(json, clazz); // validate
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
