package me.matthewedevelopment.atheriallib.database.keydata;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.database.mysql.MySqlHandler;
import me.matthewedevelopment.atheriallib.database.registry.DataColumn;
import me.matthewedevelopment.atheriallib.database.registry.DataObject;
import me.matthewedevelopment.atheriallib.database.registry.db.DatabaseTableManagerCustomData;
import me.matthewedevelopment.atheriallib.database.registry.db.MySQLDatabaseTableManagerCustom;
import me.matthewedevelopment.atheriallib.database.registry.db.SQLiteDatabaseTableManagerCustom;
import me.matthewedevelopment.atheriallib.utilities.AtherialTasks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class DataKeyObjectRegistry<K extends DataKeyObject.DataKey<?>, T extends DataKeyObject<K, T>> {
    protected Map<K, T> map;
    private final Class<T> clazz;
    private DatabaseTableManagerCustomData databaseTableManager;
    private String tableName = null;

    @Data
    @AllArgsConstructor
    public static class Tuple<A, B> {
        public A a;
        public B b;
    }

    public Tuple<Boolean, T> preRegister() {
        if (AtherialLib.getInstance().getSqlHandler().isLite()) {
            databaseTableManager = new SQLiteDatabaseTableManagerCustom();
        } else {
            databaseTableManager = new MySQLDatabaseTableManagerCustom();
        }
        T temp;
        try {
            temp = clazz.newInstance();
            tableName = temp.getTableName();
            AtherialLib.getInstance().getLogger().info("TABLE NAME: (" + tableName + ")");
            databaseTableManager.createOrUpdateTable(getConnection(), tableName, temp.getColumns());
        } catch (InstantiationException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
            return new Tuple<>(false, null);
        }
        return new Tuple<>(true, temp);
    }

    public void register() {
        Tuple<Boolean, T> res = preRegister();
        if (res != null && res.a && res.getB() != null) {
            loadAllSync(res.getB());
        }
        onRegister();
    }

    public DataKeyObjectRegistry(Class<T> clazz) {
        this.clazz = clazz;
        this.map = new HashMap<>();
    }

    public abstract void onRegister();

    public void loadAllSync(T tempInstance) {
        AtherialTasks.runSync(() -> {
            List<T> ts = tempInstance.loadAllSync(getConnection());
            for (T t1 : ts) {
                map.put(t1.getId(), t1);
            }
            onLoadAll(ts);
        });
    }

    public Connection getConnection() {
        MySqlHandler sqlHandler = AtherialLib.getInstance().getSqlHandler();
        return sqlHandler.getConnection();
    }

    public void updateAsync(T t, Runnable runnable) {
        AtherialTasks.runAsync(() -> {
            t.updateSync(getConnection());
            runnable.run();
        });
    }

    public void uploadAllSync() {
        Connection connection = getConnection();
        if (connection == null) return;
        updateSyncBatch(connection, new ArrayList<>(map.values()));
    }

    public void onLoadAll(final List<T> list) {
        // hook
    }

    public void uploadAllAsync(Runnable runnable) {
        AtherialTasks.runAsync(() -> {
            uploadAllSync();
            runnable.run();
        });
    }

    /** Helper: resolve key column name for this registry via a fresh instance */
    private String resolveKeyColumnName() {
        try {
            T temp = clazz.newInstance();
            // getColumns() guarantees key column is present (it instantiates id if needed)
            return temp.getColumns().get(0).getName();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Unable to resolve key column name", e);
        }
    }

    public void updateSyncBatch(Connection connection, List<T> dataObjects) {
        if (tableName == null) {
            if (AtherialLib.getInstance().isDebug()) {
                AtherialLib.getInstance().getLogger().severe("Failed to update sync batch due to table name being null");
            }
            return;
        }
        if (connection == null || dataObjects == null || dataObjects.isEmpty()) return;

        String keyCol = resolveKeyColumnName();
        PreparedStatement statement = null;

        try {
            connection.setAutoCommit(false); // Start transaction

            for (T dataObject : dataObjects) {
                StringBuilder updateQuery = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
                List<DataColumn> columns = dataObject.getColumns();

                for (DataColumn column : columns) {
                    if (column.getName().equalsIgnoreCase(keyCol)) continue;
                    updateQuery.append(column.getName()).append(" = ?, ");
                }

                // remove trailing ", "
                updateQuery.delete(updateQuery.length() - 2, updateQuery.length());
                updateQuery.append(" WHERE ").append(keyCol).append(" = ?;");

                statement = connection.prepareStatement(updateQuery.toString());

                int updateParameterIndex = 1;
                for (DataColumn column : columns) {
                    if (column.getName().equalsIgnoreCase(keyCol)) continue;
                    switch (column.getType()) {
                        case LONG:
                            statement.setLong(updateParameterIndex, column.getValueAsLong());
                            break;
                        case TEXT:
                        case LONGTEXT:
                        case VARCHAR:
                            statement.setString(updateParameterIndex, column.getValueAsString());
                            break;
                        case INTEGER:
                            statement.setInt(updateParameterIndex, column.getValueAsInt());
                            break;
                        case BOOLEAN:
                            statement.setBoolean(updateParameterIndex, column.getValueAsBoolean());
                            break;
                        default:
                            statement.setString(updateParameterIndex, column.getValueAsString());
                            break;
                    }
                    updateParameterIndex++;
                }

                // bind WHERE key = ?
                dataObject.getId().updateStatement(statement, updateParameterIndex);

                statement.addBatch();
            }

            statement.executeBatch();
            connection.commit();
            statement.close();

        } catch (Exception e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /** Delete by the new key type, not UUID */
    public void deleteSync(K key) {
        if (key == null) return;
        if (!map.containsKey(key)) return;

        T remove = map.get(key);
        Connection connection = getConnection();
        if (connection == null) return;

        String keyCol = resolveKeyColumnName();
        String sql = "DELETE FROM " + remove.getTableName() + " WHERE " + keyCol + " = ?;";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            key.updateStatement(statement, 1);
            statement.executeUpdate();
            map.remove(key);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<K, T> getMap() {
        return map;
    }

    public T insertSync(T t) {
        map.put(t.getId(), t);
        Connection connection = getConnection();
        if (connection == null) return map.get(t.getId());

        String keyCol = t.getId().buildColumn().getName();

        try {
            PreparedStatement statement = null;
            if (AtherialLib.getInstance().getSqlHandler().isLite()) {
                // SQLITE: INSERT OR REPLACE
                if (!existsInDBSync(t.getId(), connection)) {
                    StringBuilder query = new StringBuilder("INSERT OR REPLACE INTO ")
                            .append(tableName)
                            .append(" (").append(keyCol);
                    StringBuilder placeholders = new StringBuilder(" VALUES (?");

                    List<DataColumn> columns = t.getColumns();

                    for (DataColumn column : columns) {
                        if (!column.getName().equalsIgnoreCase(keyCol)) {
                            query.append(", ").append(column.getName());
                            placeholders.append(", ?");
                        }
                    }

                    query.append(")").append(placeholders).append(");");

                    statement = connection.prepareStatement(query.toString());

                    // bind key first
                    t.getId().updateStatement(statement, 1);

                    // bind the rest
                    int parameterIndex = 2;
                    for (DataColumn column : columns.stream()
                            .filter(c -> !c.getName().equalsIgnoreCase(keyCol))
                            .collect(Collectors.toList())) {
                        switch (column.getType()) {
                            case LONG:
                                statement.setLong(parameterIndex, column.getValueAsLong());
                                break;
                            case TEXT:
                            case LONGTEXT:
                            case VARCHAR:
                                statement.setString(parameterIndex, column.getValueAsString());
                                break;
                            case INTEGER:
                                statement.setInt(parameterIndex, column.getValueAsInt());
                                break;
                            case BOOLEAN:
                                statement.setBoolean(parameterIndex, column.getValueAsBoolean());
                                break;
                            default:
                                break;
                        }
                        parameterIndex++;
                    }
                }
            } else {
                // MYSQL: regular INSERT
                StringBuilder query = new StringBuilder("INSERT INTO ")
                        .append(t.getTableName())
                        .append(" (").append(keyCol);
                StringBuilder placeholders = new StringBuilder(") VALUES (?");

                List<DataColumn> columns = t.getColumns();
                for (DataColumn column : columns) {
                    if (!column.getName().equalsIgnoreCase(keyCol)) {
                        query.append(", ").append(column.getName());
                        placeholders.append(", ?");
                    }
                }
                placeholders.append(")");
                query.append(placeholders).append(";");

                if (AtherialLib.getInstance().isDebug()) {
                    AtherialLib.getInstance().getLogger().info(query.toString());
                }

                statement = connection.prepareStatement(query.toString());

                // bind key
                t.getId().updateStatement(statement, 1);

                // bind the rest
                int parameterIndex = 2;
                for (DataColumn column : columns.stream()
                        .filter(c -> !c.getName().equalsIgnoreCase(keyCol))
                        .collect(Collectors.toList())) {
                    switch (column.getType()) {
                        case LONG:
                            statement.setLong(parameterIndex, column.getValueAsLong());
                            break;
                        case TEXT:
                        case LONGTEXT:
                        case VARCHAR:
                            statement.setString(parameterIndex, column.getValueAsString());
                            break;
                        case INTEGER:
                            statement.setInt(parameterIndex, column.getValueAsInt());
                            break;
                        case BOOLEAN:
                            statement.setBoolean(parameterIndex, column.getValueAsBoolean());
                            break;
                        default:
                            break;
                    }
                    parameterIndex++;
                }
            }

            if (statement != null) {
                if (AtherialLib.getInstance().isDebug()) {
                    AtherialLib.getInstance().getLogger().info(statement.toString());
                }
                statement.executeUpdate();
                statement.close();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map.get(t.getId());
    }

    public void insertAsync(T t, Runnable onComplete) {
        AtherialTasks.runAsync(() -> {
            insertSync(t);
            onComplete.run();
        });
    }

    /** Existence check by new key, not by UUID */
    private boolean existsInDBSync(K key, Connection connection) {
        if (connection == null || key == null || tableName == null) {
            return false;
        }

        String keyCol = resolveKeyColumnName();
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE " + keyCol + " = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            key.updateStatement(statement, 1);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /** Async update by key (new type) */
    public void updateAsync(K key, Runnable result) {
        if (map.containsKey(key)) {
            T t = map.get(key);
            AtherialTasks.runAsync(() -> {
                t.updateSync(getConnection());
                result.run();
            });
        }
    }
}
