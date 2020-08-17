package me.badbones69.crazycrates.commands;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrateService;
import me.badbones69.crazycrates.api.FileManager;
import me.badbones69.crazycrates.api.enums.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;

public class ReloadCommand implements CrazyCrateCommand {
    private CrateService crateService = CrateService.getInstance();
    private FileManager fileManager = FileManager.getInstance();

    @Override
    public boolean executeCommand(CommandSender sender, Command cmd, String commandLable, String[] args) {

        Plugin plugin = Bukkit.getPluginManager().getPlugin("CrazyCrates");
        if (!Methods.permCheck(sender, "admin")) return true;
        fileManager.reloadAllFiles();
        fileManager.setup(plugin);
        if (!FileManager.Files.LOCATIONS.getFile().contains("Locations")) {
            FileManager.Files.LOCATIONS.getFile().set("Locations.Clear", null);
            FileManager.Files.LOCATIONS.saveFile();
        }
        if (!FileManager.Files.DATA.getFile().contains("Players")) {
            FileManager.Files.DATA.getFile().set("Players.Clear", null);
            FileManager.Files.DATA.saveFile();
        }
        crateService.loadCrates();
        sender.sendMessage(Messages.RELOAD.getMessage());
        return true;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("reload");
    }
}
