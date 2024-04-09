package me.matthewedevelopment.atheriallib.item;

import org.bukkit.Material;

/**
 * Created by Matthew E on 1/1/2024 at 8:15 PM for the project ZerianEnchantments
 */
public enum ToolType {
    PICKAXE, SHOVEL, HOE, SWORD, AXE, BOW, HELMET, CHESTPLATE, LEGGINGS, BOOTS, FISHING_ROD,
    CROSSBOW, SHEARS,FLINT_AND_STEEL,ELYTRA,SHIELD,CARROT_ON_A_STICK, ARMOR, WEAPON;


    ToolType() {

    }

    public static ToolType getByMaterial(Material material) {
        String text = new String(material.toString())
                .replace("WOODEN_","")
                .replace("WOOD_","")
                .replace("STONE_","")
                .replace("GOLD_", "")
                .replace("GOLDEN_","")
                .replace("LEATHER_","")
                .replace("CHAINMAIL_", "")
                .replace("SPADE", "SHOVEL")
                .replace("IRON_","")
                .replace("NETHERITE_","")
                .replace("DIAMOND_","").trim();
        for (ToolType value : values()) {
            if (material.toString().replace("SPADE", "SHOVEL").equals(value.toString())){
                return value;
            }
            if (text.equals(value.toString())) {
                return value;
            }
        }
        return null;
    }

    public boolean matches(Material type) {
        ToolType byMaterial = getByMaterial(type);
        if( byMaterial==null)return false;
        if (this==ARMOR&&byMaterial.isArmor())return true;
        if (this==WEAPON&&byMaterial.isWeapon())return true;
        return byMaterial==this;

    }

    public boolean isWeapon() {
        return this==SWORD||this==AXE||this==CROSSBOW||this==BOW||this==WEAPON;
    }
    public boolean isArmor() {
        return this==HELMET||this==CHESTPLATE||this==LEGGINGS||this==BOOTS||this==ARMOR;
    }
}