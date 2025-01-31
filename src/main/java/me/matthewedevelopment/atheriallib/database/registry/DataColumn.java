package me.matthewedevelopment.atheriallib.database.registry;

import lombok.Builder;
import me.matthewedevelopment.atheriallib.AtherialLib;

import me.matthewedevelopment.atheriallib.AtherialLib;
@Builder
public class DataColumn {
    private String name;
    private DataColumnType type;
    private Object value;

    public DataColumn(String name, DataColumnType type, Object value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public DataColumnType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public String getValueAsString() {
        return String.valueOf(value);
    }

    public int getValueAsInt() {
        return (int) value;
    }

    public boolean getValueAsBoolean() {
        return (boolean) value;
    }

    public String getTypeToString() {
        if (!AtherialLib.getInstance().getSqlHandler().isLite()){

            if (type==DataColumnType.VARCHAR){
                return "VARCHAR(255)";
            }
        }
        return type.toString();
    }

    public long getValueAsLong() {
        return (long) value;
    }

    // Add getters and setters as needed
}