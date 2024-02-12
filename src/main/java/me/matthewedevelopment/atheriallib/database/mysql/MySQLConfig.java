package me.matthewedevelopment.atheriallib.database.mysql;

import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.config.BukkitConfig;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Created by Matthew E on 12/13/2023 at 8:27 PM for the project AtherialLib
 */
public class MySQLConfig {
    private BukkitConfig bukkitConfig;
    private String driver;
    private String host;
    private int port;
    private String username;
    private String password;
    private String database;


    private AtherialLib lib;

    private String SET_URL = null;

    public void setSET_URL(String SET_URL) {
        this.SET_URL = SET_URL;
    }

    public String getSET_URL() {
        return SET_URL;
    }

    public MySQLConfig(AtherialLib atherialLib) {
        this.lib = atherialLib;
    }
// this.connection = DriverManager.getConnection("jdbc:mysql://" + this.config.database.host.address + ":" + this.config.database.host.port + "/" + this.config.database.database, this.config.database.auth.username, this.config.database.auth.password);

    public void load(MySqlHandler mySqlHandler) {
        this.bukkitConfig = new BukkitConfig("database.yml", lib);
        if (bukkitConfig.getConfiguration().getKeys(false).isEmpty()){
            FileConfiguration configuration = bukkitConfig.getConfiguration();
            if (mySqlHandler.isLite()){
                configuration.set("driver", "lite");

            } else {

                configuration.set("driver", "mysql");
                configuration.set("database", "atherial");
                configuration.set("host.address", "localhost");
                configuration.set("host.port",3306);
                configuration.set("auth.username", "matthew");
                configuration.set("auth.password", "matthew");
            }
            this.bukkitConfig.setConfiguration(configuration);
            this.bukkitConfig.saveConfiguration();
        }

        FileConfiguration config = bukkitConfig.getConfiguration();
        this.driver = config.getString("driver");
        if (driver.equalsIgnoreCase("lite")){
            mySqlHandler.setLite(true);
        } else {
            mySqlHandler.setLite(false);
            this.host = config.getString("host.address");
            this.port = config.getInt("host.port");
            this.database = config.getString("database");
            this.username = config.getString("auth.username");
            this.password = config.getString("auth.password");
        }
    }

    public String getDatabase() {
        return database;
    }

    public String getDriverString(){
        if (AtherialLib.getInstance().getSqlURL()!=null){
            return AtherialLib.getInstance().getSqlURL();
        }
        if (getSET_URL()!=null)return getSET_URL();
        return "jdbc:"+driver+"://"+host+":"+port+"/"+database +"?autoReconnect=true";

    }
    public BukkitConfig getBukkitConfig() {
        return bukkitConfig;
    }

    public String getDriver() {
        return driver;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public AtherialLib getLib() {
        return lib;
    }

    public boolean isLite() {
        return driver!=null&&driver.equalsIgnoreCase("lite");
    }
}
