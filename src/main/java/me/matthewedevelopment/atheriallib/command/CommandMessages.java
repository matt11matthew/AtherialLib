package me.matthewedevelopment.atheriallib.command;

/**
 * Created by Matthew E on 5/25/2019 at 5:58 PM for the project atherialapi
 */
public interface CommandMessages {
    String getNoPermissionMessage();


    String getHelpArgumentsColor();

    void load();
    String getHelpLine();
    String getHelpHeader();
    String getHelpFooter();

    String getPlayerOnlyCommandMessage();

    String getCorrectCommandArgumentUsage();

    String getCorrectCommandUsage();
}
