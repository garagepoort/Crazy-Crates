package me.badbones69.crazycrates.commands;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class TpCommand implements CrazyCrateCommand {
    private CrazyCrates cc = CrazyCrates.getInstance();

    @Override
    public boolean executeCommand(CommandSender sender, Command cmd, String commandLable, String[] args) {
        if (!Methods.permCheck(sender, "admin")) return true;
        if (args.length == 2) {
            String Loc = args[1];
            if (!FileManager.Files.LOCATIONS.getFile().contains("Locations")) {
                FileManager.Files.LOCATIONS.getFile().set("Locations.Clear", null);
                FileManager.Files.LOCATIONS.saveFile();
            }
            for (String name : FileManager.Files.LOCATIONS.getFile().getConfigurationSection("Locations").getKeys(false)) {
                if (name.equalsIgnoreCase(Loc)) {
                    World W = Bukkit.getServer().getWorld(FileManager.Files.LOCATIONS.getFile().getString("Locations." + name + ".World"));
                    int X = FileManager.Files.LOCATIONS.getFile().getInt("Locations." + name + ".X");
                    int Y = FileManager.Files.LOCATIONS.getFile().getInt("Locations." + name + ".Y");
                    int Z = FileManager.Files.LOCATIONS.getFile().getInt("Locations." + name + ".Z");
                    Location loc = new Location(W, X, Y, Z);
                    ((Player) sender).teleport(loc.add(.5, 0, .5));
                    sender.sendMessage(Methods.color(Methods.getPrefix() + "&7You have been teleported to &6" + name + "&7."));
                    return true;
                }
            }
            sender.sendMessage(Methods.color(Methods.getPrefix() + "&cThere is no location called &6" + Loc + "&c."));
            return true;
        }
        sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/cc TP <Location Name>"));
        return true;
    }
    @Override
    public List<String> getAliases() {
        return Arrays.asList("tp");
    }

}
