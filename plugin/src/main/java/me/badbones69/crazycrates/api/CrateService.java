package me.badbones69.crazycrates.api;

import me.badbones69.crazycrates.api.enums.BrokeLocation;
import me.badbones69.crazycrates.api.enums.CrateType;
import me.badbones69.crazycrates.api.interfaces.HologramController;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.CrateLocation;
import me.badbones69.crazycrates.api.objects.CrateSchematic;
import me.badbones69.crazycrates.config.CrateConfigurationParser;
import me.badbones69.crazycrates.config.CrateLocationConfigurationParser;
import me.badbones69.crazycrates.config.CrateSchematicConfigurationParser;
import me.badbones69.crazycrates.multisupport.HologramsSupport;
import me.badbones69.crazycrates.multisupport.HolographicSupport;
import me.badbones69.crazycrates.multisupport.Support;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class CrateService {

    private static CrateService instance = new CrateService();

    private CcLogger logger = CcLogger.getInstance();
    private FileManager fileManager = FileManager.getInstance();
    private CrateLocationConfigurationParser crateLocationParser = CrateLocationConfigurationParser.getInstance();

    private List<Crate> crates = new ArrayList<>();
    private List<CrateLocation> crateLocations = new ArrayList<>();
    private List<BrokeLocation> brokeLocations = new ArrayList<>();
    private List<CrateSchematic> crateSchematics = new ArrayList<>();
    private Plugin plugin = Bukkit.getPluginManager().getPlugin("CrazyCrates");
    private HologramController hologramController;


    public static CrateService getInstance() {
        return instance;
    }


    public List<BrokeLocation> getBrokeCrateLocations() {
        return brokeLocations;
    }

    public List<Crate> getCrates() {
        return crates;
    }

    public List<CrateLocation> getCrateLocations() {
        return crateLocations;
    }

    /**
     * Loads all the information the plugin needs to run.
     */
    public void loadCrates() {
        crates.clear();
        crateLocations.clear();
        crateSchematics.clear();
        if (Support.HOLOGRAPHIC_DISPLAYS.isPluginLoaded()) {
            hologramController = new HolographicSupport();
        } else if (Support.HOLOGRAMS.isPluginLoaded()) {
            hologramController = new HologramsSupport();
        }
        logger.log("Loading all crate information...");

        crates = CrateConfigurationParser.getInstance().loadCrates();
        crates.add(new Crate("Menu", "Menu", CrateType.MENU, new ItemStack(Material.AIR), new ArrayList<>(), null, 0, null, null));

        CcLogger.getInstance().log("All crate information has been loaded.");
        crateLocations = CrateLocationConfigurationParser.getInstance().loadLocations(crates);
        crateSchematics = CrateSchematicConfigurationParser.getInstance().loadSchematics();

        if (hologramController != null) {
            hologramController.removeAllHolograms();
            crateLocations.forEach(cl -> hologramController.createHologram(cl.getLocation().getBlock(), cl.getCrate()));
        }
    }

    public Crate getCrateFromName(String name) {
        return crates.stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

}
