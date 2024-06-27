package me.matthewedevelopment.atheriallib;

import me.matthewedevelopment.atheriallib.chat.ChatPromptHandler;
import me.matthewedevelopment.atheriallib.command.AnnotationlessAtherialCommand;
import me.matthewedevelopment.atheriallib.command.AtherialCommand;
import me.matthewedevelopment.atheriallib.command.spigot.AtherialLibSpigotCommand;
import me.matthewedevelopment.atheriallib.command.spigot.config.SelfCommandConfig;
import me.matthewedevelopment.atheriallib.command.spigot.serializers.SelfCommandConfigSerializer;
import me.matthewedevelopment.atheriallib.command.spigot.serializers.UsageSerializer;
import me.matthewedevelopment.atheriallib.config.sound.AtherialSound;
import me.matthewedevelopment.atheriallib.config.sound.AtherialSoundSerializer;
import me.matthewedevelopment.atheriallib.config.yaml.AtherialLibItem;
import me.matthewedevelopment.atheriallib.config.yaml.CustomTypeRegistry;
import me.matthewedevelopment.atheriallib.config.yaml.serializables.AtherialItemBuilderSerializable;
import me.matthewedevelopment.atheriallib.config.yaml.serializables.AtherialLibItemSerializable;
import me.matthewedevelopment.atheriallib.config.yaml.serializables.list.DoubleSimpleList;
import me.matthewedevelopment.atheriallib.config.yaml.serializables.list.IntSimpleList;
import me.matthewedevelopment.atheriallib.config.yaml.serializables.list.StringSimpleList;
import me.matthewedevelopment.atheriallib.config.yaml.serializables.list.serializer.DoubleSimpleListSerializer;
import me.matthewedevelopment.atheriallib.config.yaml.serializables.list.serializer.IntSimpleListSerializer;
import me.matthewedevelopment.atheriallib.config.yaml.serializables.list.serializer.StringSimpleListSerializer;
import me.matthewedevelopment.atheriallib.database.mysql.MySqlHandler;
import me.matthewedevelopment.atheriallib.dependency.DependencyManager;
import me.matthewedevelopment.atheriallib.item.AtherialItemAPI;
import me.matthewedevelopment.atheriallib.item.AtherialItemBuilder;
import me.matthewedevelopment.atheriallib.menu.HotBarListener;
import me.matthewedevelopment.atheriallib.menu.gui.AtherialMenuRegistry;
import me.matthewedevelopment.atheriallib.menu.gui.speed.FastAtherialMenuRegistry;
import me.matthewedevelopment.atheriallib.message.message.ActionBarMessage;
import me.matthewedevelopment.atheriallib.message.message.ChatMessage;
import me.matthewedevelopment.atheriallib.message.message.MessageTitle;
import me.matthewedevelopment.atheriallib.message.message.json.ActionBarMessageSerializer;
import me.matthewedevelopment.atheriallib.message.message.json.ChatMessageSerializer;
import me.matthewedevelopment.atheriallib.message.message.json.TitleJsonSerializer;
import me.matthewedevelopment.atheriallib.message.title.AtherialTitle;
import me.matthewedevelopment.atheriallib.minigame.GameMapHandler;
import me.matthewedevelopment.atheriallib.minigame.load.edit.EditLoadedGameMap;
import me.matthewedevelopment.atheriallib.minigame.load.game.GameLoadedGameMap;
import me.matthewedevelopment.atheriallib.newcommand.AtherialLibDefaultCommandConfig;
import me.matthewedevelopment.atheriallib.nms.Version;
import me.matthewedevelopment.atheriallib.nms.VersionProvider;
import me.matthewedevelopment.atheriallib.playerdata.AtherialProfile;
import me.matthewedevelopment.atheriallib.playerdata.AtherialProfileManager;
import me.matthewedevelopment.atheriallib.utilities.AtherialTasks;
import me.matthewedevelopment.atheriallib.utilities.location.AtherialLocation;
import me.matthewedevelopment.atheriallib.utilities.location.AtherialLocationSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Slime;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import spigui.SpiGUI;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

public abstract class AtherialLib extends JavaPlugin implements Listener {
    protected VersionProvider versionProvider;

    protected DependencyManager dependencyManager;
//    protected HandlerManager handlerManager;

    public static AtherialLib getInstance() {
        return instance;
    }
    public abstract void onStart();
    public abstract void onStop();

    private static AtherialLib instance;
    private AtherialMenuRegistry atherialMenuRegistry;
    private FastAtherialMenuRegistry fastAtherialMenuRegistry;
    private SpiGUI menu;
    public abstract void initDependencies();

    public FastAtherialMenuRegistry getFastAtherialMenuRegistry() {
        return fastAtherialMenuRegistry;
    }

    private MySqlHandler sqlHandler;

    private boolean disableSQLLogin = false;
    private GameMapHandler gameHandler;



