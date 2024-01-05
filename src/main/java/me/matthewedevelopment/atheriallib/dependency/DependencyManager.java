package me.matthewedevelopment.atheriallib.dependency;

import me.matthewedevelopment.atheriallib.AtherialLib;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthew E on 5/10/2018.
 */
public class DependencyManager {
    private List<Dependency> loadedDependencies;

    public DependencyManager() {
        this.loadedDependencies = new ArrayList<>();
    }

    public <T extends Dependency> T getDependency(Class<T> clazz) {
        return (T) loadedDependencies.stream()
                .filter(dependency -> dependency.getClass().getName().equals(clazz.getName()))
                .findFirst().orElseGet(null);
    }

    public void enableDependencies() {
        loadedDependencies.forEach(Dependency::onEnable);
    }

    public void disableDependencies() {
        this.loadedDependencies.forEach(Dependency::onDisable);
        this.loadedDependencies.clear();
    }

    public void loadDependencies(Dependency... dependencies) {
        for (Dependency dependency : dependencies) {
            loadedDependencies.add(dependency);
            dependency.onPreEnable();
        }
    }
}
