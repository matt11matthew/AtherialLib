package me.matthewedevelopment.atheriallib.menu.ui;

import net.kyori.adventure.text.Component;

public class UIInformation
{
    private Component title;
    private int rows;

    public UIInformation(Component title, int rows) {
        this.title = title;
        this.rows = rows;
    }

    public Component getTitle() {
        return title;
    }

    public int getRows() {
        return rows;
    }
}
