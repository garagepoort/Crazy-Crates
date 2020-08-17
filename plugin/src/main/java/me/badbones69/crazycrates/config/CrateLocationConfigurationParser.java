package me.badbones69.crazycrates.config;

import me.badbones69.crazycrates.api.CcLogger;
import me.badbones69.crazycrates.api.FileManager;
import me.badbones69.crazycrates.api.enums.BrokeLocation;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.CrateLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class CrateLocationConfigurationParser {

    private static CrateLocationConfigurationParser instance = new CrateLocationConfigurationParser();

    public static CrateLocationConfigurationParser getInstance() {
        return instance;
    }

    public List<CrateLocation> loadLocations(List<Crate> crates) {
        CcLogger.getInstance().log("Loading all the physical crate locations.");
        List<CrateLocation> crateLocations = new ArrayList<>();
        List<BrokeLocation> brokeLocations = new ArrayList<>();
        FileConfiguration locations = FileManager.Files.LOCATIONS.getFile();

        if (locations.getConfigurationSection("Locations") != null) {
            for (String locationName : locations.getConfigurationSection("Locations").getKeys(false)) {
                try {
                    String worldName = locations.getString("Locations." + locationName + ".World");
                    World world = Bukkit.getWorld(worldName);
                    int x = locations.getInt("Locations." + locationName + ".X");
                    int y = locations.getInt("Locations." + locationName + ".Y");
                    int z = locations.getInt("Locations." + locationName + ".Z");
                    Location location = new Location(world, x, y, z);
                    Crate crate = getCrateFromName(crates, locations.getString("Locations." + locationName + ".Crate"));
                    if (world != null && crate != null) {
                        crateLocations.add(new CrateLocation(locationName, crate, location));
                    } else {
                        brokeLocations.add(new BrokeLocation(locationName, crate, x, y, z, worldName));
                        CcLogger.getInstance().log("Crate could not be loaded: " + locationName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (crateLocations.size() > 0 || brokeLocations.size() > 0) {
            if (brokeLocations.size() <= 0) {
                CcLogger.getInstance().log("All physical crate locations have been loaded.");
            } else {
                CcLogger.getInstance().log("Loaded " + crateLocations.size() + " physical crate locations.");
                CcLogger.getInstance().log("Failed to load " + brokeLocations.size() + " physical crate locations.");
            }
        }
        return crateLocations;
    }

    public Crate getCrateFromName(List<Crate> crates, String name) {
        return crates.stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
