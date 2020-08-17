package me.badbones69.crazycrates.commands;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrateService;
import me.badbones69.crazycrates.api.FileManager;
import me.badbones69.crazycrates.api.enums.Messages;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.settings.Settings;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AddItemCommand implements CrazyCrateCommand {
    private CrateService crateService = CrateService.getInstance();
    private Settings settings = Settings.getInstance();
    private FileManager fileManager = FileManager.getInstance();

    @Override
    public boolean executeCommand(CommandSender sender, Command cmd, String commandLable, String[] args) {
        // /cc additem0 <crate>1 <prize>2
        if (!Methods.permCheck(sender, "admin")) return true;
        Player player = (Player) sender;
        if (args.length >= 3) {
            ItemStack item = settings.nmsSupport.getItemInMainHand(player);
            if (item != null && item.getType() != Material.AIR) {
                Crate crate = crateService.getCrateFromName(args[1]);
                if (crate != null) {
                    String prize = args[2];
                    try {
                        crate.addEditorItem(prize, item);
                    } catch (Exception e) {
                        System.out.println(fileManager.getPrefix() + "Failed to add a new prize to the " + crate.getName() + " crate.");
                        e.printStackTrace();
                    }
                    crateService.loadCrates();
                    HashMap<String, String> placeholders = new HashMap<>();
                    placeholders.put("%Crate%", crate.getName());
                    placeholders.put("%Prize%", prize);
                    player.sendMessage(Messages.ADDED_ITEM_WITH_EDITOR.getMessage(placeholders));
                } else {
                    player.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[1]));

                }
            } else {
                player.sendMessage(Messages.NO_ITEM_IN_HAND.getMessage());
            }
        } else {
            player.sendMessage(Methods.getPrefix("&c/cc additem <crate> <prize>"));
        }
        return true;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("additem");
    }
}
