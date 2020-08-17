package me.badbones69.crazycrates.config;

import me.badbones69.crazycrates.api.objects.Tier;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

public class TierConfigurationParser {

    private static TierConfigurationParser instance = new TierConfigurationParser();

    public static TierConfigurationParser getInstance() {
        return instance;
    }

    public ArrayList<Tier> load(FileConfiguration file) {
        ArrayList<Tier> tiers = new ArrayList<>();
        if (file.contains("Crate.Tiers") && file.getConfigurationSection("Crate.Tiers") != null) {
            for (String tier : file.getConfigurationSection("Crate.Tiers").getKeys(false)) {
                String path = "Crate.Tiers." + tier;
                tiers.add(new Tier(tier, file.getString(path + ".Name"), file.getString(path + ".Color"), file.getInt(path + ".Chance"), file.getInt(path + ".MaxRange")));
            }
        }
        return tiers;
    }
}
