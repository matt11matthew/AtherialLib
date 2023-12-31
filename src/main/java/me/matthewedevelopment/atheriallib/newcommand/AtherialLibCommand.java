package me.matthewedevelopment.atheriallib.newcommand;

/**
 * Created by Matthew E on 12/30/2023 at 6:39 PM for the project AtherialLib
 */
public abstract class AtherialLibCommand {
    private String name;
    private String permission;

    public AtherialLibCommand(String name, String permission) {
        this.name = name;
        this.permission = permission;
    }
}
