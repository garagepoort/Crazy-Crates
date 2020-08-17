package me.badbones69.crazycrates.commands;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.enums.CrateType;
import me.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.List;

public class AdminCommand implements CrazyCrateCommand {
    private CrazyCrates cc = CrazyCrates.getInstance();

    @Override
    public boolean executeCommand(CommandSender sender, Command cmd, String commandLable, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if (!Methods.permCheck(player, "admin")) return true;
        int size = cc.getCrates().size();
        int slots = 9;
        for (; size > 9; size -= 9)
            slots += 9;
        Inventory inv = Bukkit.createInventory(null, slots, Methods.color("&4&lAdmin Keys"));
        for (Crate crate : cc.getCrates()) {
            if (crate.getCrateType() != CrateType.MENU) {
                if (inv.firstEmpty() >= 0) {
                    inv.setItem(inv.firstEmpty(), crate.getAdminKey());
                }
            }
        }
        player.openInventory(inv);
        return true;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("admin");
    }
}
