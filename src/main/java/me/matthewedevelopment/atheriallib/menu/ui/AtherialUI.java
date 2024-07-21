package me.matthewedevelopment.atheriallib.menu.ui;

import dev.triumphteam.gui.guis.Gui;
import me.matthewedevelopment.atheriallib.config.yaml.YamlConfig;

import java.util.Collections;
import java.util.List;

public abstract  class AtherialUI<C extends YamlConfig> {
    protected C c;
    protected Gui menu;

    public AtherialUI(C c) {
        this.c = c;
    }

    public  <E> List<E> getPageItems(List<E> allItems, int amountPerPage, int currentPage) {
        if (amountPerPage <= 0) {
            throw new IllegalArgumentException("amountPerPage must be greater than zero");
        }
        if (currentPage <= 0) {
            throw new IllegalArgumentException("currentPage must be greater than zero");
        }

        int startIndex = (currentPage - 1) * amountPerPage;
        if (startIndex >= allItems.size()) {
            return Collections.emptyList(); // Return an empty list if the start index is beyond the list size
        }

        int endIndex = Math.min(startIndex + amountPerPage, allItems.size());
        return allItems.subList(startIndex, endIndex);
    }
    public  <E> int getMaxPage(List<E> items, int amountPerPage) {
        if (amountPerPage <= 0) {
            throw new IllegalArgumentException("amountPerPage must be greater than zero");
        }

        // No need for a loop - calculate the maximum page directly
        int totalItems = items.size();
        return (totalItems == 0) ? 1 : (int) Math.ceil((double) totalItems / amountPerPage);
    }
}
