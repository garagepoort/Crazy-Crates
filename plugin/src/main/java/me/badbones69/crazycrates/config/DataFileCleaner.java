package me.badbones69.crazycrates.config;

import me.badbones69.crazycrates.api.CrateService;
import me.badbones69.crazycrates.api.FileManager;
import me.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class DataFileCleaner {

    private static DataFileCleaner instance = new DataFileCleaner();
    private FileManager fileManager = FileManager.getInstance();

    public static DataFileCleaner getInstance() {
        return instance;
    }

    public void cleanDataFile() {
        FileConfiguration data = FileManager.Files.DATA.getFile();
        if (data.contains("Players")) {
            boolean logging = fileManager.isLogging();
            if (logging) System.out.println(fileManager.getPrefix() + "Cleaning up the data.yml file.");
            List<String> removePlayers = new ArrayList<>();
            for (String uuid : data.getConfigurationSection("Players").getKeys(false)) {
                boolean hasKeys = false;
                List<String> noKeys = new ArrayList<>();

                List<Crate> crates = CrateService.getInstance().getCrates();
                for (Crate crate : crates) {
                    if (data.getInt("Players." + uuid + "." + crate.getName()) <= 0) {
                        noKeys.add(crate.getName());
                    } else {
                        hasKeys = true;
                    }
                }
                if (hasKeys) {
                    for (String crate : noKeys) {
                        data.set("Players." + uuid + "." + crate, null);
                    }
                } else {
                    removePlayers.add(uuid);
                }
            }
            if (removePlayers.size() > 0) {
                if (logging) System.out.println(fileManager.getPrefix() + removePlayers.size() + " player's data has been marked to be removed.");
                for (String uuid : removePlayers) {
                    //				if(logging) System.out.println(fileManager.getPrefix() + "Removed " + data.getString("Players." + uuid + ".Name") + "'s empty data from the data.yml.");
                    data.set("Players." + uuid, null);
                }
                if (logging) System.out.println(fileManager.getPrefix() + "All empty player data has been removed.");
            }
            if (logging) System.out.println(fileManager.getPrefix() + "The data.yml file has been cleaned.");
            FileManager.Files.DATA.saveFile();
        }
    }
}
