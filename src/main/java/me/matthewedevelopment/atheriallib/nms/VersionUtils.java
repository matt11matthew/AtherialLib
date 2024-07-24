package me.matthewedevelopment.atheriallib.nms;

import org.bukkit.Bukkit;

public class VersionUtils {
    public static String getVersionForLatest() {
        try {
            return Bukkit.getVersion();
        } catch (Exception e) {
            return null; // OLD VERSION
        }
    }
}
