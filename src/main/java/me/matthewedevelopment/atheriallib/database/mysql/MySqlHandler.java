package me.matthewedevelopment.atheriallib.database.mysql;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.io.Callback;
import me.matthewedevelopment.atheriallib.utilities.AtherialTasks;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by Matthew E on 12/13/2023 at 8:34 PM for the project AtherialLib
 */
public class MySqlHandler {
    private AtherialLib atherialLib;
    private boolean enabled;
    private MySQLConfig config;

    private MysqlDataSource dataSource = null;

        private Connection connection;
    public DataSource getDataSource() {
        if (dataSource == null) {
            dataSource = new MysqlDataSource();
            dataSource.setURL(this.config.getDriverString());
            dataSource.setUser(this.config.getUsername());
            dataSource.setPassword(this.config.getPassword());

            // Optional: Configure additional properties
            dataSource.setCachePreparedStatements(true);
            dataSource.setPrepStmtCacheSize(250);
            dataSource.setPrepStmtCacheSqlLimit(2048);
            dataSource.setUseServerPrepStmts(true);
            dataSource.setUseLocalSessionState(true);
        }
        return dataSource;
    }


    public void start() {
        if (this.enabled) {
            this.config = new MySQLConfig(atherialLib);
            this.config.load();
            try {
                DataSource dataSource1 = getDataSource();

                connection = dataSource1.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                this.atherialLib.getLogger().info("[MySQLHandler] MySQL Connected!");
            }
        }

    }

    public void executePlayerUpdateAsync(String sql, UUID uuid, Callback<Boolean> onComplete) {
        AtherialTasks.runAsync(() -> {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

                pstmt.setString(1, uuid.toString());

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    onComplete.call(true);
                } else {
                    onComplete.call(false);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                onComplete.call(false);
                // Handle exception or rethrow it as needed
            }
        });
    }

    public Connection getConnection() {
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


}
