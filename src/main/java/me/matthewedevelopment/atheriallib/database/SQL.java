package me.matthewedevelopment.atheriallib.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

public class SQL {
//    public SQL() {
//    }
//
//    public static boolean tableExists(String table) {
//        try {
//            Connection connection = MySQL.getConnection();
//            if (connection == null) {
//                return false;
//            }
//
//            DatabaseMetaData metadata = connection.getMetaData();
//            if (metadata == null) {
//                return false;
//            }
//
//            ResultSet rs = metadata.getTables((String)null, (String)null, table, (String[])null);
//            if (rs.next()) {
//                return true;
//            }
//        } catch (Exception var4) {
//        }
//
//        return false;
//    }
//
//    public static boolean insertData(String columns, String values, String table) {
//        return MySQL.update("INSERT INTO " + table + " (" + columns + ") VALUES (" + values + ");");
//    }
//
//    public static boolean deleteData(String column, String logic_gate, String data, String table) {
//        if (data != null) {
//            data = "'" + data + "'";
//        }
//
//        return MySQL.update("DELETE FROM " + table + " WHERE " + column + logic_gate + data + ";");
//    }
//
//    public static boolean exists(String column, String data, String table) {
//        if (data != null) {
//            data = "'" + data + "'";
//        }
//
//        try {
//            ResultSet rs = MySQL.query("SELECT * FROM " + table + " WHERE " + column + "=" + data + ";");
//            if (rs.next()) {
//                return true;
//            }
//        } catch (Exception var4) {
//        }
//
//        return false;
//    }
//
//    public static boolean deleteTable(String table) {
//        return MySQL.update("DROP TABLE " + table + ";");
//    }
//
//    public static boolean truncateTable(String table) {
//        return MySQL.update("TRUNCATE TABLE " + table + ";");
//    }
//
//    public static boolean createTable(String table, String columns) {
//        return MySQL.update("CREATE TABLE IF NOT EXISTS " + table + " (" + columns + ");");
//    }
//
//    public static boolean upsert(String selected, Object object, String column, String data, String table) {
//        if (object != null) {
//            object = "'" + object + "'";
//        }
//
//        if (data != null) {
//            data = "'" + data + "'";
//        }
//
//        try {
//            ResultSet rs = MySQL.query("SELECT * FROM " + table + " WHERE " + column + "=" + data + ";");
//            if (rs.next()) {
//                MySQL.update("UPDATE " + table + " SET " + selected + "=" + object + " WHERE " + column + "=" + data + ";");
//            } else {
//                insertData(column + ", " + selected, data + ", " + object, table);
//            }
//        } catch (Exception var6) {
//        }
//
//        return false;
//    }
//
//    public static boolean set(String selected, Object object, String column, String logic_gate, String data, String table) {
//        if (object != null) {
//            object = "'" + object + "'";
//        }
//
//        if (data != null) {
//            data = "'" + data + "'";
//        }
//
//        return MySQL.update("UPDATE " + table + " SET " + selected + "=" + object + " WHERE " + column + logic_gate + data + ";");
//    }
//
//    public static boolean set(String selected, Object object, String[] where_arguments, String table) {
//        String arguments = "";
//        String[] var5 = where_arguments;
//        int var6 = where_arguments.length;
//
//        for(int var7 = 0; var7 < var6; ++var7) {
//            String argument = var5[var7];
//            arguments = arguments + argument + " AND ";
//        }
//
//        if (arguments.length() <= 5) {
//            return false;
//        } else {
//            arguments = arguments.substring(0, arguments.length() - 5);
//            if (object != null) {
//                object = "'" + object + "'";
//            }
//
//            return MySQL.update("UPDATE " + table + " SET " + selected + "=" + object + " WHERE " + arguments + ";");
//        }
//    }
//
//    public static Object get(String selected, String[] where_arguments, String table) {
//        String arguments = "";
//        String[] var4 = where_arguments;
//        int var5 = where_arguments.length;
//
//        for(int var6 = 0; var6 < var5; ++var6) {
//            String argument = var4[var6];
//            arguments = arguments + argument + " AND ";
//        }
//
//        if (arguments.length() <= 5) {
//            return false;
//        } else {
//            arguments = arguments.substring(0, arguments.length() - 5);
//
//            try {
//                ResultSet rs = MySQL.query("SELECT * FROM " + table + " WHERE " + arguments + ";");
//                if (rs.next()) {
//                    return rs.getObject(selected);
//                }
//            } catch (Exception var8) {
//            }
//
//            return null;
//        }
//    }
//
//    public static ArrayList<Object> listGet(String selected, String[] where_arguments, String table) {
//        ArrayList<Object> array = new ArrayList();
//        String arguments = "";
//        String[] var5 = where_arguments;
//        int var6 = where_arguments.length;
//
//        for(int var7 = 0; var7 < var6; ++var7) {
//            String argument = var5[var7];
//            arguments = arguments + argument + " AND ";
//        }
//
//        if (arguments.length() <= 5) {
//            return array;
//        } else {
//            arguments = arguments.substring(0, arguments.length() - 5);
//
//            try {
//                ResultSet rs = MySQL.query("SELECT * FROM " + table + " WHERE " + arguments + ";");
//
//                while(rs.next()) {
//                    array.add(rs.getObject(selected));
//                }
//            } catch (Exception var9) {
//            }
//
//            return array;
//        }
//    }
//
//    public static Object get(String selected, String column, String logic_gate, String data, String table) {
//        if (data != null) {
//            data = "'" + data + "'";
//        }
//
//        try {
//            ResultSet rs = MySQL.query("SELECT * FROM " + table + " WHERE " + column + logic_gate + data + ";");
//            if (rs.next()) {
//                return rs.getObject(selected);
//            }
//        } catch (Exception var6) {
//        }
//
//        return null;
//    }
//
//    public static ArrayList<Object> listGet(String selected, String column, String logic_gate, String data, String table) {
//        ArrayList<Object> array = new ArrayList();
//        if (data != null) {
//            data = "'" + data + "'";
//        }
//
//        try {
//            ResultSet rs = MySQL.query("SELECT * FROM " + table + " WHERE " + column + logic_gate + data + ";");
//
//            while(rs.next()) {
//                array.add(rs.getObject(selected));
//            }
//        } catch (Exception var7) {
//        }
//
//        return array;
//    }
//
//    public static int countRows(String table) {
//        int i = 0;
//        if (table == null) {
//            return i;
//        } else {
//            ResultSet rs = MySQL.query("SELECT * FROM " + table + ";");
//
//            try {
//                while(rs.next()) {
//                    ++i;
//                }
//            } catch (Exception var4) {
//            }
//
//            return i;
//        }
//    }
}
