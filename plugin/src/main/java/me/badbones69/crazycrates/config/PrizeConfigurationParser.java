package me.badbones69.crazycrates.config;

import me.badbones69.crazycrates.api.objects.Prize;
import me.badbones69.crazycrates.api.objects.Tier;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PrizeConfigurationParser {
    private static PrizeConfigurationParser instance = new PrizeConfigurationParser();

    public static PrizeConfigurationParser getInstance() {
        return instance;
    }

    public List<Prize> loadConfiguration(FileConfiguration file, String crateName, List<Tier> tiers) {
        List<Prize> prizes = new ArrayList<>();
        for (String prize : file.getConfigurationSection("Crate.Prizes").getKeys(false)) {
            Prize altPrize = null;
            String path = "Crate.Prizes." + prize;
            ArrayList<Tier> prizeTiers = new ArrayList<>();
            for (String tier : file.getStringList(path + ".Tiers")) {
                for (Tier loadedTier : tiers) {
                    if (loadedTier.getName().equalsIgnoreCase(tier)) {
                        prizeTiers.add(loadedTier);
                    }
                }
            }
            if (file.contains(path + ".Alternative-Prize")) {
                if (file.getBoolean(path + ".Alternative-Prize.Toggle")) {
                    altPrize = new Prize("Alternative-Prize",
                            file.getStringList(path + ".Alternative-Prize.Messages"),
                            file.getStringList(path + ".Alternative-Prize.Commands"),
                            null,//No editor items
                            ConfigurationUtil.getItems(file, prize + ".Alternative-Prize"));
                }
            }
            ArrayList<ItemStack> editorItems = new ArrayList<>();
            if (file.contains(path + ".Editor-Items")) {
                for (Object list : file.getList(path + ".Editor-Items")) {
                    editorItems.add((ItemStack) list);
                }
            }
            prizes.add(new Prize(prize, ConfigurationUtil.getDisplayItem(file, prize),
                    file.getStringList(path + ".Messages"),
                    file.getStringList(path + ".Commands"),
                    editorItems,
                    ConfigurationUtil.getItems(file, prize),
                    crateName,
                    file.getInt(path + ".Chance", 100),
                    file.getInt(path + ".MaxRange", 100),
                    file.getBoolean(path + ".Firework"),
                    file.getStringList(path + ".BlackListed-Permissions"),
                    prizeTiers,
                    altPrize));
        }
        return prizes;
    }
}
