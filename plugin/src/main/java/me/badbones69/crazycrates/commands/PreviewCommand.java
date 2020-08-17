package me.badbones69.crazycrates.commands;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.enums.CrateType;
import me.badbones69.crazycrates.api.enums.Messages;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.controllers.Preview;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class PreviewCommand implements CrazyCrateCommand {
    private CrazyCrates cc = CrazyCrates.getInstance();

    @Override
    public boolean executeCommand(CommandSender sender, Command cmd, String commandLable, String[] args) {
        if (sender instanceof Player) {
            if (!Methods.permCheck(sender, "preview")) {
                return true;
            }
        }
        if (args.length >= 2) {
            Crate crate = null;
            Player player;
            for (Crate c : cc.getCrates()) {
                if (c.getCrateType() != CrateType.MENU) {
                    if (c.getName().equalsIgnoreCase(args[1])) {
                        crate = c;
                    }
                }
            }
            if (crate != null) {
                if (crate.isPreviewEnabled()) {
                    if (crate.getCrateType() != CrateType.MENU) {
                        if (args.length >= 3) {
                            if (Methods.isOnline(args[2], sender)) {
                                player = Methods.getPlayer(args[2]);
                            } else {
                                return true;
                            }
                        } else {
                            if (!(sender instanceof Player)) {
                                sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/Crate Preview <Crate> [Player]"));
                                return true;
                            } else {
                                player = (Player) sender;
                            }
                        }
                        Preview.setPlayerInMenu(player, false);
                        Preview.openNewPreview(player, crate);
                    }
                } else {
                    sender.sendMessage(Messages.PREVIEW_DISABLED.getMessage());
                }
                return true;
            }
        }
        sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/Crate Preview <Crate> [Player]"));
        return true;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("preview");
    }
}
