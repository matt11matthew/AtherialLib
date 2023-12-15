package me.matthewedevelopment.atheriallib.playerdata;

/**
 * Created by Matthew E on 12/15/2023 at 1:11 PM for the project AtherialLib
 */
public enum Toggle {
    ON(true), OFF(false);

    private boolean status;

    Toggle(boolean status) {
        this.status = status;
    }

    public static Toggle getByValue(boolean value){
        return value ? ON : OFF;
    }
    public boolean isStatus() {
        return status;
    }
}
