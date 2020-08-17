package me.badbones69.crazycrates.commands;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.enums.CrateType;
import me.badbones69.crazycrates.api.enums.KeyType;
import me.badbones69.crazycrates.api.enums.Messages;
import me.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ForceOpenCommand implements CrazyCrateCommand {
    private CrazyCrates cc = CrazyCrates.getInstance();

    @Override
    public boolean executeCommand(CommandSender sender, Command cmd, String commandLable, String[] args) {
        if (!Methods.permCheck(sender, "forceopen")) return true;
        if (args.length >= 2) {
            for (Crate crate : cc.getCrates()) {
                if (crate.getCrateType() != CrateType.MENU) {
                    if (crate.getName().equalsIgnoreCase(args[1])) {
                        Player player;
                        if (args.length >= 3) {
                            if (Methods.isOnline(args[2], sender)) {
                                player = Methods.getPlayer(args[2]);
                            } else {
                                return true;
                            }
                        } else {
                            if (!(sender instanceof Player)) {
                                sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/Crate forceopen <Crate> [Player]"));
                                return true;
                            } else {
                                player = (Player) sender;
                            }
                        }
                        if (CrazyCrates.getInstance().isInOpeningList(player)) {
                            sender.sendMessage(Messages.CRATE_ALREADY_OPENED.getMessage());
                            return true;
                        }
                        CrateType type = crate.getCrateType();
                        if (type != null) {
                            if (type != CrateType.CRATE_ON_THE_GO && type != CrateType.QUICK_CRATE && type != CrateType.FIRE_CRACKER) {
                                cc.openCrate(player, crate, KeyType.FREE_KEY, player.getLocation(), true, false);
                                HashMap<String, String> placeholders = new HashMap<>();
                                placeholders.put("%Crate%", crate.getName());
                                placeholders.put("%Player%", player.getName());
                                sender.sendMessage(Messages.OPENED_A_CRATE.getMessage(placeholders));
                            } else {
                                sender.sendMessage(Messages.CANT_BE_A_VIRTUAL_CRATE.getMessage());
                            }
                        } else {
                            sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[1]));
                        }
                        return true;
                    }
                }
            }
            sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[1]));
            return true;
        }
        sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/Crate forceopen <Crate> [Player]"));
        return true;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("forceopen", "fopen", "fo");
    }
}
