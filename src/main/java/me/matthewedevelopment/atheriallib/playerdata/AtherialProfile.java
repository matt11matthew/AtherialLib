package me.matthewedevelopment.atheriallib.playerdata;

import me.matthewedevelopment.atheriallib.AtherialLib;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Matthew E on 12/5/2023 at 9:49 PM for the project AtherialLib
 */
public abstract class AtherialProfile<T extends AtherialProfile<T>> {

    private UUID uuid;

    private String username;

    public AtherialProfile(){

    }
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

    public void update() {
        Connection connection = AtherialLib.getInstance().getProfileManager().getConnection();
        if (connection!=null){
            saveToDatabase(connection);
        }
    }
    public abstract T loadDefault(Player player);

    public abstract T loadResultFromSet(ResultSet resultSet);
    public T loadFromRS(ResultSet resultSet) throws SQLException {
        // Example: Populate custom properties based on columns in the result set
        // Replace these with your actual custom property names and data types

        // Load a VARCHAR column named "custom_column1"
       this.uuid = UUID.fromString(resultSet.getString("uuid"));

        this.username = resultSet.getString("username");



        return loadResultFromSet(resultSet);
    }
    public T load(Player player, Connection sqliteConnection) {
//        checkTable();
        try {
            // Construct and execute an SQL query to retrieve the player's data
            String query = "SELECT * FROM " + getKey() + " WHERE uuid = ?";
            PreparedStatement statement = sqliteConnection.prepareStatement(query);
            statement.setString(1, uuid.toString());

            // Execute the query and process the result set
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Data exists in the database, create a new instance of the profile and load data from the result set
                T loadedProfile = loadFromRS(resultSet);
                resultSet.close();
                statement.close();
                return loadedProfile;
            } else {
                // No data found in the database, load default data, and save it
                T defaultProfile = loadDefault(player);
                saveToDatabase(sqliteConnection); // Save default data to the database
                return defaultProfile;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }



    public List<ProfileColumn> getColumns() {
        List<ProfileColumn> columns = new ArrayList<>();
//        columns.add(new ProfileColumn("UUID", "VARCHAR", uuid+""));
        columns.add(new ProfileColumn("username", "VARCHAR", username));

        columns.addAll(getCustomColumns());
        return columns;
    }


    // ...
    public abstract List<ProfileColumn> getCustomColumns();
    public void saveToDatabase(Connection sqliteConnection) {
        if (sqliteConnection==null)return;

        try {
            PreparedStatement statement= null;
            if (AtherialLib.getInstance().getSqlHandler().isLite()){

                if (!existsInDB(sqliteConnection)){

                    StringBuilder query = new StringBuilder("INSERT OR REPLACE INTO ").append(getKey()).append(" (uuid");
                    StringBuilder placeholders = new StringBuilder(" VALUES (?");

                    // Get the schema requirements from the profile (this is just a conceptual example)
                    List<ProfileColumn> columns = getColumns();

                    // Add columns dynamically to the SQL query
                    for (ProfileColumn column : columns) {
                        query.append(", ").append(column.getName());
                        placeholders.append(", ?");
                    }

                    query.append(")").append(placeholders).append(");");

                    statement = sqliteConnection.prepareStatement(query.toString());
                    statement.setString(1, getUuid().toString());

                    // Set values for each column based on the profile's schema requirements
                    int parameterIndex = 2; // Start at the second parameter
                    for (ProfileColumn column : columns) {
                        // Set the parameter value based on the column's data type
                        switch (column.getType().toUpperCase()) {
                            case "TEXT":
                                statement.setString(parameterIndex, column.getValueAsString());
                                break;
                            case "INTEGER":
                                statement.setInt(parameterIndex, column.getValueAsInt());
                                break;
                            case "BOOLEAN":
                                statement.setBoolean(parameterIndex, column.getValueAsBoolean());
                                break;
                            case "VARCHAR": // Handle VARCHAR
                                statement.setString(parameterIndex, column.getValueAsString());
                                break;
                            default:
                                // Handle other data types as needed
                                break;
                        }

                        parameterIndex++;
                    }
                } else {
                    if (AtherialLib.getInstance().isDebug()){
                        System.err.println("UPDATING " + username);
                    }
                    StringBuilder updateQuery = new StringBuilder("UPDATE ").append(getKey()).append(" SET ");

// Add each column to the update query
                    List<ProfileColumn> columns = getColumns();
                    for (ProfileColumn column : columns) {
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
                    statement= sqliteConnection.prepareStatement(updateQuery.toString());

// Set values for each column based on the profile's schema requirements (excluding UUID)
                    int updateParameterIndex = 1; // Start at the first parameter
                    for (ProfileColumn column : columns) {
                        if (!column.getName().equalsIgnoreCase("uuid")) {
                            // Set the parameter value based on the column's data type
                            switch (column.getType().toUpperCase()) {
                                case "TEXT":
                                    statement.setString(updateParameterIndex, column.getValueAsString());
                                    break;
                                case "INTEGER":
                                    statement.setInt(updateParameterIndex, column.getValueAsInt());
                                    break;
                                case "BOOLEAN":
                                    statement.setBoolean(updateParameterIndex, column.getValueAsBoolean());
                                    break;
                                case "VARCHAR": // Handle VARCHAR
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
                }
            } else {
                if (!existsInDB(sqliteConnection)){

                    StringBuilder query = new StringBuilder("INSERT INTO ").append(getKey()).append(" (uuid");
                    StringBuilder placeholders = new StringBuilder(") VALUES (?");

                    List<ProfileColumn> columns = getColumns();

                    for (ProfileColumn column : columns) {
                        query.append(", ").append(column.getName());
                        placeholders.append(", ?");
                    }

                    placeholders.append(")");





                    // Combine the main query and placeholders
                    query.append(placeholders).append(";");

                    if (AtherialLib.getInstance().isDebug()){
                        System.err.println(query);
                    }


                    statement = sqliteConnection.prepareStatement(query.toString());
                    statement.setString(1, getUuid().toString());

                    // Set values for each column based on the profile's schema requirements
                    int parameterIndex = 2; // Start at the second parameter
                    for (ProfileColumn column : columns) {
                        // Set the parameter value based on the column's data type
                        switch (column.getType().toUpperCase()) {
                            case "TEXT":
                                statement.setString(parameterIndex, column.getValueAsString());
                                break;
                            case "INTEGER":
                                statement.setInt(parameterIndex, column.getValueAsInt());
                                break;
                            case "BOOLEAN":
                                statement.setBoolean(parameterIndex, column.getValueAsBoolean());
                                break;
                            case "VARCHAR": // Handle VARCHAR
                                statement.setString(parameterIndex, column.getValueAsString());
                                break;
                            default:
                                // Handle other data types as needed
                                break;
                        }

                        parameterIndex++;
                    }
                } else {
                    if (AtherialLib.getInstance().isDebug()){
                        System.err.println("UPDATING " + username);
                    }
                    StringBuilder updateQuery = new StringBuilder("UPDATE ").append(getKey()).append(" SET ");

// Add each column to the update query
                    List<ProfileColumn> columns = getColumns();
                    for (ProfileColumn column : columns) {
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
                    statement= sqliteConnection.prepareStatement(updateQuery.toString());

// Set values for each column based on the profile's schema requirements (excluding UUID)
                    int updateParameterIndex = 1; // Start at the first parameter
                    for (ProfileColumn column : columns) {
                        if (!column.getName().equalsIgnoreCase("uuid")) {
                            // Set the parameter value based on the column's data type
                            switch (column.getType().toUpperCase()) {
                                case "TEXT":
                                    statement.setString(updateParameterIndex, column.getValueAsString());
                                    break;
                                case "INTEGER":
                                    statement.setInt(updateParameterIndex, column.getValueAsInt());
                                    break;
                                case "BOOLEAN":
                                    statement.setBoolean(updateParameterIndex, column.getValueAsBoolean());
                                    break;
                                case "VARCHAR": // Handle VARCHAR
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

                }


            }

            if (statement!=null) {
                if (AtherialLib.getInstance().isDebug()) {
                    System.err.println(statement.toString());
                }
                // Execute the query to save or update the data
                statement.executeUpdate();
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean existsInDB(Connection connection) {
        if (connection == null || uuid == null) {
            return false;
        }

        try {
            // Construct an SQL query to check if a profile with the given UUID exists
            String query = "SELECT COUNT(*) FROM " + getKey() + " WHERE uuid = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, String.valueOf(uuid));

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                boolean c = count > 0;

                    if (AtherialLib.getInstance().isDebug()){
                        if (c){
                            System.err.println("EXISTS " + uuid.toString());
                        } else {
                            System.err.println("DOESNT EXISTS " + uuid.toString());

                        }
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

    // ...



}
