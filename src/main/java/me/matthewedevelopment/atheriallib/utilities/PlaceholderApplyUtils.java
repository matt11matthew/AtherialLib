package me.matthewedevelopment.atheriallib.utilities;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

/**
 * Created by Matthew E on 6/28/2024 at 9:42 PM for the project AtherialLib
 */
public class PlaceholderApplyUtils {
    public static String applyPapi(String text, Player p) {
       return PlaceholderAPI.setPlaceholders(p,text);
    }
}
