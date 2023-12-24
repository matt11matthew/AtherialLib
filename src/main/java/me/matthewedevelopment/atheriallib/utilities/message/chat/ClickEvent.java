package me.matthewedevelopment.atheriallib.utilities.message.chat;

/**
 * Created by Matthew E on 5/25/2019 at 12:13 PM for the project atherialapi
 */
public class ClickEvent {
    private final Action action;
    private final String value;

    public ClickEvent(Action action, String value) {
        this.action = action;
        this.value = value;
    }

    public Action getAction() {
        return action;
    }

    public String getValue() {
        return value;
    }

    public static ClickEvent openUrl(String url) {
        return new ClickEvent(Action.OPEN_URL, url);
    }

    public static ClickEvent openFile(String file) {
        return new ClickEvent(Action.OPEN_FILE, file);
    }

    public static ClickEvent runCommand(String command) {
        return new ClickEvent(Action.RUN_COMMAND, command);
    }

    public static ClickEvent suggestCommand(String command) {
        return new ClickEvent(Action.SUGGEST_COMMAND, command);
    }

    public static ClickEvent changePage(String page) {
        return new ClickEvent(Action.CHANGE_PAGE, page);
    }

    public static enum Action {
        OPEN_URL,
        OPEN_FILE,
        RUN_COMMAND,
        SUGGEST_COMMAND,
        CHANGE_PAGE;

        private Action() {
        }

        public net.md_5.bungee.api.chat.ClickEvent.Action toBungee() {
            net.md_5.bungee.api.chat.ClickEvent.Action action;
            try {

                action = net.md_5.bungee.api.chat.ClickEvent.Action.valueOf(this.toString());
            } catch (Exception e) {
                return null;
            }
            return action;
        }
    }
}
