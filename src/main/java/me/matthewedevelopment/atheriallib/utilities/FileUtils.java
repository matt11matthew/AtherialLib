package me.matthewedevelopment.atheriallib.utilities;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class FileUtils {
    public FileUtils() {
    }

    public static String readFileToString(File file) {
        try {
            return org.apache.commons.io.FileUtils.readFileToString(file, Charset.forName("UTF-8"));
        } catch (IOException var2) {
            var2.printStackTrace();
            return null;
        }
    }

    public static boolean createFile(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
                return true;
            } catch (IOException var2) {
                var2.printStackTrace();
            }
        }

        return false;
    }

    public static boolean writeStringToFile(File file, String string) {
        try {
            org.apache.commons.io.FileUtils.writeStringToFile(file, string, Charset.forName("UTF-8"), false);
            return true;
        } catch (IOException var3) {
            var3.printStackTrace();
            return false;
        }
    }
}
