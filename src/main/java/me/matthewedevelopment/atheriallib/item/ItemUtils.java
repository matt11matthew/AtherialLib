package me.matthewedevelopment.atheriallib.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.util.Base64;

/**
 * Created by Matthew E on 12/6/2023 at 11:47 PM for the project AtherialLib
 */
public class ItemUtils {
    public static String toBase64(ItemStack item) {
        if (item==null||item.getType()==Material.AIR)return "AIR";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(item);
            dataOutput.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }
    public static ItemStack fromBase64(String data) {
        if (data==null||data.equals("AIR"))return new ItemStack(Material.AIR);
//        if (data==null)return new ItemStack(Material.AIR);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data.trim()));
        ItemStack item = new ItemStack(Material.AIR);
         try {
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
              item = (ItemStack) dataInput.readObject();
             dataInput.close();
         } catch ( Exception e) {
             throw new RuntimeException(e);
         }

        return item;
    }

}
