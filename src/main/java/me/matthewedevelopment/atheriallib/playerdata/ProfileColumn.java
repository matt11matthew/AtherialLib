package me.matthewedevelopment.atheriallib.playerdata;

import lombok.Builder;
import me.matthewedevelopment.atheriallib.AtherialLib;

public class ProfileColumn {
    private String name;
    private ProfileColumnType type;
    private Object value;

    public ProfileColumn(String name, ProfileColumnType type, Object value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }


    public String getName() {
        return name;
    }

    public ProfileColumnType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public String getValueAsString() {
        return String.valueOf(value);
    }

    public long getValueAsLong() {
        return (long) value;
    }

    public int getValueAsInt() {
        return (int) value;
    }

    public boolean getValueAsBoolean() {
        return (boolean) value;
    }

    public String getTypeToString() {
        if (!AtherialLib.getInstance().getSqlHandler().isLite()) {

            if (type == ProfileColumnType.VARCHAR) {
                return "VARCHAR(255)";
            }
        }
        return type.toString();
    }

    public static ProfileColumnBuilder builder() {
        return new ProfileColumnBuilder();
    }

    public static final class ProfileColumnBuilder {
        private String name;
        private ProfileColumnType type;
        private Object value;

        private ProfileColumnBuilder() {
        }


        public ProfileColumnBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ProfileColumnBuilder type(ProfileColumnType type) {
            this.type = type;
            return this;
        }

        public ProfileColumnBuilder value(Object value) {
            this.value = value;
            return this;
        }

        public ProfileColumn build() {
            return new ProfileColumn(name, type, value);
        }
    }

}