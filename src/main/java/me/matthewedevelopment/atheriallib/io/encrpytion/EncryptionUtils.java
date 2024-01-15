package me.matthewedevelopment.atheriallib.io.encrpytion;

import java.util.Base64;

public class EncryptionUtils {
    public static String toBase64(String input) {
        byte[] bytes = input.getBytes();
        byte[] encodedBytes = Base64.getEncoder().encode(bytes);
        return new String(encodedBytes);
    }

    public static String fromBase64(String input) {
        byte[] decodedBytes = Base64.getDecoder().decode(input);
        return new String(decodedBytes);
    }
}
