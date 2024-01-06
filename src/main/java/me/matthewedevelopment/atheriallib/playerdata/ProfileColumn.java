package me.matthewedevelopment.atheriallib.playerdata;

import me.matthewedevelopment.atheriallib.AtherialLib;

public class ProfileColumn {
    private String name;
    private String type;
    private Object value;

    public ProfileColumn(String name, String type, Object value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getType() {
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

            if (type.equalsIgnoreCase("VARCHAR")){
                return "VARCHAR(255)";
            }
        }
        return type;
    }

    // Add getters and setters as needed
}