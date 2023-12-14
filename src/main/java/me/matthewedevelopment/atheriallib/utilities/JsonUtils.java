package me.matthewedevelopment.atheriallib.utilities;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.io.File;

public class JsonUtils {

    public static GsonBuilder GSON_BUILDER = new GsonBuilder().setPrettyPrinting();

    public JsonUtils() {
    }

    public static <Value> Value loadJsonObjectFromFile(File file, Class<Value> valueClass, Value defaultValue) {
        if (!file.exists()) {
            FileUtils.createFile(file);
            saveJsonObjectToFile(file, defaultValue);
            return defaultValue;
        } else {
            return GSON_BUILDER.create().fromJson(FileUtils.readFileToString(file), valueClass);
        }
    }

    public static String convertJsonStringToPrettyString(String json) {
        JsonParser jsonParser = new JsonParser();
        return GSON_BUILDER.create().toJson(jsonParser.parse(json));
    }

    public static <Value> void saveJsonObjectToFile(File file, Value value) {
        FileUtils.writeStringToFile(file, GSON_BUILDER.create().toJson(value, value.getClass()));
    }
}