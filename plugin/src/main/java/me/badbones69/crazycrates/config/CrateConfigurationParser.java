package me.badbones69.crazycrates.config;

import me.badbones69.crazycrates.api.FileManager;
import me.badbones69.crazycrates.api.enums.CrateType;
import me.badbones69.crazycrates.api.objects.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

import static java.util.stream.Collectors.toList;

public class CrateConfigurationParser {

    private static CrateConfigurationParser instance = new CrateConfigurationParser();
    private FileManager fileManager = FileManager.getInstance();
    private PrizeConfigurationParser prizeConfigurationParser = PrizeConfigurationParser.getInstance();
    private TierConfigurationParser tierConfigurationParser = TierConfigurationParser.getInstance();
    private boolean giveNewPlayersKeys;
    private List<String> brokenCrateConfigurations = new ArrayList<>();

    public static CrateConfigurationParser getInstance() {
        return instance;
    }

    public List<Crate> loadCrates() {
        return fileManager.getAllCratesNames().stream()
                .map(this::loadCrate)
                .filter(Objects::nonNull)
                .collect(toList());
    }

    public List<String> getBrokenCrateConfigurations() {
        return brokenCrateConfigurations;
    }

    public boolean isGiveNewPlayersKeys() {
        return giveNewPlayersKeys;
    }

    private Crate loadCrate(String crateName) {
        try {
            FileConfiguration file = fileManager.getFile(crateName).getFile();
            String previewName = file.contains("Crate.Preview-Name") ? file.getString("Crate.Preview-Name") : file.getString("Crate.Name");
            List<Tier> tiers = tierConfigurationParser.load(file);
            List<Prize> prizes = prizeConfigurationParser.loadConfiguration(file, crateName, tiers);

            int newPlayersKeys = file.getInt("Crate.StartingKeys");
            if (giveNewPlayersKeys = false) {
                if (newPlayersKeys > 0) {
                    giveNewPlayersKeys = true;
                }
            }

            CrateHologram hologram = new CrateHologram(file.getBoolean("Crate.Hologram.Toggle"), file.getDouble("Crate.Hologram.Height", 0.0), file.getStringList("Crate.Hologram.Message"));
            return new Crate(crateName, previewName, CrateType.getFromName(file.getString("Crate.CrateType")),
                    getKey(file),
                    prizes, file,
                    newPlayersKeys,
                    tiers,
                    hologram);
            //				if(fileManager.isLogging()) System.out.println(fileManager.getPrefix() + "" + crateName + ".yml has been loaded.");
        } catch (Exception e) {
            brokenCrateConfigurations.add(crateName);
            Bukkit.getLogger().log(Level.WARNING, fileManager.getPrefix() + "There was an error while loading the " + crateName + ".yml file.");
            e.printStackTrace();
            return null;
        }
    }

    private ItemStack getKey(FileConfiguration file) {
        String name = file.getString("Crate.PhysicalKey.Name");
        List<String> lore = file.getStringList("Crate.PhysicalKey.Lore");
        String id = file.getString("Crate.PhysicalKey.Item");
        boolean glowing = false;
        if (file.contains("Crate.PhysicalKey.Glowing")) {
            glowing = file.getBoolean("Crate.PhysicalKey.Glowing");
        }
        return new ItemBuilder().setMaterial(id).setName(name).setLore(lore).setGlowing(glowing).build();
    }

}
