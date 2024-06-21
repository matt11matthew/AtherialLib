package me.matthewedevelopment.atheriallib.minigame;

import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.config.SerializedName;
import me.matthewedevelopment.atheriallib.config.yaml.YamlConfig;
import me.matthewedevelopment.atheriallib.message.message.ChatMessage;

/**
 * Created by Matthew E on 6/16/2024 at 2:24 PM for the project AtherialLib
 */
public class GameMapConfig extends YamlConfig<AtherialLib> {
    @SerializedName("scoreboard.line")

    public String SCOREBOARD_LINE =  "&7&m-------------";

    public static GameMapConfig get() {
        return GameMapHandler.get().getConfig();
    }


    @SerializedName("settings.gameLobbyCountDown")
    public Integer LOBBY_COUNTDOWN = 45;
    @SerializedName("settings.defaultWorld")
    public String DEFAULT_WORLD_ZIP = "test.zip";



  

    @SerializedName("permissions.gamemap.create")
    public String GAME_MAP_CREATE_PERM = "atherial.gamemap.create";

    @SerializedName("permissions.gamemap.delete")
    public String GAME_MAP_DELETE_PERM = "atherial.gamemap.delete";

    @SerializedName("permissions.gamemap.edit")
    public String GAME_MAP_EDIT_PERM = "atherial.gamemap.edit";
    @SerializedName("permissions.server.setmainspawn")
    public String SET_MAIN_SPAWN_PERM = "permissions.server.setmainspawn";

    @SerializedName("permissions.gamemap.setlobby")
    public String SET_D_SPAWN_PERM = "permissions.gamemap.setlobby";
    @SerializedName("permissions.gamemap.save")
    public String GAME_MAP_SAVE_PERM = "atherial.gamemap.save";
    @SerializedName("permissions.gamemap.start")
    public String GAME_MAP_START_PERM = "atherial.gamemap.start";
    @SerializedName("permissions.gamemap.end")
    public String GAME_MAP_END_PERM = "atherial.gamemap.end";
    @SerializedName("editing.actionbar")
    public String EDITING_ACTIONBAR ="&cEditing &f%name%";





    @SerializedName("settings.prefix")
    public String PREFIX = "&f&l[&e&l"+AtherialLib.getInstance().getDescription().getName()+"&f&l]&7 ";
    @SerializedName("messages.gamemap.alreadyExists")
    public ChatMessage GAME_MAP_ALREADY_EXISTS = new ChatMessage(PREFIX + "&cThe gamemap &f%name%&c already exists!");
    @SerializedName("messages.gamemap.doesntExists")
    public ChatMessage GAME_MAP_DOESNT_EXISTS = new ChatMessage(PREFIX + "&cThe gamemap &f%name%&c doesn't exist!");

    @SerializedName("messages.gamemap.delete")
    public ChatMessage GAME_MAP_DELETED = new ChatMessage(PREFIX + "You have deleted &f%name%&7.");
    @SerializedName("messages.gamemap.notEditing")
    public ChatMessage GAME_MAP_NOT_EDITING_MSG = new ChatMessage(PREFIX + "&cThe gamemap &f%name%&c isn't currently being edited!");



    @SerializedName("messages.gamemap.cantStartEditing")
    public ChatMessage CANT_START_EDITING = new ChatMessage(PREFIX + "&cThe gamemap &f%name%&c is currently being &lEDITED&c!");
    @SerializedName("messages.gamemap.create")
    public ChatMessage GAME_MAP_CREATE_MSG = new ChatMessage(PREFIX + "Created gamemap &f%name%&7.");
    @SerializedName("messages.gamemap.edit")
    public ChatMessage GAME_MAP_EDIT_MSG = new ChatMessage(PREFIX + "Editing gamemap &f%name%&7.");
    @SerializedName("messages.gamemap.save")
    public ChatMessage GAME_MAP_SAVE_MSG = new ChatMessage(PREFIX + "Saved gamemap &f%name%&7.");

    @SerializedName("messages.gamemap.endCMD")
    public ChatMessage GAME_MAP_END_CMD_MSG = new ChatMessage(PREFIX + "Ended gamemap &f%name%&7.");
    @SerializedName("messages.lobby.cantsetlobbyspawn")
    public ChatMessage CANT_SET_LOBBY_SPAWN = new ChatMessage(PREFIX + "&cYou can only set lobby spawn in a normal world!");
    @SerializedName("messages.lobby.setlobbyspawn")
    public ChatMessage SET_LOBBY_SPAWN_MSG = new ChatMessage(PREFIX + "set lobby spawn.");
    @SerializedName("messages.gamemap.youMustBeInEdit")
    public ChatMessage MUST_BE_IN_EDIT_D_MSG = new ChatMessage(PREFIX + "&cYou must be in an edit gamemap to set gamemap lobby spawn!");


    @SerializedName("messages.misc.setPos1")
    public ChatMessage SET_POS_1 = new ChatMessage(PREFIX + "Set position #1 &f(%x%, %y%, %z%)");
    @SerializedName("messages.misc.setPos2")
    public ChatMessage SET_POS_2 = new ChatMessage(PREFIX + "Set position #2 &f(%x%, %y%, %z%)");




    @SerializedName("messages.misc.pleaseSetPositions")
    public ChatMessage PLEASE_SET_POS = new ChatMessage(PREFIX + "&cYou must select both positions");




    @SerializedName("messages.gamemap.setlobby")
    public ChatMessage GAME_MAP_SET_LOBBY_SPAWN_MSG = new ChatMessage(PREFIX + "set lobby spawn for gamemap &f%id%&e.");



    public ChatMessage NOT_IN_GAME_MSG =new ChatMessage(PREFIX +"&cYou're not in a game.");


    @SerializedName("permissions.debug")
    public String DEBUG_PERM = "permissions.debug";

    @SerializedName("messages.gamemapNotReady")
    public ChatMessage GAME_MAP_NOT_READY =new ChatMessage("&cThe gamemap is not fully setup!");



    public GameMapConfig() {
        super("game_settings.yml", AtherialLib.getInstance());
        loadConfig();
    }
}
