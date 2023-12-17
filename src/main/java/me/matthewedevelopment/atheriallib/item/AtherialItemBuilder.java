package me.matthewedevelopment.atheriallib.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.matthewedevelopment.atheriallib.io.DoubleStringReplacer;
import me.matthewedevelopment.atheriallib.io.StringReplacer;
import me.matthewedevelopment.atheriallib.utilities.ChatUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AtherialItemBuilder {
    private ItemStack itemStack;

    public AtherialItemBuilder(Builder builder) {
        this.itemStack = builder.itemStack;
    }
    public AtherialItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public static Builder builder() {
        return new Builder();
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public static final class Builder {
        private ItemStack itemStack;

        public Builder() {
            this.itemStack = new ItemStack(Material.STONE);
        }

        public Builder(AtherialItemBuilder atherialItem) {
            this.itemStack = atherialItem.itemStack;
        }

        public Builder itemStack(ItemStack itemStack) {
            this.itemStack = itemStack;
            return this;
        }

        public Builder replace(Player player, DoubleStringReplacer<Player> stringReplacer) {
            String displayName = getDisplayName();
            if (displayName != null) {
                this.displayName(stringReplacer.replace(player, displayName));
            }

            List<String> lore = getLore();
            if (lore != null) {
                this.lore(lore.stream().map(s -> stringReplacer.replace(player, s)).collect(Collectors.toList()));
            }
            return this;
        }

        public Builder customData(String data) {

            String skullType = "SKULL_ITEM";
            if (AtherialItemAPI.getAtherialLib().getVersionProvider().isGreaterThan1_13()) {
                skullType = "PLAYER_HEAD";
            }
            if (itemStack.getType().toString().equals(skullType)) {

                GameProfile profile = new GameProfile(UUID.randomUUID(), null);
                profile.getProperties().put("textures", new Property("textures", data));
                Field field;

                SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
                try {
                    field = skullMeta.getClass().getDeclaredField("profile");
                    field.setAccessible(true);
                    field.set(skullMeta, profile);
                } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                itemStack.setItemMeta(skullMeta);
                return this;
            }
            return this;
        }


        public Builder skullOwner(String data) {
            String skullType = "SKULL_ITEM";
            if (AtherialItemAPI.getAtherialLib().getVersionProvider().isGreaterThan1_13()) {
                skullType = "PLAYER_HEAD";
            }
            if (itemStack.getType().toString().equals(skullType)) {

                GameProfile profile = new GameProfile(UUID.randomUUID(), null);
                profile.getProperties().put("textures", new Property("textures", data));
                Field field;

                SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
                try {
                    field = skullMeta.getClass().getDeclaredField("profile");
                    field.setAccessible(true);
                    field.set(skullMeta, profile);
                } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                itemStack.setItemMeta(skullMeta);
                return this;
            }
            return this;
        }

        public Builder replace(StringReplacer stringReplacer) {
            String displayName = getDisplayName();
            if (displayName != null) {
                this.displayName(stringReplacer.replace(displayName));
            }

            List<String> lore = getLore();
            if (lore != null) {
                this.lore(lore.stream().map(s -> stringReplacer.replace(s)).collect(Collectors.toList()));
            }
            return this;
        }

        public Builder type(Material material) {
            this.itemStack.setType(material);
            return this;
        }

        public Builder durability(short durability) {
            this.itemStack.setDurability(durability);
            return this;
        }

        public Builder itemFlags(ItemFlag... itemFlags) {
            ItemMeta itemMeta = getItemMeta();

            itemMeta.addItemFlags(itemFlags);
            return itemMeta(itemMeta);
        }

        public Builder amount(int amount) {
            this.itemStack.setAmount(amount);
            return this;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }

        public Builder displayName(String displayName) {
            ItemMeta itemMeta = this.itemStack.getItemMeta();
            itemMeta.setDisplayName(ChatUtils.colorize(displayName));
            return this.itemMeta(itemMeta);
        }

        public String getDisplayName() {
            ItemMeta itemMeta = this.itemStack.getItemMeta();
            return itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : null;
        }

        public List<String> getLore() {
            ItemMeta itemMeta = this.itemStack.getItemMeta();
            return itemMeta.hasLore() ? itemMeta.getLore() : null;
        }



        public Builder lore(List<String> loreLines) {
            ItemMeta itemMeta = this.itemStack.getItemMeta();
            itemMeta.setLore(loreLines.stream().map(ChatUtils::colorize).collect(Collectors.toList()));
            return this.itemMeta(itemMeta);
        }

        public Builder appendLore(String line) {
            ItemMeta itemMeta = this.itemStack.getItemMeta();
            List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
            lore.add(ChatUtils.colorize(line));
            itemMeta.setLore(lore);
            return this.itemMeta(itemMeta);
        }

        public Builder lore(String... strings) {
            ItemMeta itemMeta = this.itemStack.getItemMeta();
            itemMeta.setLore(Arrays.stream(strings).map(ChatUtils::colorize).collect(Collectors.toList()));
            return this.itemMeta(itemMeta);
        }

        public Builder itemMeta(ItemMeta itemMeta) {
            this.itemStack.setItemMeta(itemMeta);
            return this;
        }

        public ItemMeta getItemMeta() {
            return this.itemStack.getItemMeta();
        }

        public AtherialItemBuilder build() {
            return new AtherialItemBuilder(this);
        }
    }
}
