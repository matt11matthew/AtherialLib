package me.matthewedevelopment.atheriallib.nms;

/**
 * Created by Matthew Eisenberg on 1/30/2019 at 1:12 PM for the project atherialapi
 */
public abstract class VersionProviderAddonHandler<T extends VersionProviderAddon> {
    private String name;
    private Version version;
    private T addon;
    private Class<T> baseClass;

    public VersionProviderAddonHandler(String name, Class<T> baseClass) {
        this.name = name;
        this.baseClass = baseClass;
    }

    public Class<T> getBaseClass() {
        return baseClass;
    }

    public T getAddon() {
        return addon;
    }

    public Version getVersion() {
        return version;
    }

    public void setup(Version version) {
        this.version = version;
        this.addon = getAddonByVersion(this.version);
    }

    public abstract T getAddonByVersion(Version version);

    public String getName() {
        return name;
    }
}
