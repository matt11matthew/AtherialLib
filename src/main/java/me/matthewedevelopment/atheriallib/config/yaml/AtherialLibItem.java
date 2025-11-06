package me.matthewedevelopment.atheriallib.config.yaml;

import lombok.Getter;
import lombok.Setter;
import me.matthewedevelopment.atheriallib.config.yaml.serializables.AtherialLibItemSerializable;
import me.matthewedevelopment.atheriallib.config.yaml.serializables.list.IntSimpleList;
import me.matthewedevelopment.atheriallib.dependency.headdatabase.HeadDatabaseDependency;
import me.matthewedevelopment.atheriallib.io.StringReplacer;
import me.matthewedevelopment.atheriallib.utilities.AtherialTranslationMessage;
import me.matthewedevelopment.atheriallib.utilities.ChatUtils;
import me.matthewedevelopment.atheriallib.utilities.DisplayNameUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import spigui.item.ItemBuilder;

import java.util.*;

/**
 * Created by Matthew E on 12/23/2023 at 10:19 PM for the project AtherialLib
 */
public class AtherialLibItem {
    private Material type;
    private int amount;
    private String displayName;
    private List<String> lore;
    private List<String> commands = new ArrayList<>();
    private int modelId = 0;
    private Map<String, Integer> enchantments;
    private String headDatabaseHead;
    private String tooltipStyle;

    @Getter
    private String customModel;
    private IntSimpleList multiSlots = null;

    public AtherialLibItem setCustomModel(String customModel) {
        this.customModel = customModel;
        return this;
    }
   public AtherialLibItem setItemModel(String customModel) {
        this.customModel = customModel;
        return this;
    }

    @Getter
    @Setter
    private boolean snakeCase;

    public int getModelId() {
        return modelId;
    }

    public AtherialLibItem setModelId(int modelId) {
        this.modelId = modelId;
        return this;
    }

    public int getData() {
        return data;
    }

    public Map<String, Integer> getEnchantments() {
        return enchantments;
    }

    public static AtherialLibItem of(ConfigurationSection section, String key) {
        if (!section.isSet(key)) return null;
        MemorySection memorySection = (MemorySection) section.get(key);
        return new AtherialLibItemSerializable().deserializeComplex(memorySection.getValues(false));
    }

    public AtherialLibItem setHeadDatabaseHead(String headDatabaseHead) {
        this.headDatabaseHead = headDatabaseHead;
        return this;
    }

    public String getHeadDatabaseHead() {
        return headDatabaseHead;
    }

    private int data = -1;

    private String skullOwner;

    private int slot = -1;

    public AtherialLibItem(Material type, int amount, String displayName, List<String> lore, String skullOwner, int slot, Map<String, Integer> enchantments) {
        this.type = type;
        this.amount = amount;
        this.displayName = displayName;
        this.lore = lore;
        this.skullOwner = skullOwner;
        this.slot = slot;
        this.enchantments = enchantments;
    }

    public List<String> getCommands() {
        return commands;
    }

    public AtherialLibItem setCommands(List<String> commands) {
        this.commands = commands;
        return this;
    }

    public AtherialLibItem setLore(String... lore) {
        this.lore = new ArrayList<>();
        for (String s : lore) {
            this.lore.add(s);
        }
        return this;
    }

    public AtherialLibItem setMultiSlots(IntSimpleList multiSlots) {
        this.multiSlots = multiSlots;
        return this;
    }

