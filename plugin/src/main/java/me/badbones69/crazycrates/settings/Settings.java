package me.badbones69.crazycrates.settings;

import me.badbones69.crazycrates.api.FileManager;
import me.badbones69.crazycrates.multisupport.Version;
import me.badbones69.crazycrates.multisupport.nms.NMSSupport;
import me.badbones69.crazycrates.multisupport.nms.v1_10_R1.NMS_v1_10_R1;
import me.badbones69.crazycrates.multisupport.nms.v1_11_R1.NMS_v1_11_R1;
import me.badbones69.crazycrates.multisupport.nms.v1_12_R1.NMS_v1_12_R1;
import me.badbones69.crazycrates.multisupport.nms.v1_13_R2.NMS_v1_13_R2;
import me.badbones69.crazycrates.multisupport.nms.v1_14_R1.NMS_v1_14_R1;
import me.badbones69.crazycrates.multisupport.nms.v1_15_R1.NMS_v1_15_R1;
import me.badbones69.crazycrates.multisupport.nms.v1_16_R1.NMS_v1_16_R1;
import me.badbones69.crazycrates.multisupport.nms.v1_8_R3.NMS_v1_8_R3;
import me.badbones69.crazycrates.multisupport.nms.v1_9_R1.NMS_v1_9_R1;
import me.badbones69.crazycrates.multisupport.nms.v1_9_R2.NMS_v1_9_R2;
import org.bukkit.configuration.file.FileConfiguration;

public class Settings {
    private static Settings instance = new Settings();

    public boolean useNewMaterial;
    public boolean useNewSounds;
    public NMSSupport nmsSupport;
    public Integer quadCrateTimer;
    public boolean giveVirtualKeysWhenInventoryFull;
    public boolean updateChecker;
    public String prefix;
    public boolean acceptsVirtualKeys;
    public boolean knockBack;
    public String needKeySound;
    public int inventorySize;
    public String inventoryName;
    public boolean virtualAcceptsPhysicalKeys;

    public static Settings getInstance() {
        return instance;
    }

    public void load() {
        Version version = Version.getCurrentVersion();
        useNewMaterial = version.isNewer(Version.v1_12_R1);
        useNewSounds = version.isNewer(Version.v1_8_R3);
        switch (version) {
            case v1_8_R3:
                nmsSupport = new NMS_v1_8_R3();
                break;
            case v1_9_R1:
                nmsSupport = new NMS_v1_9_R1();
                break;
            case v1_9_R2:
                nmsSupport = new NMS_v1_9_R2();
                break;
            case v1_10_R1:
                nmsSupport = new NMS_v1_10_R1();
                break;
            case v1_11_R1:
                nmsSupport = new NMS_v1_11_R1();
                break;
            case v1_12_R1:
                nmsSupport = new NMS_v1_12_R1();
                break;
            case v1_13_R2:
                nmsSupport = new NMS_v1_13_R2();
                break;
            case v1_14_R1:
                nmsSupport = new NMS_v1_14_R1();
                break;
            case v1_15_R1:
                nmsSupport = new NMS_v1_15_R1();
                break;
            case v1_16_R1:
                nmsSupport = new NMS_v1_16_R1();
                break;
        }

        FileConfiguration file = FileManager.Files.CONFIG.getFile();

        quadCrateTimer = file.getInt("Settings.QuadCrate.Timer") * 20;
        giveVirtualKeysWhenInventoryFull = file.getBoolean("Settings.Give-Virtual-Keys-When-Inventory-Full");
        acceptsVirtualKeys = file.getBoolean("Settings.Physical-Accepts-Virtual-Keys");
        virtualAcceptsPhysicalKeys = file.getBoolean("Settings.Virtual-Accepts-Physical-Keys");
        updateChecker = !file.contains("Settings.Update-Checker") || file.getBoolean("Settings.Update-Checker");
        prefix = file.getString("Settings.Prefix");
        knockBack = file.getBoolean("Settings.KnockBack");
        needKeySound = file.getString("Settings.Need-Key-Sound");
        needKeySound = file.getString("Settings.Need-Key-Sound");
        inventorySize = file.getInt("Settings.InventorySize");
        inventoryName = file.getString("Settings.InventoryName");
    }
}
