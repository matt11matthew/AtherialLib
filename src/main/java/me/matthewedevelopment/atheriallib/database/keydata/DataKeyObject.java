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

public abstract class DataKeyObject<K extends DataKeyObject.DataKey<?>, T extends DataKeyObject<K, T>> {
    protected K id;
    private Class<K> keyClazz;

    public abstract static class DataKey<KEY> {
        protected KEY key;

        public DataKey(KEY key) { this.key = key; }
        public DataKey() {}

        public abstract DataColumn buildColumn();
        public abstract DataKey<KEY> load(ResultSet resultSet) throws SQLException;
        public abstract void updateStatement(PreparedStatement statement, int index) throws SQLException;

        @Override
        public String toString() {
            return String.valueOf(key);
        }
    }

    public DataKeyObject(K id, Class<K> keyClazz) {
        this.id = id;
        this.keyClazz = keyClazz;
    }

    public DataKeyObject(Class<K> keyClazz) {
        this.keyClazz = keyClazz;
    }

    public DataKeyObject() {}

    public abstract String getTableName();

    public abstract List<DataColumn> getDefaultColumns();

    public List<DataColumn> getOptionalColumns() {
        return new ArrayList<>();
    }

    /** Helper: ensure we have an id instance and return its column name */
    private String getKeyColumnName() {
        try {
            K k = (id != null) ? id : keyClazz.newInstance();
            return k.buildColumn().getName();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to instantiate key to determine column name", e);
        }
    }

    /** Build full column list = key + optional + defaults */
    public List<DataColumn> getColumns() {
        List<DataColumn> columns = new ArrayList<>();
        if (id == null) {
            try {
                id = keyClazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        columns.add(id.buildColumn());

        List<DataColumn> optionalColumns = getOptionalColumns();
        if (optionalColumns != null && !optionalColumns.isEmpty()) {
            columns.addAll(optionalColumns);
        }
        columns.addAll(getDefaultColumns());
        return columns;
    }

    public K getId() { return id; }

    public T loadFromRS(ResultSet resultSet) throws SQLException {
        try {
            this.id = (K) keyClazz.newInstance().load(resultSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadOptionalFromRS(resultSet);
        return loadResultFromSet(resultSet);
    }

    public void loadOptionalFromRS(ResultSet resultSet) throws SQLException { /* no-op by default */ }

    public abstract T loadResultFromSet(ResultSet resultSet);

    public void updateSync(Connection connection) {
        if (connection == null) return;
        PreparedStatement statement = null;

        if (AtherialLib.getInstance().isDebug()) {
            System.err.println("UPDATING " + String.valueOf(id));
        }

        final String keyCol = getKeyColumnName();

        StringBuilder updateQuery = new StringBuilder("UPDATE ").append(getTableName()).append(" SET ");

        List<DataColumn> columns = getColumns();
        for (DataColumn column : columns) {
            if (!column.getName().equalsIgnoreCase(keyCol)) {
                updateQuery.append(column.getName()).append(" = ?, ");
            }
        }

        if (updateQuery.charAt(updateQuery.length() - 2) == ',') {
            updateQuery.delete(updateQuery.length() - 2, updateQuery.length());
        }

        updateQuery.append(" WHERE ").append(keyCol).append(" = ?;");

        if (AtherialLib.getInstance().isDebug()) {
            System.err.println(updateQuery);
        }

        try {
            statement = connection.prepareStatement(updateQuery.toString());

            int idx = 1;
            for (DataColumn column : columns) {
                if (!column.getName().equalsIgnoreCase(keyCol)) {
                    switch (column.getType()) {
                        case TEXT:
                        case LONGTEXT:
                        case VARCHAR:
                            statement.setString(idx, column.getValueAsString());
                            break;
                        case INTEGER:
                            statement.setInt(idx, column.getValueAsInt());
                            break;
                        case BOOLEAN:
                            statement.setBoolean(idx, column.getValueAsBoolean());
                            break;
                        case LONG:
                            statement.setLong(idx, column.getValueAsLong());
                            break;
                        default:
                            // fallback as string
                            statement.setString(idx, column.getValueAsString());
                            break;
                    }
                    idx++;
                }
            }

            // WHERE key = ?
            id.updateStatement(statement, idx);

            if (statement != null) {
                if (AtherialLib.getInstance().isDebug()) {
                    System.err.println(statement.toString());
                }
                statement.executeUpdate();
                statement.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isTextClear(String text) {
        return text != null && !text.equalsIgnoreCase("null") && !text.isEmpty() && !text.equalsIgnoreCase("none");
    }

    /**
     * New: filter by current key column name (e.g., "server_id") with String values (VARCHAR).
     * Use this for your new id system (e.g., "matt_Dev").
     */
    public List<T> loadAllSyncWhereKeys(Connection connection, List<String> keys) {
        List<T> results = new ArrayList<>();
        if (connection == null || keys == null || keys.isEmpty()) return results;

        final String keyCol = getKeyColumnName();

        StringBuilder query = new StringBuilder("SELECT * FROM ")
                .append(getTableName())
                .append(" WHERE ").append(keyCol).append(" IN (");

        for (int i = 0; i < keys.size(); i++) {
            query.append("?");
            if (i < keys.size() - 1) query.append(", ");
        }
        query.append(");");

        try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
            for (int i = 0; i < keys.size(); i++) {
                statement.setString(i + 1, keys.get(i));
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    DataKeyObject dataObject = this.getClass().newInstance();
                    try {
                        DataKey<?> key = (DataKey<?>) dataObject.keyClazz.newInstance();
                        dataObject.id = key.load(resultSet);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    @SuppressWarnings("unchecked")
                    T loaded = (T) dataObject.loadFromRS(resultSet);
                    results.add(loaded);
                }
            }
        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return results;
    }

    public List<T> loadAllSync(Connection connection) {
        List<T> results = new ArrayList<>();
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        try {
            String query = "SELECT * FROM " + getTableName();
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                DataKeyObject dataObject = this.getClass().newInstance();
                try {
                    DataKey<?> key = (DataKey<?>) dataObject.keyClazz.newInstance();
                    dataObject.id = key.load(resultSet);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                @SuppressWarnings("unchecked")
                T loadedProfile = (T) dataObject.loadFromRS(resultSet);
                results.add(loadedProfile);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (connection == null || connection.isClosed()) return results;
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return results;
    }
}
