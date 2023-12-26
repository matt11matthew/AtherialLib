package me.matthewedevelopment.atheriallib.playerdata;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by Matthew E on 12/5/2023 at 9:49 PM for the project AtherialLib
 */
public abstract class AtherialProfile<T extends AtherialProfile<T>> {

    private UUID uuid;

    private String username;


    public AtherialProfile(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    public abstract String getKey();



    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public abstract T loadDefault(Player player);
    public T load(Player player, AtherialProfileManager profileManager) {

        if (!profileManager.getBukkitConfig().getConfiguration().isConfigurationSection(getKey()+"." + uuid.toString())){
            T t = loadDefault(player);
            t.save(player, profileManager);
            return t;
        }

        ConfigurationSection section1 = profileManager.getBukkitConfig().getConfiguration().getConfigurationSection(getKey() + "." + uuid.toString());
        this.username = section1.getString("username");

        return (T) loadProfile(profileManager);


    }



    public void save(Player player, AtherialProfileManager profileManager){
        profileManager.getBukkitConfig().getConfiguration().set(getKey() + "."+uuid.toString()+".username", player.getName());
        saveProfile(profileManager);
    }

    public String getPlayerPath() {
        return getKey()+"."+uuid.toString();
    }

    public abstract T loadProfile(AtherialProfileManager section);
    public abstract void saveProfile(AtherialProfileManager profileManager);
}
