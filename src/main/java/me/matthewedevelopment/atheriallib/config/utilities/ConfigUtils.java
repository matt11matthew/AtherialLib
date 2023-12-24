package me.matthewedevelopment.atheriallib.config.utilities;

import me.matthewedevelopment.atheriallib.config.Config;
import me.matthewedevelopment.atheriallib.config.IgnoreValue;
import me.matthewedevelopment.atheriallib.config.SerializedName;
import me.matthewedevelopment.atheriallib.io.StringReplacer;
import me.matthewedevelopment.atheriallib.item.AtherialItemBuilder;
import me.matthewedevelopment.atheriallib.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static me.matthewedevelopment.atheriallib.utilities.ChatUtils.colorize;

/**
 * Created by Matthew E on 12/6/2023 at 10:13 PM for the project AtherialLib
 */
public class ConfigUtils {
    public  static ItemBuilder getItemBuilder(ConfigurationSection section, StringReplacer stringReplace) {
        Material type =  Material.valueOf(section.getString("material", "AIR"));
        String name = section.getString("name", null);
        List<String> lore =new ArrayList<>();
        if (section.isList("lore")){

            for (String s : section.getStringList("lore")) {
                if (stringReplace!=null){
                    lore.add(stringReplace.replace(new String(s)));

                } else {

                    lore.add(new String(s));
                }
            }
        } else if (section.isString("lore")){
            if (stringReplace!=null){
                lore.add(stringReplace.replace(section.getString("lore")));

            } else {

                lore.add(new String(section.getString("lore")));
            }
        }
        ItemBuilder builder = new ItemBuilder(type);

        if (name!=null) {
            if (stringReplace!=null){
                builder=builder.setName(stringReplace.replace(colorize(name)));
            } else {
                builder=builder.setName(colorize(name));

            }
        }
        if (!lore.isEmpty())builder=builder.addLore(lore);
        return builder;
    }

    public  static ItemBuilder getItemBuilder(ConfigurationSection section) {
        return getItemBuilder(section, null);
    }




//        public AtherialItemBuilder.Builder getAtherialItemBuilder(String path) {
//            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(getFile());
//            ConfigurationSection configurationSection = fileConfiguration.getConfigurationSection(path);
//            String type = configurationSection.getString("type");
//            Material material;
//            try {
//                material = Material.getMaterial(type);
//            } catch (Exception e) {
//                return null;
//            }
//            int amount = configurationSection.getInt("amount", 1);
//            int durability = configurationSection.getInt("durability", 0);
//            String displayName = configurationSection.getString("displayName", "");
//            List<String> loreStringList = new ArrayList<>();
//            if (configurationSection.isSet("lore") && configurationSection.isList("lore")) {
//                loreStringList = configurationSection.getStringList("lore");
//            }
//
//            return AtherialItemBuilder.builder()
//                    .type(material)
//                    .displayName(displayName)
//                    .lore(loreStringList)
//                    .durability((short) durability)
//                    .amount(amount);
//
//        }



}
