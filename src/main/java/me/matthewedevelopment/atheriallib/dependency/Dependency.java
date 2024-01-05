package me.matthewedevelopment.atheriallib.dependency;

import me.matthewedevelopment.atheriallib.AtherialLib;

import java.util.logging.Logger;

/**
 * Created by Matthew E on 5/10/2018.
 */




public abstract class Dependency {
    protected String name;
    protected AtherialLib plugin;

    public Dependency(String name,AtherialLib plugin) {
        this.name = name;
        this.plugin = plugin;
    }

    public String getName() {
        return name;
    }

    public AtherialLib getPlugin() {
        return plugin;
    }

    public abstract void onEnable();

    public abstract void onPreEnable();

    public abstract void onDisable();
}
