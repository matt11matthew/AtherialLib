package me.matthewedevelopment.atheriallib.newcommand;

import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.config.SerializedName;
import me.matthewedevelopment.atheriallib.config.yaml.YamlConfig;
import me.matthewedevelopment.atheriallib.message.message.ChatMessage;

/**
 * Created by Matthew E on 12/30/2023 at 6:40 PM for the project AtherialLib
 */
public class AtherialLibDefaultCommandConfig  extends YamlConfig {

    @SerializedName("correctUsage")
    public String CORRECT_USAGE_MESSAGE ="&c&lCorrect Usage: &7%command% &7%arguments%";

    @SerializedName("playerOnly")
    public ChatMessage PLAYER_ONLY_MESSAGE = new ChatMessage("&cThis command is player-only.");
    @SerializedName("noPermission")
    public ChatMessage NO_PERMISSION_MESSAGE = new ChatMessage("&cYou lack the permission %permission%");

    @SerializedName("consoleOnly")
    public ChatMessage CONSOLE_ONLY_MESSAGE = new ChatMessage("&cThis command is console-only.");

    @SerializedName("pDoesntExist")
    public ChatMessage P_DOESNT_EXIST = new ChatMessage("&c%player% is &lOFFLINE&c.");
    @SerializedName("invalidNum")
    public ChatMessage INVALID_NUM = new ChatMessage("&cPlease enter a valid number!");
    @SerializedName("invalidNumWith")
    public ChatMessage INVALID_NUM_WITH = new ChatMessage("&cThe number %num% is invalid!");


    @SerializedName("help.header") public String HELP_HEADER = "&7&m----------&f[&a&l%command% Help&f]&7&m----------";
    @SerializedName("help.footer") public String HELP_FOOTER = "&7&m----------&f[&a&l%command% Help&f]&7&m----------";
    @SerializedName("help.line") public String HELP_LINE = "&a/%command% &8- &7%description%";
    @SerializedName("help.argumentsColor") public String HELP_ARGUMENTS_COLOR= "&f";
    public AtherialLibDefaultCommandConfig( AtherialLib plugin) {
        super("commands.yml", plugin);
    }
}