package me.matthewedevelopment.atheriallib.playerdata;

import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.config.BukkitConfig;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Matthew E on 12/5/2023 at 9:50 PM for the project AtherialLib
 */
public class AtherialProfileManager  implements Listener {
    private AtherialLib atherialLib;
    private Map<String, Class<? extends AtherialProfile>> profiles;

    private BukkitConfig bukkitConfig;
    private Map<String, Map<UUID, AtherialProfile<?>>> playerDataMap;

    public BukkitConfig getBukkitConfig() {
        return bukkitConfig;
    }

    public AtherialProfileManager(AtherialLib atherialLib) {
        this.profiles = new HashMap<>();
        this.atherialLib = atherialLib;
        this.playerDataMap  = new HashMap<>();

    }


    /*
    T t = clazz.getConstructor(UUID.class, String.class, boolean.class).newInstance(uniqueId, player.getName(), true);
                T deserialize = t.deserialize(jsonObject);
     */
    public void load() {
        if (!profiles.isEmpty()){
            this.bukkitConfig = new BukkitConfig("players.yml", this.atherialLib);
        }
        atherialLib.getLogger().info("Loading profiles...");
        for (Class<? extends AtherialProfile> value : profiles.values()) {
            atherialLib.getLogger().info("Loading " + value.getSimpleName() + " profile system....");
            this.playerDataMap.put(value.getSimpleName(), new HashMap<>());
            for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                loadData(value, onlinePlayer);
            }
        }
        if (bukkitConfig!=null){

            bukkitConfig.saveConfiguration();
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(atherialLib, () -> {
            for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                for (Class<? extends AtherialProfile> value : profiles.values()) {
                    AtherialProfile profile = getProfile(value, onlinePlayer);
                    if (profile!=null){
                        saveData(onlinePlayer, profile, false);
                    }
                }
            }
            if (bukkitConfig!=null){


                bukkitConfig.saveConfiguration();
            }
        }, 20*5, 20*5);
    }

    public void stop(){
        if (bukkitConfig!=null){

            bukkitConfig.saveConfiguration();
        }
    }


    public void saveData(Player player, AtherialProfile atherialProfile, boolean save){
        atherialProfile.save(player, this);
        if (bukkitConfig!=null &&save){

            bukkitConfig.saveConfiguration();
        }
    }
    private AtherialProfile loadData(Class<? extends AtherialProfile> value, Player player) {



        String simpleName = value.getSimpleName();
        AtherialProfile returnProfile;
        try {
            AtherialProfile atherialProfile = value.getConstructor(UUID.class, String.class).newInstance(player.getUniqueId(), player.getName());
            returnProfile = atherialProfile.load(player, this);
            playerDataMap.get(simpleName).put(player.getUniqueId(), returnProfile);

            AtherialProfileLoadEvent customEvent = new AtherialProfileLoadEvent(player,returnProfile);
            Bukkit.getServer().getPluginManager().callEvent(customEvent);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return returnProfile;

    }
    public <T extends AtherialProfile<T>>  T getProfile(Class<T> clazz, Player player) {
        return getProfile(clazz, player.getUniqueId());
    }
    public <T extends AtherialProfile<T>>  T getProfile(Class<T> clazz, UUID uuid) {
        if (!profiles.containsKey(clazz.getSimpleName())){
            return null;
        }
        if( !playerDataMap.containsKey(clazz.getSimpleName())){
            return null;
        }
        Map<UUID, AtherialProfile<?>> uuidAtherialProfileMap = playerDataMap.get(clazz.getSimpleName());
        if (uuidAtherialProfileMap==null)return null;
        return (T) uuidAtherialProfileMap.get(uuid);
    }
    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        if (profiles.isEmpty()){
            return;
        }
        for (Class<? extends AtherialProfile> value : profiles.values()) {
            AtherialProfile profile = getProfile(value, event.getPlayer());
            if (profile == null) {
                System.out.println("ERROR COULD NOT FIND PROFILE");
                return;
            }
            saveData(event.getPlayer(), profile,true);
            playerDataMap.get(value.getSimpleName()).remove(event.getPlayer().getUniqueId());
            System.out.println("Removed player data for " + event.getPlayer().getName() );
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (profiles.isEmpty()){
            return;
        }
        for (Class<? extends AtherialProfile> value : profiles.values()) {
            loadData(value, event.getPlayer());

        }

    }

    public void registerProfileClass(Class<? extends AtherialProfile<?>> clazz) {
        if (profiles.containsKey(clazz.getSimpleName())){
            return;
        }
        profiles.put(clazz.getSimpleName(), clazz);
    }
}
