/*
 * Copyright (c) 2021. Made by Kieraaaan for the plugin ProtonCore
 */

package kr.kieran.protonprisons.listeners;

import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.enchants.AbstractEnchant;
import kr.kieran.protonprisons.enchants.Enchant;
import kr.kieran.protonprisons.objects.ProtonMine;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class BlockBreakListeners implements Listener
{

    private final ProtonPrisonsPlugin plugin;

    public BlockBreakListeners(ProtonPrisonsPlugin plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event)
    {
        // Get the item the player was holding when they broke the block
        ItemStack item = event.getPlayer().getItemInHand();
        if (item == null || !item.getType().name().endsWith("_PICKAXE")) return;
        if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) return;

        // Get the mine at which the block is located
        Block block = event.getBlock();
        ProtonMine mine = plugin.getMineManager().getLastMine(block.getLocation(), plugin.getProfileManager().getByPlayer(event.getPlayer()));
        if (mine == null) return;

        // Cancel event
        event.setCancelled(true);

        // Execute the enchants
        plugin.getEnchantManager().executeCustomEnchant(plugin.getPickaxeManager().getEnchantLevelsFrom(item), event.getPlayer(), mine, block.getLocation());
    }

}
