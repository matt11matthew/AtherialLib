package me.matthewedevelopment.atheriallib.config.yaml;

import me.matthewedevelopment.atheriallib.io.StringReplacer;
import me.matthewedevelopment.atheriallib.utilities.ChatUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import spigui.item.ItemBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthew E on 12/23/2023 at 10:19 PM for the project AtherialLib
 */
public class AtherialLibItem {
    private Material type;
    private int amount;
    private String displayName;
    private List<String> lore;

    public int getData() {
        return data;
    }

    private int data = -1;

    private String skullOwner;

    private int slot = -1;
    public AtherialLibItem(Material type, int amount, String displayName, List<String> lore, String skullOwner, int slot) {
        this.type = type;
        this.amount = amount;
        this.displayName = displayName;
        this.lore = lore;
        this.skullOwner = skullOwner;
        this.slot = slot;
    }
    public AtherialLibItem setLore(String... lore) {
        this.lore = new ArrayList<>();
        for (String s : lore) {
            this.lore.add(s);
        }
        return this;
    }

    public AtherialLibItem setLore(String lore) {
        this.lore = new ArrayList<>();
        this.lore.add(lore);
        return this;
    }

    public AtherialLibItem setData(int data) {
        this.data = data;
        return this;
    }

    public AtherialLibItem() {
    }
    public AtherialLibItem(ItemStack itemStack) {
        this.slot = -1;

        this.amount = itemStack.getAmount();

        this.type=itemStack.getType();
//        this.data = itemStack.getData().getData();

        if (itemStack.hasItemMeta()){
            ItemMeta itemMeta = itemStack.getItemMeta();
            this.lore=itemMeta.hasLore()?itemMeta.getLore():new ArrayList<>();
            this.displayName =itemMeta.hasDisplayName()?itemMeta.getDisplayName():null;
        }
    }

    public int getSlot() {
        return slot;
    }
    public ItemStack build() {

        return build(ChatUtils::colorize);
    }
    public ItemStack build(StringReplacer stringReplacer) {
        ItemStack itemStack=null;
        amount = 1;
        if (data!=-1){
            itemStack = new ItemStack(type, amount, (short) data);
        } else {

            itemStack = new ItemStack(type, amount);
        }

        ItemMeta itemMeta=itemStack.getItemMeta();

        if (displayName!=null){
            itemMeta.setDisplayName(stringReplacer.replace(new String(displayName)));
        }
        if (lore!=null&&!lore.isEmpty()){
            List<String> newLore = new ArrayList<>();
            for (String s : lore) {
                newLore.add(stringReplacer.replace(new String(s)));
            }
            if (!newLore.isEmpty()){
                itemMeta.setLore(newLore);
            }
        }
        itemStack.setItemMeta(itemMeta);
        if (skullOwner!=null){
            return new ItemBuilder(itemStack).skullOwner(skullOwner).build();
        }
        return itemStack;
    }


    public AtherialLibItem setType(Material type) {
        this.type = type;
        return this;
    }

    public AtherialLibItem setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public AtherialLibItem setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public AtherialLibItem setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public AtherialLibItem setSkullOwner(String skullOwner) {
        this.skullOwner = skullOwner;
        return this;
    }

    public Material getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    public String getSkullOwner() {
        return skullOwner;
    }

    public AtherialLibItem setSlot(int slot) {
        this.slot = slot;
        return this;
    }
}
