package me.badbones69.crazycrates.commands;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.enums.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class HelpCommand implements CrazyCrateCommand {
    @Override
    public boolean executeCommand(CommandSender sender, Command cmd, String commandLable, String[] args) {
        if (!Methods.permCheck(sender, "access")) return true;
        sender.sendMessage(Messages.HELP.getMessage());
        return true;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("help");
    }
}
