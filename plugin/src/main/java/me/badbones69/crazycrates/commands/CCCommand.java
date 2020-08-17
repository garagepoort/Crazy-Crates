package me.badbones69.crazycrates.commands;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.FileManager;
import me.badbones69.crazycrates.api.FileManager.Files;
import me.badbones69.crazycrates.api.enums.CrateType;
import me.badbones69.crazycrates.api.enums.KeyType;
import me.badbones69.crazycrates.api.enums.Messages;
import me.badbones69.crazycrates.api.events.PlayerReceiveKeyEvent;
import me.badbones69.crazycrates.api.events.PlayerReceiveKeyEvent.KeyReciveReason;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.CrateLocation;
import me.badbones69.crazycrates.api.objects.Prize;
import me.badbones69.crazycrates.controllers.CrateControl;
import me.badbones69.crazycrates.controllers.GUIMenu;
import me.badbones69.crazycrates.controllers.Preview;
import me.badbones69.crazycrates.multisupport.Support;
import me.badbones69.crazycrates.multisupport.Version;
import me.badbones69.crazycrates.multisupport.converters.CratesPlusConverter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class CCCommand implements CommandExecutor {

    private FileManager fileManager = FileManager.getInstance();
    private CrazyCrates cc = CrazyCrates.getInstance();

    private List<CrazyCrateCommand> crateCommands = Arrays.asList(new AddItemCommand(),
            new AdminCommand(), new ConvertCommand(), new DebugCommand(), new ForceOpenCommand(), new GiveAllCommand(), new GiveCommand(), new HelpCommand(), new ListCommand(), new OpenCommand(),
            new PreviewCommand(), new ReloadCommand(), new SaveCommand(), new Set1Command(), new SetCommand(), new TakeCommand(), new TransferCommand());

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLable, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                if (!Methods.permCheck(sender, "menu")) {
                    return true;
                }
            } else {
                sender.sendMessage(Messages.MUST_BE_A_PLAYER.getMessage());
                return true;
            }
            GUIMenu.openGUI((Player) sender);
            return true;
        } else {
            Optional<CrazyCrateCommand> crazyCrateCommand = crateCommands.stream().filter(cc -> cc.getAliases().contains(args[0])).findFirst();
            if (crazyCrateCommand.isPresent()) {
                return crazyCrateCommand.get().execute(sender, cmd, commandLable, args);
            } else {
                sender.sendMessage(Methods.color(Methods.getPrefix() + "&cPlease do /cc help for more info."));
                return true;
            }
        }
    }

}