    public void setupGame(String gameName, Class<? extends GameLoadedGameMap<?>> liveClass, Class<? extends
            EditLoadedGameMap<?>> editClass, Class<?> gameMapDataClass) {
        gameHandler =new GameMapHandler(this);
        gameHandler.setGameName(gameName);
        gameHandler.setEditClass(editClass);
        gameHandler.setLiveClass(liveClass);
        gameHandler.setGameDataClass(gameMapDataClass);

        getLogger().info("=====================================");
        getLogger().info("Setup game " + gameName);
        getLogger().info("=====================================");

        if (started){
        gameHandler.start();
        }
    }
    public void setDisableSQLLogin(boolean disableSQLLogin) {
        this.disableSQLLogin = disableSQLLogin;
    }

    public boolean isDisableSQLLogin() {
        return disableSQLLogin;
    }

    private  boolean debug;

    private AtherialLibDefaultCommandConfig commandConfig;

    protected AtherialProfileManager profileManager;

    protected ChatPromptHandler chatPromptHandler;

    public AtherialLib() {
        instance = this;
        this.debug = false;
        this.dependencyManager = new DependencyManager();
        this.sqlHandler = new MySqlHandler(this);
        this.nmsRequired = false;
//        initDependencies();
        AtherialTasks.setPlugin(this);
//        handlerManager =new HandlerManager(this);
        AtherialTitle.setAtherialPlugin(this);
    }

    public ChatPromptHandler getChatPromptHandler() {
        return chatPromptHandler;
    }

    public void registerListener(Listener listener){
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    public void enableMySql() {
        this.sqlHandler.setEnabled(true);
        this.sqlHandler.setLite(false);
    }
    public void enableLiteSql() {
        this.sqlHandler.setEnabled(true);
        this.sqlHandler.setLite(true);
    }

    public DependencyManager getDependencyManager() {
        return dependencyManager;
    }

    public AtherialLib setNmsRequired(boolean nmsRequired) {
        this.nmsRequired = nmsRequired;
        return this;
    }
    private boolean guiEnabled = true;
    public AtherialLib setGUIDisabled() {
        this.guiEnabled = false;

        return this;
    }

    public VersionProvider getVersionProvider() {
        return versionProvider;
    }


    public void registerHandlers() {

    }
    private boolean started = false;
    @Override
    public void onEnable() {
        initDependencies();
        if(!loadNMS()){
            if (nmsRequired){
                getLogger().severe("NMS is required for this plugin!");
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
        }

        defaultRegisterTypes();
        registerTypes();
        this.commandConfig = new AtherialLibDefaultCommandConfig(this);
        this.commandConfig.loadConfig();


        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        registerListener(new HotBarListener(this));
        if (sqlHandler.isEnabled()) {
            sqlHandler.start();
        }
        AtherialItemAPI.setAtherialLib(this);
        getServer().getPluginManager().registerEvents(this, this);
        this.menu=  new SpiGUI(this);

        this.profileManager = new AtherialProfileManager(this);
        getServer().getPluginManager().registerEvents(  this.profileManager, this);

        this.dependencyManager.enableDependencies();

//        registerListener(new PlayerJumpListener());
        atherialMenuRegistry = new AtherialMenuRegistry();
        atherialMenuRegistry.start();


        fastAtherialMenuRegistry=new FastAtherialMenuRegistry();
        fastAtherialMenuRegistry.start();

        this.chatPromptHandler= new ChatPromptHandler();
        registerListener(chatPromptHandler);
        registerHandlers();
        this.onStart();
//        handlerManager.enableHandlers();


        this.profileManager.load();
        if (gameHandler!=null&&gameHandler.isSetup()) {
            gameHandler.start();
        }


        started=true;
        if (debug) {
            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
                @Override
                public void run() {
                    for (World world : Bukkit.getServer().getWorlds()) {
                        if (!slimesEnabled){
                            world.getEntitiesByClass(Slime.class).forEach(Entity::remove);
                        }

                        world.setStorm(false);
                        world.setThundering(false);
                        world.setTime(500);
                        world.setDifficulty(Difficulty.EASY);
                    }
                }
            }, 20L, 20); // Schedule this to run every 5 minutes, for example
        }

    }
    public void onPostProfileLoad() {

    }

    private boolean slimesEnabled = false;

    public boolean isSlimesEnabled() {
        return slimesEnabled;
    }

    public void setSlimesEnabled(boolean slimesEnabled) {
        this.slimesEnabled = slimesEnabled;
    }

    public AtherialMenuRegistry getMenuRegistry() {
        return atherialMenuRegistry;
    }

    public abstract void registerTypes();

