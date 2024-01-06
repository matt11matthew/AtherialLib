package me.matthewedevelopment.atheriallib.database.registry;

import me.matthewedevelopment.atheriallib.AtherialLib;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class DataObject<T extends DataObject<T>> {
    private UUID uuid;


    public DataObject(UUID uuid) {
        this.uuid = uuid;
    }
    public DataObject() {
    }




    public abstract String getTableName();


    public abstract List<DataColumn> getDefaultColumns();
    public List<DataColumn> getColumns() {
        List<DataColumn> columns = new ArrayList<>();
        columns.add(new DataColumn("uuid", DataColumnType.VARCHAR, ""));
        columns.addAll(getDefaultColumns());
        return columns;
    }


    public UUID getUuid() {
        return uuid;
    }
//    public abstract T getDefaultData(UUID uuid);





    public T loadFromRS(ResultSet resultSet) throws SQLException {
        // Example: Populate custom properties based on columns in the result set
        // Replace these with your actual custom property names and data types

        // Load a VARCHAR column named "custom_column1"
        this.uuid = UUID.fromString(resultSet.getString("uuid"));




        return loadResultFromSet(resultSet);
    }

    public abstract T loadResultFromSet(ResultSet resultSet);




    public void updateSync(Connection connection) {
        if (connection==null)return;
        PreparedStatement statement= null;
        if (AtherialLib.getInstance().isDebug()){
            System.err.println("UPDATING " + uuid.toString());
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
                        case INTEGER:
                            statement.setInt(updateParameterIndex, column.getValueAsInt());
                            break;
                        case BOOLEAN:
                            statement.setBoolean(updateParameterIndex, column.getValueAsBoolean());
                            break;
                        case VARCHAR: // Handle VARCHAR
                            statement.setString(updateParameterIndex, column.getValueAsString());
                            break;
                        default:
                            // Handle other data types as needed
                            break;
                    }

                    updateParameterIndex++;
                }
            }

            statement.setString(updateParameterIndex, getUuid().toString());

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

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
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
                DataObject dataObject = this.getClass().newInstance();
                dataObject.setUuid(UUID.fromString(resultSet.getString("uuid")));
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
