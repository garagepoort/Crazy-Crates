package me.badbones69.crazycrates.commands;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.KeyService;
import me.badbones69.crazycrates.api.enums.CrateType;
import me.badbones69.crazycrates.api.enums.KeyType;
import me.badbones69.crazycrates.api.enums.Messages;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.controllers.CrateControl;
import me.badbones69.crazycrates.settings.Settings;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class OpenCommand implements CrazyCrateCommand {
    private CrazyCrates cc = CrazyCrates.getInstance();
    private KeyService keyService = KeyService.getInstance();

    @Override
    public boolean executeCommand(CommandSender sender, Command cmd, String commandLable, String[] args) {
        if (!Methods.permCheck(sender, "open")) return true;
        if (args.length >= 2) {
            for (Crate crate : cc.getCrates()) {
                if (crate.getName().equalsIgnoreCase(args[1])) {
                    Player player;
                    if (args.length >= 3) {
                        if (!Methods.permCheck(sender, "open.other")) return true;
                        if (Methods.isOnline(args[2], sender)) {
                            player = Methods.getPlayer(args[2]);
                        } else {
                            return true;
                        }
                    } else {
                        if (!(sender instanceof Player)) {
                            sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/Crate open <Crate> [Player]"));
                            return true;
                        } else {
                            player = (Player) sender;
                        }
                    }
                    if (CrazyCrates.getInstance().isInOpeningList(player)) {
                        sender.sendMessage(Messages.CRATE_ALREADY_OPENED.getMessage());
                        return true;
                    }
                    CrateType type = crate.getCrateType();
                    if (type != null) {
                        boolean hasKey = false;
                        KeyType keyType = KeyType.VIRTUAL_KEY;
                        if (cc.getVirtualKeys(player, crate) >= 1) {
                            hasKey = true;
                        } else {
                            if (Settings.getInstance().virtualAcceptsPhysicalKeys) {
                                if (keyService.hasPhysicalKey(player, crate, false)) {
                                    hasKey = true;
                                    keyType = KeyType.PHYSICAL_KEY;
                                }
                            }
                        }
                        if (!hasKey) {
                            if (Settings.getInstance().needKeySound != null) {
                                Sound sound = Sound.valueOf(Settings.getInstance().needKeySound);
                                if (sound != null) {
                                    player.playSound(player.getLocation(), sound, 1f, 1f);
                                }
                            }
                            player.sendMessage(Messages.NO_VIRTUAL_KEY.getMessage());
                            CrateControl.knockBack(player, player.getTargetBlock(null, 1).getLocation().add(.5, 0, .5));
                            return true;
                        }
                        if (Methods.isInventoryFull(player)) {
                            player.sendMessage(Messages.INVENTORY_FULL.getMessage());
                            return true;
                        }
                        if (type != CrateType.CRATE_ON_THE_GO && type != CrateType.QUICK_CRATE && type != CrateType.FIRE_CRACKER && type != CrateType.QUAD_CRATE) {
                            cc.openCrate(player, crate, keyType, player.getLocation(), true, false);
                            HashMap<String, String> placeholders = new HashMap<>();
                            placeholders.put("%Crate%", crate.getName());
                            placeholders.put("%Player%", player.getName());
                            sender.sendMessage(Messages.OPENED_A_CRATE.getMessage(placeholders));
                        } else {
                            sender.sendMessage(Messages.CANT_BE_A_VIRTUAL_CRATE.getMessage());
                        }
                    } else {
                        sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", args[1]));
                    }
                    return true;
                }
            }
        }
        sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/Crate open <Crate> [Player]"));
        return true;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("open");
    }
}
