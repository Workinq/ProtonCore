/*
 * Copyright (c) 2021. Made by Kieraaaan for the plugin ProtonCore
 */

package kr.kieran.protonprisons.listeners;

import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

public class ItemSwitchListeners implements Listener
{

    private final ProtonPrisonsPlugin plugin;

    public ItemSwitchListeners(ProtonPrisonsPlugin plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void hold(PlayerItemHeldEvent event)
    {
        // Args
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItem(event.getNewSlot());
        if (item == null || !item.getType().name().endsWith("_PICKAXE")) return;


    }

}
