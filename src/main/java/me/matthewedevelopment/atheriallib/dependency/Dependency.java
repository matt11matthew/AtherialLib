package me.matthewedevelopment.atheriallib.dependency;

import java.util.logging.Logger;

/**
 * Created by Matthew E on 5/10/2018.
 */




public abstract class Dependency<T> {
    protected String name;
    protected T plugin;
    protected DependencyManager dependencyManager;
    protected Logger logger;

    public Dependency(String name, T plugin) {
        this.name = name;
        this.plugin = plugin;
        this.logger = Logger.getLogger(name);
    }

    public String getName() {
        return name;
    }

    public T getPlugin() {
        return plugin;
    }

    public DependencyManager getDependencyManager() {
        return dependencyManager;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setDependencyManager(DependencyManager dependencyManager) {
        this.dependencyManager = dependencyManager;
    }

    public abstract void onEnable();

    public abstract void onPreEnable();

    public abstract void onDisable();
}
