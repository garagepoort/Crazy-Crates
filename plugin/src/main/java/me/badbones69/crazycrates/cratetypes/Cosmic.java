package me.badbones69.crazycrates.cratetypes;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.FileManager;
import me.badbones69.crazycrates.api.KeyService;
import me.badbones69.crazycrates.api.enums.KeyType;
import me.badbones69.crazycrates.api.enums.Messages;
import me.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import me.badbones69.crazycrates.api.events.PlayerReceiveKeyEvent;
import me.badbones69.crazycrates.api.events.PlayerReceiveKeyEvent.KeyReciveReason;
import me.badbones69.crazycrates.api.managers.CosmicCrateManager;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.Prize;
import me.badbones69.crazycrates.api.objects.Tier;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Cosmic implements Listener {
    
    private static CrazyCrates cc = CrazyCrates.getInstance();
    private static KeyService keyService = KeyService.getInstance();
    private static HashMap<Player, ArrayList<Integer>> glass = new HashMap<>();
    private static HashMap<Player, ArrayList<Integer>> picks = new HashMap<>();
    private static HashMap<Player, Boolean> checkHands = new HashMap<>();
    
    private static void showRewards(Player player, Crate crate) {
        Inventory inv = Bukkit.createInventory(null, 27, Methods.sanitizeColor(crate.getFile().getString("Crate.CrateName") + " - Prizes"));
        picks.get(player).forEach(i -> inv.setItem(i, pickTier(player).getTierPane()));
        player.openInventory(inv);
    }
    
    private static void startRoll(Player player, Crate crate) {
        Inventory inv = Bukkit.createInventory(null, 27, Methods.sanitizeColor(crate.getFile().getString("Crate.CrateName") + " - Shuffling"));
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, pickTier(player).getTierPane());
        }
        player.playSound(player.getLocation(), cc.getSound("UI_BUTTON_CLICK", "CLICK"), 1, 1);
        player.openInventory(inv);
    }
    
    private static void setChests(Inventory inv, Crate crate) {
        CosmicCrateManager manager = (CosmicCrateManager) crate.getManager();
        int slot = 1;
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, manager.getMysteryCrate().setAmount(slot).addNamePlaceholder("%Slot%", slot + "").addLorePlaceholder("%Slot%", slot + "").build());
            slot++;
        }
    }
    
    public static void openCosmic(Player player, Crate crate, KeyType keyType, boolean checkHand) {
        Inventory inv = Bukkit.createInventory(null, 27, Methods.sanitizeColor(crate.getFile().getString("Crate.CrateName") + " - Choose"));
        setChests(inv, crate);
        cc.addPlayerKeyType(player, keyType);
        checkHands.put(player, checkHand);
        player.openInventory(inv);
    }
    
    private static Tier pickTier(Player player) {
        Crate crate = cc.getOpeningCrate(player);
        if (crate.getTiers() != null && !crate.getTiers().isEmpty()) {
            for (int stopLoop = 0; stopLoop <= 100; stopLoop++) {
                for (Tier tier : crate.getTiers()) {
                    int chance = tier.getChance();
                    int num = new Random().nextInt(tier.getMaxRange());
                    if (num >= 1 && num <= chance) {
                        return tier;
                    }
                }
            }
        }
        return null;
    }
    
    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        final Inventory inv = e.getInventory();
        final Player player = (Player) e.getWhoClicked();
        if (inv != null) {
            final Crate crate = cc.getOpeningCrate(player);
            if (cc.isInOpeningList(player)) {
                if (!crate.getFile().getString("Crate.CrateType").equalsIgnoreCase("Cosmic")) return;
            } else {
                return;
            }
            final FileConfiguration file = crate.getFile();
            if (e.getView().getTitle().equals(Methods.sanitizeColor(file.getString("Crate.CrateName") + " - Shuffling"))) {
                e.setCancelled(true);
            }
            if (e.getView().getTitle().equals(Methods.sanitizeColor(file.getString("Crate.CrateName") + " - Prizes"))) {
                e.setCancelled(true);
                int slot = e.getRawSlot();
                if (inCosmic(slot)) {
                    for (int i : picks.get(player)) {
                        if (slot == i) {
                            ItemStack item = e.getCurrentItem();
                            Tier tier = getTier(crate, item);
                            if (item != null && tier != null) {
                                Prize prize = crate.pickPrize(player, tier);
                                for (int stop = 0; prize == null && stop <= 2000; stop++) {
                                    prize = crate.pickPrize(player, tier);
                                }
                                if (prize != null) {
                                    cc.givePrize(player, prize);
                                    Bukkit.getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, cc.getOpeningCrate(player).getName(), prize));
                                    e.setCurrentItem(prize.getDisplayItem());
                                    player.playSound(player.getLocation(), cc.getSound("ENTITY_PLAYER_LEVELUP", "LEVEL_UP"), 1, 1);
                                    if (prize.useFireworks()) {
                                        Methods.fireWork(player.getLocation().add(0, 1, 0));
                                    }
                                }
                                return;
                            }
                        }
                    }
                }
            }
            if (e.getView().getTitle().equals(Methods.sanitizeColor(file.getString("Crate.CrateName") + " - Choose"))) {
                e.setCancelled(true);
                int slot = e.getRawSlot();
                if (inCosmic(slot)) {
                    ItemStack item = e.getCurrentItem();
                    if (item != null) {
                        CosmicCrateManager manager = (CosmicCrateManager) crate.getManager();
                        int totalPrizes = manager.getTotalPrizes();
                        int pickedSlot = slot + 1;
                        NBTItem nbtItem = new NBTItem(item);
                        if (nbtItem.hasNBTData()) {
                            if (nbtItem.hasKey("Cosmic-Mystery-Crate")) {
                                if (!glass.containsKey(player)) glass.put(player, new ArrayList<>());
                                if (glass.get(player).size() < totalPrizes) {
                                    e.setCurrentItem(manager.getPickedCrate().setAmount(pickedSlot).addNamePlaceholder("%Slot%", pickedSlot + "").addLorePlaceholder("%Slot%", pickedSlot + "").build());
                                    glass.get(player).add(slot);
                                }
                                player.playSound(player.getLocation(), cc.getSound("UI_BUTTON_CLICK", "CLICK"), 1, 1);
                            } else if (nbtItem.hasKey("Cosmic-Picked-Crate")) {
                                if (!glass.containsKey(player)) glass.put(player, new ArrayList<>());
                                e.setCurrentItem(manager.getMysteryCrate().setAmount(pickedSlot).addNamePlaceholder("%Slot%", pickedSlot + "").addLorePlaceholder("%Slot%", pickedSlot + "").build());
                                ArrayList<Integer> l = new ArrayList<>();
                                for (int i : glass.get(player))
                                    if (i != slot) l.add(i);
                                glass.put(player, l);
                                player.playSound(player.getLocation(), cc.getSound("UI_BUTTON_CLICK", "CLICK"), 1, 1);
                            }
                        }
                        if (glass.get(player).size() >= totalPrizes) {
                            KeyType keyType = cc.getPlayerKeyType(player);
                            if (keyType == KeyType.PHYSICAL_KEY && !keyService.hasPhysicalKey(player, crate, checkHands.get(player))) {
                                player.closeInventory();
                                player.sendMessage(Messages.NO_KEY.getMessage());
                                if (cc.isInOpeningList(player)) {
                                    cc.removePlayerFromOpeningList(player);
                                    cc.removePlayerKeyType(player);
                                }
                                checkHands.remove(player);
                                glass.remove(player);
                                return;
                            }
                            if (cc.hasPlayerKeyType(player) && !cc.takeKeys(1, player, crate, keyType, checkHands.get(player))) {
                                Methods.failedToTakeKey(player, crate);
                                cc.removePlayerFromOpeningList(player);
                                cc.removePlayerKeyType(player);
                                checkHands.remove(player);
                                glass.remove(player);
                                return;
                            }
                            cc.addCrateTask(player, new BukkitRunnable() {
                                int time = 0;
                                
                                @Override
                                public void run() {
                                    try {
                                        startRoll(player, crate);
                                    } catch (Exception e) {
                                        PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, KeyReciveReason.REFUND);
                                        Bukkit.getPluginManager().callEvent(event);
                                        if (!event.isCancelled()) {
                                            cc.addKeys(1, player, crate, keyType);
                                            cc.endCrate(player);
                                            cancel();
                                            player.sendMessage(Methods.getPrefix("&cAn issue has occurred and so a key refund was given."));
                                            System.out.println(FileManager.getInstance().getPrefix() + "An issue occurred when the user " + player.getName() +
                                            " was using the " + crate.getName() + " crate and so they were issued a key refund.");
                                            e.printStackTrace();
                                        }
                                        return;
                                    }
                                    time++;
                                    if (time == 40) {
                                        cc.endCrate(player);
                                        showRewards(player, crate);
                                        player.playSound(player.getLocation(), cc.getSound("BLOCK_ANVIL_PLACE", "ANVIL_LAND"), 1, 1);
                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                if (player.getOpenInventory().getTopInventory().equals(inv)) {
                                                    player.closeInventory();
                                                }
                                            }
                                        }.runTaskLater(cc.getPlugin(), 40);
                                    }
                                }
                            }.runTaskTimer(cc.getPlugin(), 0, 2));
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onInvClose(InventoryCloseEvent e) {
        Inventory inv = e.getInventory();
        Player player = (Player) e.getPlayer();
        Crate crate = cc.getOpeningCrate(player);
        if (cc.isInOpeningList(player)) {
            if (crate.getFile() == null) {
                return;
            } else {
                if (!crate.getFile().getString("Crate.CrateType").equalsIgnoreCase("Cosmic")) {
                    return;
                }
            }
        } else {
            return;
        }
        if (e.getView().getTitle().equals(Methods.sanitizeColor(crate.getFile().getString("Crate.CrateName") + " - Prizes"))) {
            boolean playSound = false;
            for (int i : picks.get(player)) {
                if (inv.getItem(i) != null) {
                    Tier tier = getTier(crate, inv.getItem(i));
                    if (tier != null) {
                        Prize prize = crate.pickPrize(player, tier);
                        for (int stop = 0; prize == null && stop <= 2000; stop++) {
                            prize = crate.pickPrize(player, tier);
                        }
                        cc.givePrize(player, prize);
                        playSound = true;
                    }
                }
            }
            if (playSound) {
                player.playSound(player.getLocation(), cc.getSound("UI_BUTTON_CLICK", "CLICK"), 1, 1);
            }
            cc.removePlayerFromOpeningList(player);
            cc.removePlayerKeyType(player);
            if (glass.containsKey(player)) {
                picks.put(player, glass.get(player));
                glass.remove(player);
            }
            checkHands.remove(player);
        }
        if (cc.isInOpeningList(player) && e.getView().getTitle().equals(Methods.sanitizeColor(crate.getFile().getString("Crate.CrateName") + " - Choose"))) {
            if (!glass.containsKey(player) || glass.get(player).size() < 4) {
                cc.removePlayerFromOpeningList(player);
                cc.removePlayerKeyType(player);
            }
            if (glass.containsKey(player)) {
                picks.put(player, glass.get(player));
                glass.remove(player);
            }
            checkHands.remove(player);
        }
    }
    
    private Tier getTier(Crate crate, ItemStack item) {
        for (Tier tier : crate.getTiers()) {
            if (tier.getTierPane().isSimilar(item)) {
                return tier;
            }
        }
        return null;
    }
    
    private boolean inCosmic(int slot) {
        //The last slot in cosmic crate is 27
        return slot < 27;
    }
    
}