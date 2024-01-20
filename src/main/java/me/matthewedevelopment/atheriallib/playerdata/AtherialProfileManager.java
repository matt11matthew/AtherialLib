package me.matthewedevelopment.atheriallib.playerdata;

import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.database.mysql.MySqlHandler;
import me.matthewedevelopment.atheriallib.io.Callback;
import me.matthewedevelopment.atheriallib.playerdata.db.DatabaseTableManager;
import me.matthewedevelopment.atheriallib.playerdata.db.MySQLDatabaseTableManager;
import me.matthewedevelopment.atheriallib.playerdata.db.SQLiteDatabaseTableManager;
import me.matthewedevelopment.atheriallib.utilities.AtherialTasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Matthew E on 12/5/2023 at 9:50 PM for the project AtherialLib
 */
public class AtherialProfileManager  implements Listener {
    private AtherialLib atherialLib;
    private Map<String, Class<? extends AtherialProfile>> profiles;

//    private BukkitConfig bukkitConfig;
    private Map<String, Map<UUID, AtherialProfile<?>>> playerDataMap;

    private me.matthewedevelopment.atheriallib.playerdata.db.DatabaseTableManager databaseTableManager;

//    public BukkitConfig getBukkitConfig() {
//        return bukkitConfig;
//    }

    public AtherialProfileManager(AtherialLib atherialLib) {
        this.profiles = new HashMap<>();
        this.atherialLib = atherialLib;
        this.playerDataMap  = new HashMap<>();

    }

    public DatabaseTableManager getDatabaseTableManager() {
        return databaseTableManager;
    }

    /*
        T t = clazz.getConstructor(UUID.class, String.class, boolean.class).newInstance(uniqueId, player.getName(), true);
                    T deserialize = t.deserialize(jsonObject);
         */
    public void load() {
//        if (!profiles.isEmpty()){
//            this.bukkitConfig = new BukkitConfig("players.yml", this.atherialLib);
//        }

        if (atherialLib.getSqlHandler().isLite()){
            databaseTableManager=new SQLiteDatabaseTableManager();
        } else {
            databaseTableManager=new MySQLDatabaseTableManager();
        }
        atherialLib.getLogger().info("Loading profiles...");
        List<Class<? extends AtherialProfile>> profileClazzes = atherialLib.getProfileClazzes();
        for (Class<? extends AtherialProfile> profileClazz : profileClazzes) {
            registerProfileClass((Class<? extends AtherialProfile<?>>) profileClazz);
        }
        for (Class<? extends AtherialProfile> value : profiles.values()) {
            atherialLib.getLogger().info("Loading " + value.getSimpleName() + " profile system....");
            this.playerDataMap.put(value.getSimpleName(), new HashMap<>());
            for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                loadDataSync(value, onlinePlayer);
            }
        }
//        if (bukkitConfig!=null){
//
//            bukkitConfig.saveConfiguration();
//        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(atherialLib, () -> {
            if (profiles.isEmpty())return;
            for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                for (Class<? extends AtherialProfile> value : profiles.values()) {
                    AtherialProfile profile = getProfile(value, onlinePlayer);
                    if (profile!=null){
                        AtherialTasks.runAsync(() ->   {
                            profile.preSave(onlinePlayer, PreSaveType.INTERVAL);
                            profile.saveToDatabaseSync(getConnection());
                        });
//                        saveData(onlinePlayer, profile, false);
                    }
                }
            }
//            if (bukkitConfig!=null){
//
//
//                bukkitConfig.saveConfiguration();
//            }
        }, 20*30, 20*30);
    }

    public void stop(){

    }




    public Connection getConnection() {
        MySqlHandler sqlHandler = AtherialLib.getInstance().getSqlHandler();
       return sqlHandler.getConnection();
//        return null;
    }

    public void loadDataASync(Class<? extends AtherialProfile> value, Player player, Callback<AtherialProfile> atherialProfileCallback) {
        AtherialTasks.runAsync(() -> {
            AtherialProfile atherialProfile = loadDataSync(value, player);
            atherialProfileCallback.call(atherialProfile);
        });
    }

    private AtherialProfile loadDataSync(Class<? extends AtherialProfile> value, Player player) {



        String simpleName = value.getSimpleName();
        AtherialProfile returnProfile;
        try {
            AtherialProfile atherialProfile = value.getConstructor(UUID.class, String.class).newInstance(player.getUniqueId(), player.getName());
            returnProfile = atherialProfile.loadSync(player, getConnection());
            playerDataMap.get(simpleName).put(player.getUniqueId(), returnProfile);

            AtherialTasks.runSync(() -> {
                AtherialProfileLoadEvent customEvent = new AtherialProfileLoadEvent(player,returnProfile);
                Bukkit.getServer().getPluginManager().callEvent(customEvent);
            });
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
            AtherialTasks.runSync(() -> {

                AtherialProfileQuitEvent atherialProfileQuitEvent = new AtherialProfileQuitEvent(event.getPlayer(),profile);
                Bukkit.getPluginManager().callEvent(atherialProfileQuitEvent);
            });

            profile.preSave(event.getPlayer(), PreSaveType.QUIT);
           AtherialTasks.runAsync(() -> { profile.saveToDatabaseSync(getConnection());});
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
            loadDataASync(value, event.getPlayer(),atherialProfile -> {});

        }

    }

    public void registerProfileClass(Class<? extends AtherialProfile<?>> clazz) {
        if (profiles.containsKey(clazz.getSimpleName())){
            return;
        }
        profiles.put(clazz.getSimpleName(), clazz);
        try {
            AtherialProfile<?> atherialProfile = clazz.newInstance();
            String databaseName = AtherialLib.getInstance().getSqlHandler().getDatabaseName();

            Connection connection = AtherialLib.getInstance().getProfileManager().getConnection();
            String key = atherialProfile.getKey();
            System.out.println(databaseName+"."+key);
          databaseTableManager.createOrUpdateTable(connection,  key,atherialProfile.getColumns());
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    public static boolean createdTable = false;


}
