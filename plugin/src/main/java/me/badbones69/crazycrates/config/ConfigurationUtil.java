package me.badbones69.crazycrates.config;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.objects.ItemBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;

import java.util.Arrays;
import java.util.List;

public class ConfigurationUtil {
    public static List<ItemBuilder> getItems(FileConfiguration file, String prize) {
        return ItemBuilder.convertStringList(file.getStringList("Crate.Prizes." + prize + ".Items"), prize);
    }

    public static ItemBuilder getDisplayItem(FileConfiguration file, String prize) {
        String path = "Crate.Prizes." + prize + ".";
        ItemBuilder itemBuilder = new ItemBuilder();
        try {
            itemBuilder.setMaterial(file.getString(path + "DisplayItem"))
                    .setAmount(file.getInt(path + "DisplayAmount", 1))
                    .setName(file.getString(path + "DisplayName"))
                    .setLore(file.getStringList(path + "Lore"))
                    .setGlowing(file.getBoolean(path + "Glowing"))
                    .setUnbreakable(file.getBoolean(path + "Unbreakable"))
                    .hideItemFlags(file.getBoolean(path + "HideItemFlags"))
                    .addItemFlags(file.getStringList(path + "Flags"))
                    .addPatterns(file.getStringList(path + "Patterns"))
                    .setPlayer(file.getString(path + "Player"));
            if (file.contains(path + "DisplayEnchantments")) {
                for (String enchantmentName : file.getStringList(path + "DisplayEnchantments")) {
                    Enchantment enchantment = Methods.getEnchantment(enchantmentName.split(":")[0]);
                    if (enchantment != null) {
                        itemBuilder.addEnchantments(enchantment, Integer.parseInt(enchantmentName.split(":")[1]));
                    }
                }
            }
            return itemBuilder;
        } catch (Exception e) {
            return new ItemBuilder().setMaterial("RED_TERRACOTTA", "STAINED_CLAY:14").setName("&c&lERROR").setLore(Arrays.asList("&cThere is an error", "&cFor the reward: &c" + prize));
        }
    }
}
