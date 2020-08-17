package me.badbones69.crazycrates.commands;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.enums.Messages;
import me.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SetCommand implements CrazyCrateCommand {
    private CrazyCrates cc = CrazyCrates.getInstance();

    @Override
    public boolean executeCommand(CommandSender sender, Command cmd, String commandLable, String[] args) {
        if (!Methods.permCheck(sender, "admin")) return true;
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.MUST_BE_A_PLAYER.getMessage());
            return true;
        }
        if (args.length == 2) {
            Player player = (Player) sender;
            String c = args[1]; //Crate
            for (Crate crate : cc.getCrates()) {
                if (crate.getName().equalsIgnoreCase(c)) {
                    Block block = player.getTargetBlock(null, 5);
                    if (block.isEmpty()) {
                        player.sendMessage(Messages.MUST_BE_LOOKING_AT_A_BLOCK.getMessage());
                        return true;
                    }
                    CrazyCrates.getInstance().addCrateLocation(block.getLocation(), crate);
                    HashMap<String, String> placeholders = new HashMap<>();
                    placeholders.put("%Crate%", crate.getName());
                    placeholders.put("%Prefix%", Methods.getPrefix());
                    player.sendMessage(Messages.CREATED_PHYSICAL_CRATE.getMessage(placeholders));
                    return true;
                }
            }
            sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", c));
            return true;
        }
        sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/cc Set <Crate>"));
        return true;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("set", "s");
    }
}
