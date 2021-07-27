/*
 * Copyright (c) 2021. Made by Kieraaaan for the plugin ProtonCore
 */

package kr.kieran.protonprisons.managers;

import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.enchants.AbstractEnchant;
import kr.kieran.protonprisons.enchants.Enchant;
import kr.kieran.protonprisons.enchants.VanillaEnchant;
import kr.kieran.protonprisons.utilities.Color;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PickaxeManager
{

    private final ProtonPrisonsPlugin plugin;

    public PickaxeManager(ProtonPrisonsPlugin plugin)
    {
        this.plugin = plugin;
    }

    public ItemStack getPickaxe(String pickaxeName)
    {
        // Create the pickaxe item
        ItemStack pickaxe = new ItemStack(Material.getMaterial(plugin.getConfig().getString("pickaxes." + pickaxeName + ".material")));
        ItemMeta meta = pickaxe.getItemMeta();

        // Set the name & lore
        meta.setDisplayName(Color.color(plugin.getConfig().getString("pickaxes." + pickaxeName + ".name").replace("%blocks%", "0")));
        List<String> lore = new ArrayList<>();
        for (String string : plugin.getConfig().getStringList("pickaxes." + pickaxeName + ".lore"))
        {
            String progressBar = Color.getProgressBar(0.0d, 100.0d, plugin.getConfig().getInt("progress-bar.total-bars"), plugin.getConfig().getString("progress-bar.symbol"), ChatColor.valueOf(plugin.getConfig().getString("progress-bar.completed-color")).toString(), ChatColor.valueOf(plugin.getConfig().getString("progress-bar.uncompleted-color")).toString());
            lore.add(Color.color(string.replace("%level%", "1").replace("%experience%", progressBar).replace("%experience-percent%", "0%")));
        }
        meta.setLore(lore);

        // Add vanilla enchants to the pickaxe
        Map<AbstractEnchant, Integer> enchantLevels = this.getEnchantLevelsFrom(meta);
        enchantLevels.keySet().stream().filter(enchant -> VanillaEnchant.class.isAssignableFrom(enchant.getClass())).forEach(enchant -> meta.addEnchant(Enchantment.getByName(enchant.getEnchant().getVanillaName()), enchantLevels.get(enchant), true));

        // Set unbreakable & add item flags
        meta.spigot().setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

        // Assign meta & return pick
        pickaxe.setItemMeta(meta);
        return pickaxe;
    }

    public Map<AbstractEnchant, Integer> getEnchantLevelsFrom(ItemStack pickaxe) { return this.getEnchantLevelsFrom(pickaxe.getItemMeta()); }
    public Map<AbstractEnchant, Integer> getEnchantLevelsFrom(ItemMeta meta)
    {
        // Args
        Map<AbstractEnchant, Integer> enchantLevels = new HashMap<>();

        // Loop over the item's lore
        for (String text : meta.getLore())
        {
            // Remove all numbers from the text and see if an enchant exists
            String enchantName = Color.strip(text).replaceAll("[0-9]", "").trim();
            Enchant enchant = Enchant.getEnchantByName(enchantName);
            if (enchant == null) continue;

            // Get the abstract enchant from the enchant
            AbstractEnchant abstractEnchant = plugin.getEnchantManager().getEnchantByType(enchant);
            if (abstractEnchant == null) continue;

            // Get the level by removing all non-numerical characters
            int level = Integer.parseInt(text.replaceAll("[^0-9]", "").trim());
            enchantLevels.put(abstractEnchant, level);
        }

        // Return
        return enchantLevels;
    }

}
