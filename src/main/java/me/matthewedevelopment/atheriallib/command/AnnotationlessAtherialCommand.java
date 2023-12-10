package me.matthewedevelopment.atheriallib.command;

/**
 * Created by Matthew Eisenberg on 8/18/2018 at 5:05 PM for the project atherialapi
 */
public abstract class AnnotationlessAtherialCommand extends AtherialCommand {
    private String name;
    private String usage;
    private String description;
    private String[] aliases;

    public AnnotationlessAtherialCommand(String name, String usage, String description, String... aliases) {
        this.name = name;
        this.usage = usage;
        this.description = description;
        this.aliases = aliases;
    }

    public AnnotationlessAtherialCommand(String name, String usage, String description) {
        this(name, usage, description, new String[]{});
    }

    public AnnotationlessAtherialCommand(String name, String description) {
        this(name, "", description, new String[]{});
    }

    public AnnotationlessAtherialCommand(String name, String... aliases) {
        this(name, "", "", aliases);
    }

    public AnnotationlessAtherialCommand(String name) {
        this(name, "", "", new String[]{});
    }


    public String getUsage() {
        return usage;
    }

    public String getDescription() {
        return description;
    }

    public String[] getAliases() {
        return aliases;
    }

    public String getName() {
        return name;
    }
}
