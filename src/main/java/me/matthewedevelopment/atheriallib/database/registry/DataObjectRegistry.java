package me.matthewedevelopment.atheriallib.database.registry;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.database.mysql.MySqlHandler;
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

public abstract class DataObjectRegistry<T extends DataObject<T>> {
     protected Map<UUID,T> map;
    private Class<T> clazz;
    private DatabaseTableManagerCustomData databaseTableManager;
    private String tableName=null;


    @Data
    @AllArgsConstructor
    public static class Tuple<A,B> {
        public A a;
        public B b;
    }
    public Tuple<Boolean, T> preRegister() {


        if (AtherialLib.getInstance().getSqlHandler().isLite()){
            databaseTableManager=new SQLiteDatabaseTableManagerCustom();
        } else {
            databaseTableManager=new MySQLDatabaseTableManagerCustom();
        }
        T temp=null;
        try {
            temp = clazz.newInstance();

            tableName=temp.getTableName();
            AtherialLib.getInstance().getLogger().info("TABLE NAME: ("+tableName+")");
            databaseTableManager.createOrUpdateTable(getConnection(),tableName,temp.getColumns());
        } catch (InstantiationException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
            return new Tuple<>(false,null);
        }
        return new Tuple<>(true,temp);

    }
    public void register() {


        Tuple<Boolean, T> booleanTTuple = preRegister();

        if ((booleanTTuple != null) && booleanTTuple.a && (booleanTTuple.getB() != null)) {
            loadAllSync(booleanTTuple.getB());
        }
        onRegister();
    }
    public DataObjectRegistry(Class<T> clazz) {
        this.clazz = clazz;

        this.map = new HashMap<>();
    }

    public abstract void onRegister();
    public T getData(UUID uuid){
        return map.getOrDefault(uuid, null);
    }


    public void loadAllSync(T tempInstance) {
        AtherialTasks.runSync(() -> {
            List<T> ts = tempInstance.loadAllSync(getConnection());
            for (T t1 : ts) {
                map.put(t1.getUuid(),t1);
            }
            onLoadAll(ts);
        });

    }

    public Connection getConnection() {
        MySqlHandler sqlHandler = AtherialLib.getInstance().getSqlHandler();
        return sqlHandler.getConnection();
//        return null;
    }


    public void updateAsync(T t, Runnable runnable) {
        AtherialTasks.runAsync(() -> {
            t.updateSync(getConnection());
            runnable.run();
        });
    }

    public void uploadAllSync() {
        Connection connection = getConnection();
        if (connection==null)return;
        updateSyncBatch(connection,new ArrayList<>(map.values()));
    }

    public void onLoadAll(final List<T> list) {

    }

