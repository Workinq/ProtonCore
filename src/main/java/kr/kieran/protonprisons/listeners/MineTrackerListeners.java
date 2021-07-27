/*
 * Copyright (c) 2021. Made by Kieraaaan for the plugin ProtonCore
 */

package kr.kieran.protonprisons.listeners;

import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class MineTrackerListeners implements Listener
{

    private final ProtonPrisonsPlugin plugin;

    public MineTrackerListeners(ProtonPrisonsPlugin plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void blockBreak(BlockBreakEvent event)
    {
        plugin.getMineManager().getLastMine(event.getBlock().getLocation(), plugin.getProfileManager().getByPlayer(event.getPlayer()));
    }

}
