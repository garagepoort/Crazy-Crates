package me.badbones69.crazycrates.commands;

import me.badbones69.crazycrates.Methods;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface CrazyCrateCommand {

    default boolean execute(CommandSender sender, Command cmd, String commandLable, String[] args) {
        try {
            return executeCommand(sender, cmd, commandLable, args);
        } catch (CommandInputException e) {
            sender.sendMessage(Methods.color(Methods.getPrefix() + e.getMessage()));
            return true;
        }
    }

    boolean executeCommand(CommandSender sender, Command cmd, String commandLable, String[] args);

    List<String> getAliases();
}
