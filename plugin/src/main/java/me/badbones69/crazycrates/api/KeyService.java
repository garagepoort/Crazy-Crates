package me.badbones69.crazycrates.api;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.enums.CrateType;
import me.badbones69.crazycrates.api.enums.KeyType;
import me.badbones69.crazycrates.api.events.PlayerReceiveKeyEvent;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.config.CrateConfigurationParser;
import me.badbones69.crazycrates.multisupport.Version;
import me.badbones69.crazycrates.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeyService {

    private static KeyService instance = new KeyService();

    private Settings settings = Settings.getInstance();

    public static KeyService getInstance() {
        return instance;
    }

    public void addKeys(int amount, Player player, Crate crate, KeyType keyType) {
        switch (keyType) {
            case PHYSICAL_KEY:
                if (Methods.isInventoryFull(player)) {
                    if (settings.giveVirtualKeysWhenInventoryFull) {
                        addKeys(amount, player, crate, KeyType.VIRTUAL_KEY);
                    } else {
                        player.getWorld().dropItem(player.getLocation(), crate.getKey(amount));
                    }
                } else {
                    player.getInventory().addItem(crate.getKey(amount));
                }
                break;
            case VIRTUAL_KEY:
                String uuid = player.getUniqueId().toString();
                int keys = getVirtualKeys(player, crate);
                FileManager.Files.DATA.getFile().set("Players." + uuid + ".Name", player.getName());
                FileManager.Files.DATA.getFile().set("Players." + uuid + "." + crate.getName(), (Math.max((keys + amount), 0)));
                FileManager.Files.DATA.saveFile();
                break;
        }
    }

    /**
     * Set a new player's default amount of keys.
     *
     * @param player The player that has just joined.
     */
    public void setNewPlayerKeys(Player player) {

        if (!CrateConfigurationParser.getInstance().isGiveNewPlayersKeys()) {
            //No keys for new players
            return;
        }
        String uuid = player.getUniqueId().toString();
        if (player.hasPlayedBefore()) {
            CrateService.getInstance().getCrates().stream()
                    .filter(Crate::doNewPlayersGetKeys)
                    .forEach(crate -> FileManager.Files.DATA.getFile().set("Players." + uuid + "." + crate, crate.getNewPlayerKeys()));
            FileManager.Files.DATA.saveFile();
        }

    }

    /**
     * Load the offline keys of a player who has came online.
     *
     * @param player The player which you would like to load the offline keys for.
     */
    public void loadOfflinePlayersKeys(Player player) {
        FileConfiguration data = FileManager.Files.DATA.getFile();
        String name = player.getName().toLowerCase();
        if (data.contains("Offline-Players." + name)) {
            List<Crate> crates = CrateService.getInstance().getCrates();
            for (Crate crate : crates) {
                if (data.contains("Offline-Players." + name + "." + crate.getName())) {
                    PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReciveReason.OFFLINE_PLAYER);
                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        addKeys(data.getInt("Offline-Players." + name + "." + crate.getName()), player, crate, KeyType.VIRTUAL_KEY);
                    }
                }
            }
            data.set("Offline-Players." + name, null);
            FileManager.Files.DATA.saveFile();
        }
    }

    public int getVirtualKeys(Player player, Crate crate) {
        return FileManager.Files.DATA.getFile().getInt("Players." + player.getUniqueId() + "." + crate.getName());
    }

    /**
     * Checks to see if the player has a physical key of the crate in their main hand or inventory.
     *
     * @param player    The player being checked.
     * @param crate     The crate that has the key you are checking.
     * @param checkHand If it just checks the players hand or if it checks their inventory.
     * @return True if they have the key and false if not.
     */
    public boolean hasPhysicalKey(Player player, Crate crate, boolean checkHand) {
        List<ItemStack> items = new ArrayList<>();
        if (checkHand) {
            items.add(settings.nmsSupport.getItemInMainHand(player));
            if (Version.getCurrentVersion().isNewer(Version.v1_8_R3)) {
                items.add(player.getEquipment().getItemInOffHand());
            }
        } else {
            items.addAll(Arrays.asList(player.getInventory().getContents()));
            items.removeAll(Arrays.asList(player.getInventory().getArmorContents()));
        }
        for (ItemStack item : items) {
            if (item != null) {
                if (isKeyFromCrate(item, crate)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Get the total amount of keys a player has.
     */
    public Integer getTotalKeys(Player player, Crate crate) {
        return getVirtualKeys(player, crate) + getPhysicalKeys(player, crate);
    }

    /**
     * Get the amount of physical keys a player has.
     */
    public int getPhysicalKeys(Player player, Crate crate) {
        int keys = 0;
        for (ItemStack item : player.getOpenInventory().getBottomInventory().getContents()) {
            if (item != null) {
                if (Methods.isSimilar(item, crate)) {
                    keys += item.getAmount();
                } else {
                    NBTItem nbtItem = new NBTItem(item);
                    if (nbtItem.hasKey("CrazyCrates-Crate")) {
                        if (crate.getName().equals(nbtItem.getString("CrazyCrates-Crate"))) {
                            keys += item.getAmount();
                        }
                    }
                }
            }
        }
        return keys;
    }

    public boolean isKeyFromCrate(ItemStack item, Crate crate) {
        if (crate.getCrateType() != CrateType.MENU) {
            if (item != null && item.getType() != Material.AIR) {
                return Methods.isSimilar(item, crate);
            }
        }
        return false;
    }

}
