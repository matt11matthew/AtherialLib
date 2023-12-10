package me.matthewedevelopment.atheriallib.command.spigot;


import me.matthewedevelopment.atheriallib.command.AnnotationlessAtherialCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static me.matthewedevelopment.atheriallib.utilities.ChatUtils.formatEnum;


/**
 * Created by Matthew E on 5/25/2019 at 5:56 PM for the project atherialapi
 */
public abstract class SpigotCommand extends AnnotationlessAtherialCommand {
    protected boolean playerOnly = false;
    protected String permission = null;
    private Map<String, SpigotCommand> subCommandMap;

    public SpigotCommand(String name, String... aliases) {
        super(name, "/" + name, name, aliases);

        this.subCommandMap = new ConcurrentHashMap<>();
    }

    public void addSubCommand(SpigotCommand spigotCommand) {
        if (!subCommandMap.containsKey(spigotCommand.getName().toLowerCase())) {
            subCommandMap.put(spigotCommand.getName().toLowerCase(), spigotCommand);
        }
    }

    public Map<String, SpigotCommand> getSubCommandMap() {
        return subCommandMap;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (checkPlayerOnly(sender)) {
            return;
        }
        if (!checkPermission(sender)) {
            return;
        }
        if (args.length >= 1) {
            if (subCommandMap.containsKey(args[0].toLowerCase())) {
                SpigotCommand spigotCommand = subCommandMap.get(args[0].toLowerCase());
                spigotCommand.execute(sender, getArgs(args, 1));
            } else {
                run(sender, args);
            }
        } else {
            run(sender, args);
        }
    }

    private boolean checkPermission(CommandSender sender) {
        if (permission == null) {
            return true;
        }
        if (!sender.hasPermission(permission)) {
            CommandUtils.sendNoPermissionMessage(sender, permission);
            return false;
        }
        return true;
    }

    public boolean checkPlayerOnly(CommandSender sender) {
        if (this.playerOnly && (!(sender instanceof Player))) {
            CommandUtils.sendPlayerOnlyMessage(sender);
            return true;
        }
        return false;
    }

    public String[] getArgs(String[] input, int start) {
        List<String> newArgs = new ArrayList<>();
        if (input.length - 1 == start) {
            newArgs.add(input[1]);
        } else {
            for (int i = start; i < input.length; i++) {
                if (input[i] != null) {
                    newArgs.add(input[i]);
                }
            }
        }
        return newArgs.toArray(new String[0]);
    }

    public abstract void run(CommandSender sender, String[] args);

    public abstract List<HelpSubCommand> getHelp(String[] args);


    public void sendHelp(CommandSender sender, String[] args) {

        List<HelpSubCommand> help = getHelp(args);
        List<HelpSubCommand> newHelp = new ArrayList<>();
        if (help != null) {

            help.stream().filter(helpSubCommand -> sender.hasPermission(helpSubCommand.getPermission())).forEach(newHelp::add);
        }
        if (!newHelp.isEmpty()) {
            CommandUtils.sendCommandHelp(sender, formatEnum(this.getName()), newHelp.toArray(new HelpSubCommand[0]));

        }
    }
}
