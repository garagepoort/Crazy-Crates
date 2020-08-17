package me.badbones69.crazycrates.commands;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.multisupport.Version;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class SaveCommand implements CrazyCrateCommand {
    private CrazyCrates cc = CrazyCrates.getInstance();

    @Override
    public boolean executeCommand(CommandSender sender, Command cmd, String commandLable, String[] args) {
        if (!Methods.permCheck(sender, "admin")) return true;
        if (Version.getCurrentVersion().isOlder(Version.v1_13_R2)) {
            sender.sendMessage(Methods.getPrefix("&cThis command only works on 1.13+. If you wish to make schematics for 1.12.2- use World Edit to do so."));
            return true;
        }
        Location[] locations = cc.getSchematicLocations().get(((Player) sender).getUniqueId());
        if (locations != null && locations[0] != null && locations[1] != null) {
            if (args.length >= 2) {
                File file = new File(cc.getPlugin().getDataFolder() + "/Schematics/" + args[1]);
                cc.getNMSSupport().saveSchematic(locations, sender.getName(), file);
                sender.sendMessage(Methods.getPrefix("&7Saved the " + args[1] + ".nbt into the Schematics folder."));
                cc.loadSchematics();
            } else {
                sender.sendMessage(Methods.getPrefix("&cYou need to specify a schematic file name."));
            }
        } else {
            sender.sendMessage(Methods.getPrefix("&cYou need to use /cc set1/set2 to set the connors of your schematic."));
        }
        return true;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("save");
    }
}
