package me.badbones69.crazycrates.commands;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.CrateLocation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class ListCommand implements CrazyCrateCommand {
    private CrazyCrates cc = CrazyCrates.getInstance();

    @Override
    public boolean executeCommand(CommandSender sender, Command cmd, String commandLable, String[] args) {
        if (!Methods.permCheck(sender, "admin")) return true;
        String crates = "";
        String brokecrates = "";
        for (Crate crate : cc.getCrates()) {
            crates += "&a" + crate.getName() + "&8, ";
        }
        for (String crate : cc.getBrokeCrates()) {
            brokecrates += "&c" + crate + ".yml&8, ";
        }
        sender.sendMessage(Methods.color("&e&lCrates:&f " + crates));
        if (brokecrates.length() > 0) {
            sender.sendMessage(Methods.color("&6&lBroken Crates:&f " + brokecrates.substring(0, brokecrates.length() - 2)));
        }
        sender.sendMessage(Methods.color("&e&lAll Crate Locations:"));
        sender.sendMessage(Methods.color("&c[ID]&8, &c[Crate]&8, &c[World]&8, &c[X]&8, &c[Y]&8, &c[Z]"));
        int line = 1;
        for (CrateLocation loc : cc.getCrateLocations()) {
            Crate crate = loc.getCrate();
            String world = loc.getLocation().getWorld().getName();
            int x = loc.getLocation().getBlockX();
            int y = loc.getLocation().getBlockY();
            int z = loc.getLocation().getBlockZ();
            sender.sendMessage(Methods.color("&8[&b" + line + "&8]: " + "&c" + loc.getID() + "&8, &c" + crate.getName() + "&8, &c" + world + "&8, &c" + x + "&8, &c" + y + "&8, &c" + z));
            line++;
        }
        return true;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("list");
    }
}
