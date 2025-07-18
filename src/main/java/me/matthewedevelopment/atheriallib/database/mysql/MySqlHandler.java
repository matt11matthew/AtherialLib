package me.matthewedevelopment.atheriallib.database.mysql;

import me.matthewedevelopment.atheriallib.AtherialLib;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Matthew E on 12/13/2023 at 8:34 PM for the project AtherialLib
 */
public class MySqlHandler {
    private AtherialLib atherialLib;
    private boolean enabled;
    private MySQLConfig config;

    public boolean isLite() {
        return lite;
    }

    private boolean lite =false;
//    private MysqlDataSource dataSource = null;

//    private Connection connection;

    public void setLite(boolean lite) {
        this.lite = lite;
    }

    private Connection connection;
//    public DataSource getDataSource() {
//        if (dataSource == null) {
////            hikari.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
////            dataSource= new com.mysql.cj.jdbc.MysqlDataSource();
//            dataSource = new MysqlDataSource();
//            dataSource.setURL(this.config.getDriverString());
//            dataSource.setUser(this.config.getUsername());
//            dataSource.setPassword(this.config.getPassword());
//
//
//            // Optional: Configure additional properties
//            dataSource.setCachePrepStmts(true);
//            dataSource.setPrepStmtCacheSize(250);
//            dataSource.setPrepStmtCacheSqlLimit(2048);
//            dataSource.setUseServerPrepStmts(true);
//            dataSource.setUseLocalSessionState(true);
//        }
//        return dataSource;
//    }


    public void start() {
        if (this.enabled) {

            this.config = new MySQLConfig(atherialLib);
            this.config.load(this);
            try {
                if (isLite()){
                    createLiteDB();
                } else {
                    if (AtherialLib.getInstance().isDisableSQLLogin()){
                        this.atherialLib.getLogger().info("[SQLHandler #1] Attempting to connect with "+   config.getDriverString());

//                        connection = DriverManager.getConnection(config.getDriverString());
                        connection = DriverManager.getConnection(config.getDriverString(), config.getUsername(), config.getPassword());
                    } else {
                        this.atherialLib.getLogger().info("[SQLHandler #2] Attempting to connect with "+   config.getDriverString());
                        connection = DriverManager.getConnection(config.getDriverString(), config.getUsername(), config.getPassword());

                    }
                }


            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (lite){

                    this.atherialLib.getLogger().info("[SQLHandler] SQLite Connected!");
                } else {
                    this.atherialLib.getLogger().info("[SQLHandler] MySQL Connected!");

                }
            }
        }

    }




//    public void executePlayerUpdateAsync(String sql, UUID uuid, Callback<Boolean> onComplete) {
//        AtherialTasks.runAsync(() -> {
//            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
//
//                pstmt.setString(1, uuid.toString());
//
//                int affectedRows = pstmt.executeUpdate();
//                if (affectedRows > 0) {
//                    onComplete.call(true);
//                } else {
//                    onComplete.call(false);
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//                onComplete.call(false);
//                // Handle exception or rethrow it as needed
//            }
//        });
//    }

    public  void createLiteDB() {
//        Connection connection = null;
        try {
            // Create a directory for the database file if it doesn't exist
            File dbDirectory = new File(AtherialLib.getInstance().getDataFolder()+"/databases"); // Change to your desired directory
            if (!dbDirectory.exists()) {
                dbDirectory.mkdirs();
            }

            // Construct the JDBC URL for the database file
            String url = "jdbc:sqlite:" + dbDirectory.getAbsolutePath() + File.separator + "atherial.db";

            // Create a connection to the database
            connection = DriverManager.getConnection(url);

            if (connection != null) {
                AtherialLib.getInstance().getLogger().info("SQLite database created: " + url);

                // You can execute SQL statements or create tables here if needed

                // Close the connection when done
//                connection.close();
            } else {
                AtherialLib.getInstance().getLogger().severe("Failed to create the SQLite database.");
            }
        } catch (SQLException e) {
            AtherialLib.getInstance().getLogger().severe("SQL Exception: " + e.getMessage());
        }
    }
    public Connection getConnection() {
        try {
            if ((connection == null) || connection.isClosed()) {
                start();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void stop() {
        if (enabled && connection!=null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public MySqlHandler(AtherialLib atherialLib) {
        this.atherialLib = atherialLib;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }


    public String getDatabaseName() {
        return config.getDatabase();
    }
}
