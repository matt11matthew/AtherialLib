package me.matthewedevelopment.atheriallib.database.keydata;

import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.database.registry.DataColumn;
import me.matthewedevelopment.atheriallib.database.registry.DataColumnType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class DataKeyObject<K extends DataKeyObject.DataKey<?>, T extends DataKeyObject<K, T>> {
    protected K id;
    private Class<K> keyClazz;


    public abstract static class  DataKey<KEY> {
        protected KEY key;

        public DataKey(KEY key) {
            this.key = key;
        }

        public DataKey() {
        }

        public abstract DataColumn buildColumn();
        public abstract  DataKey<KEY> load(ResultSet resultSet) throws SQLException;

        public abstract void updateStatement(PreparedStatement statement, int index);
    }

    public DataKeyObject(K id, Class<K> keyClazz) {
        this.id =id;
        this.keyClazz = keyClazz;
    }

    public DataKeyObject( Class<K> keyClazz) {
        this.keyClazz = keyClazz;
    }

    public DataKeyObject() {
    }




    public abstract String getTableName();


    public abstract List<DataColumn> getDefaultColumns();
    public List<DataColumn> getOptionalColumns() {
        return new ArrayList<>();
    }
    public List<DataColumn> getColumns() {
        List<DataColumn> columns = new ArrayList<>();
        columns.add(id.buildColumn());


        List<DataColumn> optionalColumns = getOptionalColumns();
        if (optionalColumns!=null&&!optionalColumns.isEmpty()){

            columns.addAll(optionalColumns);
        }
        columns.addAll(getDefaultColumns());
        return columns;
    }


    public K getId() {
        return id;
    }

    public T loadFromRS(ResultSet resultSet) throws SQLException {
        // Example: Populate custom properties based on columns in the result set
        // Replace these with your actual custom property names and data types

        // Load a VARCHAR column named "custom_column1"
        try {
            this.id = (K) keyClazz.newInstance().load(resultSet);
        } catch (Exception e) {
           e.printStackTrace();
        }


        loadOptionalFromRS(resultSet);

        return loadResultFromSet(resultSet);
    }

    public void loadOptionalFromRS(ResultSet resultSet) throws SQLException {

    }

    public abstract T loadResultFromSet(ResultSet resultSet);




    public void updateSync(Connection connection) {
        if (connection==null)return;
        PreparedStatement statement= null;
        if (AtherialLib.getInstance().isDebug()){
            System.err.println("UPDATING " + id.toString());
        }
        StringBuilder updateQuery = new StringBuilder("UPDATE ").append(getTableName()).append(" SET ");

// Add each column to the update query
        List<DataColumn> columns = getColumns();
        for (DataColumn column : columns) {
            if (!column.getName().equalsIgnoreCase("uuid")) { // Exclude the UUID column from the update
                updateQuery.append(column.getName()).append(" = ?, ");
            }
        }

// Remove the trailing comma and space from the update query
        if (columns.size() > 0) {
            updateQuery.delete(updateQuery.length() - 2, updateQuery.length());
        }

// Add the WHERE clause to specify which row to update
        updateQuery.append(" WHERE uuid = ?;");

        if (AtherialLib.getInstance().isDebug()) {
            System.err.println(updateQuery);
        }

// Now you have the update query, and you can use it to update the existing row
        try {
            statement= connection.prepareStatement(updateQuery.toString());

// Set values for each column based on the profile's schema requirements (excluding UUID)
            int updateParameterIndex = 1; // Start at the first parameter
            for (DataColumn column : columns) {
                if (!column.getName().equalsIgnoreCase("uuid")) {
                    // Set the parameter value based on the column's data type
                    switch (column.getType()) {
                        case TEXT:
                            statement.setString(updateParameterIndex, column.getValueAsString());
                            break;



                        case LONGTEXT:
                            statement.setString(updateParameterIndex, column.getValueAsString());
                            break;
                        case INTEGER:
                            statement.setInt(updateParameterIndex, column.getValueAsInt());
                            break;
                        case BOOLEAN:
                            statement.setBoolean(updateParameterIndex, column.getValueAsBoolean());
                            break;
                        case VARCHAR: // Handle VARCHAR
                            statement.setString(updateParameterIndex, column.getValueAsString());
                            break;
                        case LONG:
                            statement.setLong(updateParameterIndex, column.getValueAsLong());
                            break;
                        default:
                            // Handle other data types as needed
                            break;
                    }

                    updateParameterIndex++;
                }
            }

            id.updateStatement(statement, updateParameterIndex);

            if (statement!=null) {
                if (AtherialLib.getInstance().isDebug()) {
                    System.err.println(statement.toString());
                }
                // Execute the query to save or update the data
                statement.executeUpdate();
                statement.close();
            }
        } catch ( Exception e){
            e.printStackTrace();
        }
    }


    public boolean isTextClear(String text) {
        return text!=null&&!text.equalsIgnoreCase("null")&&!text.isEmpty()&&!text.equalsIgnoreCase("none");
    }

    public List<T> loadAllSyncWhereKeys(Connection connection, List<UUID> keys) {
        List<T> results = new ArrayList<>();
        if (keys == null || keys.isEmpty()) return results;

        StringBuilder query = new StringBuilder("SELECT * FROM ")
                .append(getTableName())
                .append(" WHERE uuid IN (");

        for (int i = 0; i < keys.size(); i++) {
            query.append("?");
            if (i < keys.size() - 1) query.append(", ");
        }
        query.append(");");

        try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
            for (int i = 0; i < keys.size(); i++) {
                statement.setString(i + 1, keys.get(i).toString());
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    DataKeyObject dataObject = this.getClass().newInstance(); // Or use a factory method
                    try {
                        DataKeyObject.DataKey<?> key = (DataKey<?>) dataObject.keyClazz.newInstance();

                        dataObject.id = key.load(resultSet);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    T loaded = (T) dataObject.loadFromRS(resultSet);
                    results.add(loaded);
                }
            }

        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return results;
    }


    public List<T> loadAllSync(Connection connection){
        List<T> results = new ArrayList<>();
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        try {
            // Construct and execute an SQL query to retrieve the player's data
            String query = "SELECT * FROM " + getTableName();
             statement = connection.prepareStatement(query);
//            statement.setString(1, uuid.toString());

            // Execute the query and process the result set
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                // Data exists in the database, create a new instance of the profile and load data from the result set
                DataKeyObject dataObject = this.getClass().newInstance();

                try {
                    DataKeyObject.DataKey<?> key = (DataKey<?>) dataObject.keyClazz.newInstance();

                    dataObject.id = key.load(resultSet);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                T loadedProfile = (T) dataObject.loadFromRS(resultSet);
                results.add(loadedProfile);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (connection==null||connection.isClosed())return  results;
                if (resultSet!=null){
                    resultSet.close();
                }
                if (statement!=null){

                    statement.close();
                }
            } catch ( Exception e){
                e.printStackTrace();
            }
        }
        return results;
    }
}
