package me.matthewedevelopment.atheriallib.dependency.luckperms;

public class Rank {
    private String group;
    private String prefix;
    private int weight;

    public Rank(String group, String prefix, int weight) {
        this.group = group;
        this.prefix = prefix;
        this.weight = weight;

    }

    public String getGroup() {
        return group;
    }

    public String getPrefix() {
        return prefix;
    }

    public int getWeight() {
        return weight;
    }
}
