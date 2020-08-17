package me.badbones69.crazycrates.commands;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.enums.Messages;
import me.badbones69.crazycrates.multisupport.Support;
import me.badbones69.crazycrates.multisupport.converters.CratesPlusConverter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class ConvertCommand implements CrazyCrateCommand {
    private CrazyCrates cc = CrazyCrates.getInstance();

    @Override
    public boolean executeCommand(CommandSender sender, Command cmd, String commandLable, String[] args) {
        if (!Methods.permCheck(sender, "admin")) return true;
        if (Support.CRATESPLUS.isPluginLoaded()) {
            try {
                CratesPlusConverter.convert();
                sender.sendMessage(Messages.CONVERT_CRATES_PLUS.getMessage("%Prefix%", Methods.getPrefix()));
            } catch (Exception e) {
                sender.sendMessage(Messages.ERROR_CONVERTING_FILES.getMessage());
                System.out.println("Error while trying to convert files with Crazy Crates v" + cc.getPlugin().getDescription().getVersion());
                e.printStackTrace();
            }
        } else {
            sender.sendMessage(Messages.NO_FILES_TO_CONVERT.getMessage());
        }
        return true;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("convert");
    }
}
