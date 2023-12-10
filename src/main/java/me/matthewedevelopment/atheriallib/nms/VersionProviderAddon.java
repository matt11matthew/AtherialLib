package me.matthewedevelopment.atheriallib.nms;

/**
 * Created by Matthew Eisenberg on 1/30/2019 at 10:09 AM for the project atherialapi
 */
public abstract class VersionProviderAddon {
    private String name;
    private Version version;

    public VersionProviderAddon(String name, Version version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public Version getVersion() {
        return version;
    }
}
