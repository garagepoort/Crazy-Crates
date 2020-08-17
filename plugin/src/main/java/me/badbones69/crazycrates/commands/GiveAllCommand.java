package me.badbones69.crazycrates.commands;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.enums.CrateType;
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

public class GiveAllCommand implements CrazyCrateCommand {
    private CrazyCrates cc = CrazyCrates.getInstance();

    @Override
    public boolean executeCommand(CommandSender sender, Command cmd, String commandLable, String[] args) {
        if (!Methods.permCheck(sender, "admin")) return true;
        if (args.length >= 3) {
            int amount = 1;
            if (args.length >= 4) {
                if (!Methods.isInt(args[3])) {
                    sender.sendMessage(Messages.NOT_A_NUMBER.getMessage("%Number%", args[3]));
                    return true;
                }
                amount = Integer.parseInt(args[3]);
            }
            KeyType type = KeyType.getFromName(args[1]);
            if (type == null || type == KeyType.FREE_KEY) {
                sender.sendMessage(Methods.color(Methods.getPrefix() + "&cPlease use Virtual/V or Physical/P for a Key type."));
                return true;
            }
            Crate crate = cc.getCrateFromName(args[2]);
            if (crate != null) {
                if (crate.getCrateType() != CrateType.MENU) {
                    HashMap<String, String> placeholders = new HashMap<>();
                    placeholders.put("%Amount%", amount + "");
                    placeholders.put("%Key%", crate.getKey().getItemMeta().getDisplayName());
                    sender.sendMessage(Messages.GIVEN_EVERYONE_KEYS.getMessage(placeholders));
                    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                        PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReciveReason.GIVE_ALL_COMMAND);
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            player.sendMessage(Messages.OBTAINING_KEYS.getMessage(placeholders));
                            if (crate.getCrateType() == CrateType.CRATE_ON_THE_GO) {
                                player.getInventory().addItem(crate.getKey(amount));
                                return true;
                            }
                            cc.addKeys(amount, player, crate, type);
                        }
                    }
                    return true;
                }
            }
            sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[2]));
            return true;
        }
        sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/Crate GiveAll <Physical/Virtual> <Crate> <Amount>"));
        return true;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("giveall");
    }
}
