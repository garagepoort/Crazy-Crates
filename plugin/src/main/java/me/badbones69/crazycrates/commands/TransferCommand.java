package me.badbones69.crazycrates.commands;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.enums.KeyType;
import me.badbones69.crazycrates.api.enums.Messages;
import me.badbones69.crazycrates.api.events.PlayerReceiveKeyEvent;
import me.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TransferCommand implements CrazyCrateCommand {
    private CrazyCrates cc = CrazyCrates.getInstance();

    @Override
    public boolean executeCommand(CommandSender sender, Command cmd, String commandLable, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.MUST_BE_A_PLAYER.getMessage());
            return true;
        }
        if (Methods.permCheck(sender, "transfer")) {
            if (args.length >= 3) {
                Crate crate = cc.getCrateFromName(args[1]);
                if (crate != null) {
                    if (!args[2].equalsIgnoreCase(sender.getName())) {
                        Player target;
                        Player player = (Player) sender;
                        if (Methods.isOnline(args[2], sender)) {
                            target = Methods.getPlayer(args[2]);
                        } else {
                            sender.sendMessage(Messages.NOT_ONLINE.getMessage("%Player%", args[2]));
                            return true;
                        }
                        int amount = 1;
                        if (args.length >= 4) {
                            if (!Methods.isInt(args[3])) {
                                sender.sendMessage(Messages.NOT_A_NUMBER.getMessage("%Number%", args[3]));
                                return true;
                            }
                            amount = Integer.parseInt(args[3]);
                        }
                        if (cc.getVirtualKeys(player, crate) >= amount) {
                            PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReciveReason.TRANSFER);
                            Bukkit.getPluginManager().callEvent(event);
                            if (!event.isCancelled()) {
                                cc.takeKeys(amount, player, crate, KeyType.VIRTUAL_KEY, false);
                                cc.addKeys(amount, target, crate, KeyType.VIRTUAL_KEY);
                                HashMap<String, String> placeholders = new HashMap<>();
                                placeholders.put("%Crate%", crate.getName());
                                placeholders.put("%Amount%", amount + "");
                                placeholders.put("%Player%", target.getName());
                                player.sendMessage(Messages.TRANSFERRED_KEYS.getMessage(placeholders));
                                placeholders.put("%Player%", player.getName());
                                target.sendMessage(Messages.RECEIVED_TRANSFERRED_KEYS.getMessage(placeholders));
                            }
                        } else {
                            sender.sendMessage(Messages.NOT_ENOUGH_KEYS.getMessage("%Crate%", crate.getName()));
                        }
                    } else {
                        sender.sendMessage(Messages.SAME_PLAYER.getMessage());
                    }
                } else {
                    sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[1]));
                }
            } else {
                sender.sendMessage(Methods.getPrefix("&c/Crate Transfer <Crate> <Player> [Amount]"));
            }
        }
        return true;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("transfer", "tran");
    }
}