    private void defaultRegisterTypes() {
        CustomTypeRegistry.registerType(AtherialItemBuilder.class, new AtherialItemBuilderSerializable());
        CustomTypeRegistry.registerType(AtherialLibItem.class, new AtherialLibItemSerializable());


        CustomTypeRegistry.registerType(ActionBarMessage.class, new ActionBarMessageSerializer());
        CustomTypeRegistry.registerType(MessageTitle.class, new TitleJsonSerializer());
        CustomTypeRegistry.registerType(ChatMessage.class, new ChatMessageSerializer());


        CustomTypeRegistry.registerType(IntSimpleList.class, new IntSimpleListSerializer());
        CustomTypeRegistry.registerType(DoubleSimpleList.class, new DoubleSimpleListSerializer());
        CustomTypeRegistry.registerType(StringSimpleList.class, new StringSimpleListSerializer());


        CustomTypeRegistry.registerType(SelfCommandConfig.Usage.class, new UsageSerializer());
        CustomTypeRegistry.registerType(SelfCommandConfig.class, new SelfCommandConfigSerializer());

        CustomTypeRegistry.registerType(AtherialLocation.class, new AtherialLocationSerializer());

        CustomTypeRegistry.registerType(AtherialSound.class, new AtherialSoundSerializer());

    }





    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public MySqlHandler getSqlHandler() {
        return sqlHandler;
    }

    public AtherialProfileManager getProfileManager() {
        return profileManager;
    }

    public SpiGUI getMenu() {
        return menu;
    }

    private String sqlURL = null;

    public void setSqlURL(String sqlURL) {
        this.sqlURL = sqlURL;
    }

    public String getSqlURL() {
        return sqlURL;
    }

    private boolean nmsEnabled;
    protected boolean nmsRequired ;

    public boolean isNmsEnabled() {
        return nmsEnabled;
    }

    private boolean loadNMS() {
        try {
            Version version = Version.getVersion();
            if (version == null) {
                this.getLogger().warning("[NMSDependency] Failed to load version.");
//                Bukkit.getPluginManager().disablePlugin(this);
                this.nmsEnabled = false;
                return false;
            } else {
                this.nmsEnabled = true;
                this.versionProvider = version.getVersionProviderClass().getConstructor(AtherialLib.class).newInstance(this);
                this.getLogger().info("[NMSDependency] Loaded new NMS version " + this.versionProvider.getVersion().getVersionString());
                return true;
            }
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public void onDisable() {
        profileManager.stop();
//        handlerManager.disableHandlers();
        if (gameHandler!=null&&gameHandler.isSetup()) {
            gameHandler.stop();
        }
        this.onStop();
        if (sqlHandler.isEnabled()) {
            sqlHandler.stop();
        }


        this.dependencyManager.disableDependencies();
    }


    public void reloadConfigs() {

        this.commandConfig.reload();

        this.handleConfigReloads();
    }

    protected abstract void handleConfigReloads();

    public void registerCommand(AtherialLibSpigotCommand atherialLibSpigotCommand) {
        registerAtherialCommand(atherialLibSpigotCommand);
    }
    public void registerAtherialCommand(AtherialCommand command) {

        if (command instanceof AnnotationlessAtherialCommand) {
            AnnotationlessAtherialCommand atherialCommand = (AnnotationlessAtherialCommand) command;
            me.matthewedevelopment.atheriallib.command.Command annotationCommand = new me.matthewedevelopment.atheriallib.command.Command() {

                @Override
                public Class<? extends Annotation> annotationType() {
                    return me.matthewedevelopment.atheriallib.command.Command.class;
                }

                @Override
                public String name() {
                    return atherialCommand.getName();
                }

                @Override
                public String[] aliases() {
                    return atherialCommand.getAliases();
                }

                @Override
                public String description() {
                    return atherialCommand.getDescription();
                }

                @Override
                public String usage() {
                    return atherialCommand.getUsage();
                }
            };
            command.setCommand(annotationCommand);
        } else {
            me.matthewedevelopment.atheriallib.command.Command annotationCommand = command.getClass().getAnnotation(me.matthewedevelopment.atheriallib.command.Command.class);
            if (annotationCommand != null) {
                command.setCommand(annotationCommand);
            }
        }
        try {


            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap spigotCommandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
            ReflectCommand reflectCommand = new ReflectCommand(command.getCommand().name());
            reflectCommand.setAliases(Arrays.asList(command.getCommand().aliases()));
            reflectCommand.setDescription(command.getCommand().description());
            reflectCommand.setUsage(command.getCommand().usage());
            reflectCommand.setExecutor(command);


            boolean register = spigotCommandMap.register(reflectCommand.spigotCommand.getCommand().name(), reflectCommand);
            if (register) {
                System.out.println("[" + getDescription().getName() + "] Registered command /" + command.getCommand().name());
//                getCommand(command.getCommand().name()).setTabCompleter(command);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AtherialLibDefaultCommandConfig getCommandConfig() {
        return commandConfig;
    }

    public abstract List<Class<? extends AtherialProfile>> getProfileClazzes();

    public GameMapHandler getGameMapHandler() {
        return gameHandler;
    }

    public static final class ReflectCommand extends Command {
        public AtherialCommand spigotCommand;

        public ReflectCommand(String command) {
            super(command);
        }

        public void setExecutor(AtherialCommand exe) {
            this.spigotCommand = exe;
        }



        @Override
        public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
            return spigotCommand.onTabComplete(sender,this,alias,args);
        }

        @Override
        public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            return spigotCommand != null && spigotCommand.onCommand(sender, this, commandLabel, args);
        }
    }

}
