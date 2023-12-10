package me.matthewedevelopment.atheriallib.command.spigot;

/**
 * Created by Matthew E on 5/25/2019 at 6:02 PM for the project atherialapi
 */
public class HelpSubCommand {
    private String command = "/command";
    private String description = "Please provide a description";
    private String permission = "atherial.use";
    private String[] arguments = new String[0];

    public HelpSubCommand(String command, String description, String permission, String... arguments) {
        this.command = command;
        this.description = description;
        this.permission = permission;
        this.arguments = arguments;
    }

    private HelpSubCommand(Builder builder) {
        command = builder.command;
        description = builder.description;
        permission = builder.permission;
        arguments = builder.arguments;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(HelpSubCommand copy) {
        Builder builder = new Builder();
        builder.command = copy.getCommand();
        builder.description = copy.getDescription();
        builder.arguments = copy.getArguments();
        builder.permission = copy.getPermission();
        return builder;
    }

    public String getPermission() {
        return permission;
    }

    public String getCommand() {
        return command;
    }

    public String getDescription() {
        return description;
    }

    public String[] getArguments() {
        return arguments;
    }

    /**
     * {@code HelpSubCommand} builder static inner class.
     */
    public static final class Builder {
        private String command = "/command";
        private String description = "Please provide a description";
        private String permission = "atherial.use";
        private String[] arguments = new String[0];

        private Builder() {
        }

        /**
         * Sets the {@code command} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param command the {@code command} to set
         * @return a reference to this Builder
         */
        public Builder command(String command) {
            this.command = command;
            return this;
        }

        /**
         * Sets the {@code description} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param description the {@code description} to set
         * @return a reference to this Builder
         */
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder permission(String permission) {
            this.permission = permission;
            return this;
        }

        /**
         * Sets the {@code arguments} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param arguments the {@code arguments} to set
         * @return a reference to this Builder
         */
        public Builder arguments(String... arguments) {
            this.arguments = arguments;
            return this;
        }

        /**
         * Returns a {@code HelpSubCommand} built from the parameters previously set.
         *
         * @return a {@code HelpSubCommand} built with parameters of this {@code HelpSubCommand.Builder}
         */
        public HelpSubCommand build() {
            return new HelpSubCommand(this);
        }
    }
}