    public AtherialLibItem addEnchantment(Enchantment enchantment, int level) {
        if (this.enchantments == null) {
            this.enchantments = new HashMap<>();
        }
        this.enchantments.put(enchantment.getName().toUpperCase(), level);
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

    public AtherialLibItem setTooltipStyle(String tooltipStyle) {
        this.tooltipStyle = tooltipStyle;
        return this;
    }

    public String getTooltipStyle() {
        return tooltipStyle;
    }

    public AtherialLibItem(AtherialLibItem clone) {
        this.type = clone.type;
        this.displayName = clone.displayName;
        this.lore = clone.lore;
        this.amount = clone.amount;
        this.data = clone.data;
        this.skullOwner = clone.skullOwner;
        this.slot = clone.slot;
        this.enchantments = clone.enchantments;
        this.headDatabaseHead = clone.headDatabaseHead;
        this.modelId = clone.modelId;
        this.commands = clone.commands;

        this.customModel = clone.customModel;

        this.tooltipStyle = clone.tooltipStyle;
        this.snakeCase = clone.snakeCase;
        this.multiSlots = clone.multiSlots;

    }

    /*
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
     */
    public AtherialLibItem() {
    }

    public AtherialLibItem(ItemStack itemStack) {
        this.slot = -1;

        this.amount = itemStack.getAmount();

        this.type = itemStack.getType();
//        this.data = itemStack.getData().getData();

        if (itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            this.lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
            this.displayName = itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : null;
        }
        if (!itemStack.getEnchantments().isEmpty()) {
            this.enchantments = new HashMap<>();
            for (Map.Entry<Enchantment, Integer> entry : itemStack.getEnchantments().entrySet()) {
                enchantments.put(entry.getKey().getName().toUpperCase(), entry.getValue());
            }
        }
    }


    public int getSlot() {
        return slot;
    }

    public ItemStack build() {

        return build(ChatUtils::colorize);
    }

    public ItemStack build(StringReplacer stringReplacer, boolean allFlags) {
        ItemStack build = build(stringReplacer);
        if (!allFlags) return build;
        ItemMeta itemMeta = build.getItemMeta();

        itemMeta.addItemFlags(ItemFlag.values());
        build.setItemMeta(itemMeta);
        return build;
    }

    public ItemStack build(StringReplacer stringReplacer) {
        ItemStack itemStack = null;
        if (amount < 1) {
            amount = 1;
        }
        if (headDatabaseHead != null && HeadDatabaseDependency.get() != null) {
            itemStack = HeadDatabaseDependency.get().createHead(headDatabaseHead);
        } else {

            if (data != -1) {
                itemStack = new ItemStack(type, amount, (short) data);
            } else {

                itemStack = new ItemStack(type, amount);
            }
        }


        ItemMeta itemMeta = itemStack.getItemMeta();

        if (displayName != null) {
            displayName = displayName.replaceAll("<center>", "").replaceAll("</center>", "");
            Component component = new AtherialTranslationMessage(stringReplacer.replace(displayName)).toComponent(stringReplacer);
            if (component!=null) {

                component = component.decoration(TextDecoration.ITALIC, false);
                boolean b = DisplayNameUtil.tryAdventureSetter(itemMeta, component);
                if (!b) {
                    itemMeta.setDisplayName(ChatUtils.colorize(stringReplacer.replace(displayName)));
                }
            } else {
                itemMeta.setDisplayName(ChatUtils.colorize(stringReplacer.replace(displayName)));

            }
        }
        if (lore != null && !lore.isEmpty()) {
            List<String> newLore = new ArrayList<>();
            List<Component> newLoreComp = new ArrayList<>();
            for (String line : lore) {


                List<String> toLoop = new ArrayList<>();
                if (line.contains("\n")) {
                    toLoop.addAll(Arrays.asList(line.split("\n")));
                } else {
                    toLoop.add(line);
                }

                for (String s : toLoop) {

                    s = s.replaceAll("<center>", "").replaceAll("</center>", "");
                    newLore.add(stringReplacer.replace(new String(s)));

                    Component component = new AtherialTranslationMessage(s).toComponent(stringReplacer);
                    if (component!=null) {

                        component = component.decoration(TextDecoration.ITALIC, false);
                        newLoreComp.add(component);
                    } else {
                        newLoreComp.add(Component.empty());

                    }
                }
            }
            if (!newLoreComp.isEmpty()) {

                boolean b = DisplayNameUtil.tryAdventureLoreSetter(itemMeta, newLoreComp);
                if (!b) {

                    if (!newLore.isEmpty()) {
                        itemMeta.setLore(newLore);
                    }
                }
            } else {

                if (!newLore.isEmpty()) {
                    itemMeta.setLore(newLore);
                }
            }
        }
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
        if (this.modelId != 0) {
            itemMeta.setCustomModelData(modelId);

        }
        itemStack.setItemMeta(itemMeta);

        if (enchantments != null && !enchantments.isEmpty()) {
            for (String s : enchantments.keySet()) {
                Enchantment byName = Enchantment.getByName(s.toLowerCase());
                if (byName == null) continue;

                itemStack.addUnsafeEnchantment(byName, enchantments.get(s));

            }
        }

        if (customModel != null && !customModel.isEmpty()) {
            itemStack = CustomItemUtil.applyCustomItem(itemStack, customModel,null);

        }
        if (tooltipStyle != null && !tooltipStyle.isEmpty()) {
            itemStack = CustomItemUtil.applyCustomItem(itemStack, null, tooltipStyle);

        }
        if (skullOwner != null) {
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

    public IntSimpleList getMultiSlots() {
        return multiSlots;
    }

    public String getSkullOwner() {
        return skullOwner;
    }

    public AtherialLibItem setSlot(int slot) {
        this.slot = slot;
        return this;
    }
}
