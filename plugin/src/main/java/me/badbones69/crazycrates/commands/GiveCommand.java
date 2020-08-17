package me.badbones69.crazycrates.commands;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.KeyService;
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

import static me.badbones69.crazycrates.commands.CommandInputValidator.validate;

public class GiveCommand implements CrazyCrateCommand {
    private CrazyCrates cc = CrazyCrates.getInstance();
    private KeyService keyService = KeyService.getInstance();

    @Override
    public boolean executeCommand(CommandSender sender, Command cmd, String commandLable, String[] args) {
        // /Crate Give <Physical/Virtual> <Crate> [Amount] [Player]
        if (!Methods.permCheck(sender, "admin")) return true;
        validate(() -> args.length >= 4, Methods.color(Methods.getPrefix() + "&c/Crate Give <Physical/Virtual> <Crate> [Amount] [Player]"));

        KeyType type = KeyType.getFromName(args[1]);
        Crate crate = cc.getCrateFromName(args[2]);

        validate(() -> type != null && type != KeyType.FREE_KEY, Methods.color(Methods.getPrefix() + "&cPlease use Virtual/V or Physical/P for a Key type."));
        validate(() -> crate != null && crate.getCrateType() != CrateType.MENU, Messages.NOT_A_CRATE.getMessage("%Crate%", args[2]));
        validate(() -> sender instanceof Player, Messages.MUST_BE_A_PLAYER.getMessage());
        validate(() -> Methods.isInt(args[3]), Messages.NOT_A_NUMBER.getMessage("%Number%", args[3]));

        int amount = Integer.parseInt(args[3]);
        Player target = args.length >= 5 ? Methods.getPlayer(args[4]) : (Player) sender;

        PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(target, crate, PlayerReceiveKeyEvent.KeyReciveReason.GIVE_COMMAND);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            return true;
        }
        if (crate.getCrateType() == CrateType.CRATE_ON_THE_GO) {
            target.getInventory().addItem(crate.getKey(amount));
        } else {
            if (target != null) {
                cc.addKeys(amount, target, crate, type);
            } else {
                if (!cc.addOfflineKeys(args[4], crate, amount)) {
                    sender.sendMessage(Messages.INTERNAL_ERROR.getMessage());
                } else {
                    HashMap<String, String> placeholders = new HashMap<>();
                    placeholders.put("%Amount%", amount + "");
                    placeholders.put("%Player%", args[4]);
                    sender.sendMessage(Messages.GIVEN_OFFLINE_PLAYER_KEYS.getMessage(placeholders));
                }
                return true;
            }
        }

        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("%Player%", target.getName());
        placeholders.put("%Amount%", amount + "");
        placeholders.put("%Key%", crate.getKey().getItemMeta().getDisplayName());
        sender.sendMessage(Messages.GIVEN_A_PLAYER_KEYS.getMessage(placeholders));
        target.sendMessage(Messages.OBTAINING_KEYS.getMessage(placeholders));
        return true;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("give");
    }
}
