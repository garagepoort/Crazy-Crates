package me.badbones69.crazycrates.config;

import me.badbones69.crazycrates.api.CcLogger;
import me.badbones69.crazycrates.api.objects.CrateSchematic;
import me.badbones69.crazycrates.multisupport.Version;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CrateSchematicConfigurationParser {

    private static CrateSchematicConfigurationParser instance = new CrateSchematicConfigurationParser();
    private Plugin plugin = Bukkit.getPluginManager().getPlugin("CrazyCrates");

    public static CrateSchematicConfigurationParser getInstance() {
        return instance;
    }

    public List<CrateSchematic> loadSchematics() {
        List<CrateSchematic> crateSchematics = new ArrayList<>();
        CcLogger.getInstance().log("Searching for schematics to load.");
        String[] schems = new File(plugin.getDataFolder() + "/Schematics/").list();
        boolean isNewer = Version.getCurrentVersion().isNewer(Version.v1_12_R1);
        for (String schematicName : schems) {
            if (isNewer) {
                if (schematicName.endsWith(".nbt")) {
                    crateSchematics.add(new CrateSchematic(schematicName.replace(".nbt", ""), new File(plugin.getDataFolder() + "/Schematics/" + schematicName)));
                    CcLogger.getInstance().log(schematicName + " was successfully found and loaded.");
                }
            } else {
                if (schematicName.endsWith(".schematic")) {
                    crateSchematics.add(new CrateSchematic(schematicName.replace(".schematic", ""), new File(plugin.getDataFolder() + "/Schematics/" + schematicName)));
                    CcLogger.getInstance().log(schematicName + " was successfully found and loaded.");
                }
            }
        }
        CcLogger.getInstance().log("All schematics were found and loaded.");
        return crateSchematics;
    }
}
