package me.matthewedevelopment.atheriallib.handler;

public enum HandlerPriority {
    LOW(1), NORMAL(2), HIGH(3), HIGHEST(4);
    private int priority;

    HandlerPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}