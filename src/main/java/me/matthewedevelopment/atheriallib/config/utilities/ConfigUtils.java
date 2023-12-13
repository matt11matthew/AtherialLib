package me.matthewedevelopment.atheriallib.config.utilities;

import me.matthewedevelopment.atheriallib.io.StringReplacer;
import me.matthewedevelopment.atheriallib.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

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


}
