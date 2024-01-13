package me.matthewedevelopment.atheriallib.nms;

import me.matthewedevelopment.atheriallib.nms.providers.*;
import org.bukkit.Bukkit;

/**
 * Created by Matthew Eisenberg on 5/20/2018 at 8:39 PM for the project atherialapi
 */
public enum Version {
    V1_7_R4(VersionProviderV1_7_R4.class),
    V1_8_R3(VersionProviderV1_8_R3.class),
    V1_9_R1(VersionProviderV1_9_R1.class),
    V1_9_R2(VersionProviderV1_9_R2.class),
    V1_10,
    V1_11,
    V1_12_R1(VersionProviderV1_12_R1.class),
    V1_13_R1(VersionProviderV1_13_R1.class),
    V1_13_R2(VersionProviderV1_13_R2.class),
    V1_14_R1(VersionProviderV1_14_R1.class),
    V1_15_R1(VersionProviderV1_15_R1.class),
    V1_16_R1(VersionProviderV1_16_R1.class),
    V1_20_R1(VersionProviderV1_20_R1.class),
    V1_20_R2(VersionProviderV1_20_R2.class),
    V1_20_R3(VersionProviderV1_20_R3.class);

    private Class<? extends VersionProvider> versionProviderClass;

    Version() {

    }

    Version(Class<? extends VersionProvider> versionProviderClass) {
        this.versionProviderClass = versionProviderClass;
    }

    public static Version getVersion() {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        System.out.println(packageName);
        for (Version minecraftVersion : Version.values()) {
            if (packageName.contains(minecraftVersion.toString().replaceAll("V", "").trim())) {
                return minecraftVersion;
            }
        }
        return null;
    }

    public Class<?> getNMSClass(String simpleName) {
        String className = "net.minecraft.server." + getVersionString() + "." + simpleName;
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getVersionString() {
        return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    }

    public Class<? extends VersionProvider> getVersionProviderClass() {
        return versionProviderClass;
    }
}
