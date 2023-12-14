package me.matthewedevelopment.atheriallib.item;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.matthewedevelopment.atheriallib.AtherialLib;

/**
 * Created by Matthew E on 12/13/2023 at 8:05 PM for the project AtherialLib
 */
public class AtherialItemAPI {
    private static AtherialLib atherialLib;
    public static final Gson GSON = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
        @Override
        public boolean shouldSkipField(FieldAttributes fieldAttributes) {
            return fieldAttributes.getAnnotation(IgnoreData.class) != null;
        }

        @Override
        public boolean shouldSkipClass(Class<?> aClass) {
            return false;
        }
    }).create();
    public static void setAtherialLib(AtherialLib atherialLib) {
        AtherialItemAPI.atherialLib = atherialLib;
    }

    public static AtherialLib getAtherialLib() {
        return atherialLib;
    }
}
