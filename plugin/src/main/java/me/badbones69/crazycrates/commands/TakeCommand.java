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

public class TakeCommand implements CrazyCrateCommand {
    private CrazyCrates cc = CrazyCrates.getInstance();

    @Override
    public boolean executeCommand(CommandSender sender, Command cmd, String commandLable, String[] args) {
        // /Crate Give <Physical/Virtual> <Crate> [Amount] [Player]
        if (!Methods.permCheck(sender, "admin")) return true;
        KeyType keyType = null;
        if (args.length >= 2) {
            keyType = KeyType.getFromName(args[1]);
        }
        if (keyType == null || keyType == KeyType.FREE_KEY) {
            sender.sendMessage(Methods.color(Methods.getPrefix() + "&cPlease use Virtual/V or Physical/P for a Key type."));
            return true;
        }
        if (args.length == 3) {
            Crate crate = cc.getCrateFromName(args[2]);
            if (crate != null) {
                if (crate.getCrateType() != CrateType.MENU) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Messages.MUST_BE_A_PLAYER.getMessage());
                        return true;
                    }
                    HashMap<String, String> placeholders = new HashMap<>();
                    placeholders.put("%Amount%", "1");
                    placeholders.put("%Player%", sender.getName());
                    sender.sendMessage(Messages.TAKE_A_PLAYER_KEYS.getMessage(placeholders));
                    if (!cc.takeKeys(1, (Player) sender, crate, keyType, false)) {
                        Methods.failedToTakeKey((Player) sender, crate);
                    }
                    return true;
                }
            }
            sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[2]));
            return true;
        } else if (args.length == 4) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Messages.MUST_BE_A_PLAYER.getMessage());
                return true;
            }
            if (!Methods.isInt(args[3])) {
                sender.sendMessage(Messages.NOT_A_NUMBER.getMessage("%Number%", args[3]));
                return true;
            }
            int amount = Integer.parseInt(args[3]);
            Crate crate = cc.getCrateFromName(args[2]);
            if (crate != null) {
                if (crate.getCrateType() != CrateType.MENU) {
                    HashMap<String, String> placeholders = new HashMap<>();
                    placeholders.put("%Amount%", amount + "");
                    placeholders.put("%Player%", sender.getName());
                    sender.sendMessage(Messages.TAKE_A_PLAYER_KEYS.getMessage(placeholders));
                    if (!cc.takeKeys(amount, (Player) sender, crate, keyType, false)) {
                        Methods.failedToTakeKey((Player) sender, crate);
                    }
                    return true;
                }
            }
            sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[2]));
            return true;
        } else if (args.length == 5) {
            if (!Methods.isInt(args[3])) {
                sender.sendMessage(Messages.NOT_A_NUMBER.getMessage("%Number%", args[3]));
                return true;
            }
            int amount = Integer.parseInt(args[3]);
            Player target = Methods.getPlayer(args[4]);
            Crate crate = cc.getCrateFromName(args[2]);
            if (crate != null) {
                if (crate.getCrateType() != CrateType.MENU) {
                    if (keyType == KeyType.VIRTUAL_KEY) {
                        if (target != null) {
                            HashMap<String, String> placeholders = new HashMap<>();
                            placeholders.put("%Amount%", amount + "");
                            placeholders.put("%Player%", target.getName());
                            sender.sendMessage(Messages.TAKE_A_PLAYER_KEYS.getMessage(placeholders));
                            if (!cc.takeKeys(amount, target, crate, KeyType.VIRTUAL_KEY, false)) {
                                Methods.failedToTakeKey((Player) sender, crate);
                            }
                        } else {
                            if (!cc.takeOfflineKeys(args[4], crate, amount)) {
                                sender.sendMessage(Messages.INTERNAL_ERROR.getMessage());
                            } else {
                                HashMap<String, String> placeholders = new HashMap<>();
                                placeholders.put("%Amount%", amount + "");
                                placeholders.put("%Player%", args[4]);
                                sender.sendMessage(Messages.TAKE_OFFLINE_PLAYER_KEYS.getMessage(placeholders));
                            }
                            return true;
                        }
                    } else if (keyType == KeyType.PHYSICAL_KEY) {
                        if (target != null) {
                            HashMap<String, String> placeholders = new HashMap<>();
                            placeholders.put("%Amount%", amount + "");
                            placeholders.put("%Player%", target.getName());
                            sender.sendMessage(Messages.TAKE_A_PLAYER_KEYS.getMessage(placeholders));
                            if (!cc.takeKeys(amount, target, crate, KeyType.PHYSICAL_KEY, false)) {
                                Methods.failedToTakeKey((Player) sender, crate);
                            }
                        } else {
                            sender.sendMessage(Messages.NOT_ONLINE.getMessage("%Player%", args[4]));
                        }
                    }
                    return true;
                }
            }
            sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[2]));
            return true;
        }
        sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/Crate Take <Physical/Virtual> <Crate> [Amount] [Player]"));
        return true;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("take");
    }
}
