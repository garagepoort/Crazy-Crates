package me.badbones69.crazycrates.commands;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.enums.Messages;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.Prize;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class DebugCommand implements CrazyCrateCommand {
    private CrazyCrates cc = CrazyCrates.getInstance();

    @Override
    public boolean executeCommand(CommandSender sender, Command cmd, String commandLable, String[] args) {
        if (!Methods.permCheck(sender, "admin")) return true;
        if (args.length >= 2) {
            Crate crate = cc.getCrateFromName(args[1]);
            if (crate != null) {
                for (Prize prize : crate.getPrizes()) {
                    cc.givePrize((Player) sender, prize);
                }
            } else {
                sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[1]));
                return true;
            }
            return true;
        }
        sender.sendMessage(Methods.getPrefix("&c/cc debug <crate>"));
        return true;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("debug");
    }
}
