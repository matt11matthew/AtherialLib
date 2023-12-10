package me.matthewedevelopment.atheriallib.command;

public class DefaultCommand<S> {

    protected Command command;

    public DefaultCommand() {

    }

    /**
     * Setter for property 'command'.
     *
     * @param command Value to set for property 'command'.
     */
    public DefaultCommand<S> setCommand(Command command) {
        this.command = command;
        return this;
    }

    public void execute(S sender, String[] args) {}

    public Command getCommand() {
        return command;
    }
}