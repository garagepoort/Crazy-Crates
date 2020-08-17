package me.badbones69.crazycrates.commands;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.enums.Messages;
import me.badbones69.crazycrates.multisupport.Version;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class Set1Command implements CrazyCrateCommand {
    private CrazyCrates cc = CrazyCrates.getInstance();

    @Override
    public boolean executeCommand(CommandSender sender, Command cmd, String commandLable, String[] args) {
        if (!Methods.permCheck(sender, "admin")) return true;
        if (Version.getCurrentVersion().isOlder(Version.v1_13_R2)) {
            sender.sendMessage(Methods.getPrefix("&cThis command only works on 1.13+. If you wish to make schematics for 1.12.2- use World Edit to do so."));
            return true;
        }
        Player player = (Player) sender;
        int set = args[0].equalsIgnoreCase("set1") ? 1 : 2;
        Block block = player.getTargetBlockExact(10);
        if (block == null || block.isEmpty()) {
            player.sendMessage(Messages.MUST_BE_LOOKING_AT_A_BLOCK.getMessage());
            return true;
        }
        if (cc.getSchematicLocations().containsKey(player.getUniqueId())) {
            cc.getSchematicLocations().put(player.getUniqueId(), new Location[] {set == 1 ? block.getLocation() : cc.getSchematicLocations().getOrDefault(player.getUniqueId(), null)[0], set == 2 ? block.getLocation() : cc.getSchematicLocations().getOrDefault(player.getUniqueId(), null)[1]});
        } else {
            cc.getSchematicLocations().put(player.getUniqueId(), new Location[] {set == 1 ? block.getLocation() : null, set == 2 ? block.getLocation() : null});
        }
        player.sendMessage(Methods.getPrefix("&7You have set location #" + set + "."));
        return true;
        //Commented code is for debugging schematic files if there is an issue with them.
        //				}else if(args[0].equalsIgnoreCase("pasteall")) {// /cc pasteall
        //					if(!Methods.permCheck(sender, "admin")) return true;
        //					Location location = ((Player) sender).getLocation().subtract(0, 1, 0);
        //					for(CrateSchematic schematic : cc.getCrateSchematics()) {
        //						cc.getNMSSupport().pasteSchematic(schematic.getSchematicFile(), location);
        //						location.add(0, 0, 6);
        //					}
        //					sender.sendMessage(Methods.getPrefix("&7Pasted all of the schematics."));
        //					return true;
        //				}else if(args[0].equalsIgnoreCase("paste")) {// /cc paste <schematic file name>
        //					if(!Methods.permCheck(sender, "admin")) return true;
        //					if(args.length >= 2) {
        //						String name = args[1];
        //						Location location = ((Player) sender).getLocation().subtract(0, 1, 0);
        //						CrateSchematic schematic = cc.getCrateSchematic(name);
        //						if(schematic != null) {
        //							cc.getNMSSupport().pasteSchematic(schematic.getSchematicFile(), location);
        //							sender.sendMessage("Pasted the " + schematic.getSchematicName() + " schematic.");
        //						}else {
        //							sender.sendMessage(Methods.getPrefix("&cNo schematics by the name of " + name + " where found."));
        //						}
        //					}else {
        //						sender.sendMessage(Methods.getPrefix("&c/cc paste <schematic file name>"));
        //					}
        //					return true;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("set1", "set2");
    }
}