    public void uploadAllAsync(Runnable runnable) {
        AtherialTasks.runAsync(() ->{
            uploadAllSync();
            runnable.run();
        });
    }
    public void updateSyncBatch(Connection connection, List<T> dataObjects) {
        if (tableName==null){
            if (AtherialLib.getInstance().isDebug()){
                AtherialLib.getInstance().getLogger().severe("Failed to update sync batch due to table name being null");

            }
            return;
        }
        if (connection == null || dataObjects == null || dataObjects.isEmpty()) return;
        PreparedStatement statement = null;

        try {
            connection.setAutoCommit(false); // Start transaction

            for (T dataObject : dataObjects) {
                StringBuilder updateQuery = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
                List<DataColumn> columns = dataObject.getColumns(); // Assuming DataObject has a method to get its columns

                for (DataColumn column : columns) {
                    if (column.getName().equalsIgnoreCase("uuid"))continue;
                    updateQuery.append(column.getName()).append(" = ?, ");
                }

                updateQuery.delete(updateQuery.length() - 2, updateQuery.length()); // Remove trailing comma and space
                updateQuery.append(" WHERE uuid = ?;"); // Assuming each DataObject can be uniquely identified by a UUID

                statement = connection.prepareStatement(updateQuery.toString());

                int updateParameterIndex = 1;
                for (DataColumn column : columns) {
                    if (column.getName().equalsIgnoreCase("uuid"))continue;
                    // Set the parameters for each column
                    switch (column.getType()) {
                        case LONG:
                            statement.setLong(updateParameterIndex, column.getValueAsLong());
                            break;
                        case TEXT:
                            statement.setString(updateParameterIndex, column.getValueAsString());
                            break;
                        case INTEGER:
                            statement.setInt(updateParameterIndex, column.getValueAsInt());
                            break;
                        case BOOLEAN:
                            statement.setBoolean(updateParameterIndex, column.getValueAsBoolean());
                            break;
                        case VARCHAR:
                            statement.setString(updateParameterIndex, column.getValueAsString());
                            break;
                        // Add other cases as needed
                    }
                    updateParameterIndex++;
                }

                statement.setString(updateParameterIndex, dataObject.getUuid().toString());
                statement.addBatch(); // Add this update to the batch
            }

            statement.executeBatch(); // Execute all updates in the batch
            connection.commit(); // Commit the transaction
            statement.close();

        } catch (Exception e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback(); // Rollback in case of an error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true); // Reset auto-commit to true
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void deleteSync(UUID uuid) {
        if (map.containsKey(uuid)) {
            T remove = map.get(uuid);
            Connection connection = getConnection();
            if (connection==null)return;
            String sql = "DELETE FROM " + remove.getTableName() +" WHERE uuid=?;";
            try {
                PreparedStatement statement= connection.prepareStatement(sql);
                statement.setString(1, uuid.toString());
                statement.executeUpdate();
                statement.close();
                map.remove(uuid);
            } catch (SQLException e) {
               e.printStackTrace();
            }
        }

    }

    public Map<UUID, T> getMap() {
        return map;
    }

    public T insertSync(T t) {
        map.put(t.getUuid(), t);
        Connection connection = getConnection();

        try {
            PreparedStatement statement = null;
            if (AtherialLib.getInstance().getSqlHandler().isLite()) {

                if (!existsInDBSync(t.getUuid(), connection)) {

                    StringBuilder query = new StringBuilder("INSERT OR REPLACE INTO ").append(tableName).append(" (uuid");
                    StringBuilder placeholders = new StringBuilder(" VALUES (?");

                    // Get the schema requirements from the profile (this is just a conceptual example)
                    List<DataColumn> columns = t.getColumns();

                    // Add columns dynamically to the SQL query
                    for (DataColumn column : columns) {
                        if (!column.getName().equalsIgnoreCase("uuid")) {
                            query.append(", ").append(column.getName());

                            placeholders.append(", ?");
                        }
                    }

                    query.append(")").append(placeholders).append(");");

                    statement = connection.prepareStatement(query.toString());
                    statement.setString(1, t.getUuid().toString());

                    // Set values for each column based on the profile's schema requirements
                    int parameterIndex = 2; // Start at the second parameter
                    for (DataColumn column : columns.stream().filter(dataColumn -> !dataColumn.getName().equalsIgnoreCase("uuid")).collect(Collectors.toList())) {
                        // Set the parameter value based on the column's data type
                        switch (column.getType()) {
                            case LONG:
                                statement.setLong(parameterIndex, column.getValueAsLong());
                                break;
                            case TEXT:
                                statement.setString(parameterIndex, column.getValueAsString());
                                break;
                            case INTEGER:
                                statement.setInt(parameterIndex, column.getValueAsInt());
                                break;
                            case BOOLEAN:
                                statement.setBoolean(parameterIndex, column.getValueAsBoolean());
                                break;
                            case VARCHAR: // Handle VARCHAR
                                statement.setString(parameterIndex, column.getValueAsString());
                                break;
                            default:
                                // Handle other data types as needed
                                break;
                        }

                        parameterIndex++;
                    }
                }
            } else {
                StringBuilder query = new StringBuilder("INSERT INTO ").append(t.getTableName()).append(" (uuid");
                StringBuilder placeholders = new StringBuilder(") VALUES (?");

                List<DataColumn> columns = t.getColumns();

                for (DataColumn column : columns) {
                    if (!column.getName().equalsIgnoreCase("uuid")) {
                        query.append(", ").append(column.getName());
                        placeholders.append(", ?");

                    }
//                        query.append(", ").append(column.getName());
                }

                placeholders.append(")");


                // Combine the main query and placeholders
                query.append(placeholders).append(";");

                if (AtherialLib.getInstance().isDebug()) {
                    AtherialLib.getInstance().getLogger().info(query.toString());
                }


                statement = connection.prepareStatement(query.toString());
                statement.setString(1, t.getUuid().toString());

                // Set values for each column based on the profile's schema requirements
                int parameterIndex = 2; // Start at the second parameter
                for (DataColumn column : columns.stream().filter(dataColumn -> !dataColumn.getName().equalsIgnoreCase("uuid")).collect(Collectors.toList())) {
                    // Set the parameter value based on the column's data type
                    switch (column.getType()) {
                        case LONG:
                            statement.setLong(parameterIndex, column.getValueAsLong());
                            break;
                        case TEXT:
                            statement.setString(parameterIndex, column.getValueAsString());
                            break;
                        case INTEGER:
                            statement.setInt(parameterIndex, column.getValueAsInt());
                            break;
                        case BOOLEAN:
                            statement.setBoolean(parameterIndex, column.getValueAsBoolean());
                            break;
                        case VARCHAR: // Handle VARCHAR
                            statement.setString(parameterIndex, column.getValueAsString());
                            break;
                        default:
                            // Handle other data types as needed
                            break;
                    }

                    parameterIndex++;
                }
            }
            if (statement != null) {
                if (AtherialLib.getInstance().isDebug()) {
                    AtherialLib.getInstance().getLogger().info(statement.toString());
                }
                // Execute the query to save or update the data
                statement.executeUpdate();
                statement.close();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map.get(t.getUuid());
    }
    public void insertAsync(T t, Runnable onComplete) {
        AtherialTasks.runAsync(() -> {
            T t1 = insertSync(t);
            onComplete.run();
        });
    }

    private boolean existsInDBSync(UUID uuid, Connection connection) {
        if (connection == null || uuid == null ||tableName==null) {
            return false;
        }

        try {
            // Construct an SQL query to check if a profile with the given UUID exists
            String query = "SELECT COUNT(*) FROM " + tableName + " WHERE uuid = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, String.valueOf(uuid));

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                boolean c = count > 0;

                if (AtherialLib.getInstance().isDebug()){
//                    if (c){
//                        System.err.println("EXISTS " + uuid.toString());
//                    } else {
//                        System.err.println("DOESNT EXISTS " + uuid.toString());
//
//                    }
                }
                return c; // Return true if a profile with the given UUID exists
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // An error occurred or the profile was not found
    }

    public void updateAsync(UUID uuid, Runnable result) {
        if (map.containsKey(uuid)){
            T t = map.get(uuid);
            AtherialTasks.runAsync(() -> {
                t.updateSync(getConnection());
                result.run();
            });
        }


    }
}
