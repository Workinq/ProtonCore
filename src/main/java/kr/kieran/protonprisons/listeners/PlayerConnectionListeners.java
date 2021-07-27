package kr.kieran.protonprisons.listeners;

import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListeners implements Listener
{

    private final ProtonPrisonsPlugin plugin;

    public PlayerConnectionListeners(ProtonPrisonsPlugin plugin)
    {
        this.plugin = plugin;
    }

    // JOIN
    @EventHandler(priority = EventPriority.LOW)
    public void login(AsyncPlayerPreLoginEvent event)
    {
        // Check if another plugin has disallowed the player from logging in
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) return;

        // Load the player's profile, ignore the callback since we don't need it
        plugin.getProfileManager().loadProfile(event.getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void join(PlayerLoginEvent event)
    {
        if (!plugin.getProfileManager().isProfileLoaded(event.getPlayer()))
        {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Your prisons profile couldn't be loaded, please join again");
        }
    }

    // QUIT
    @EventHandler(priority = EventPriority.MONITOR) public void quit(PlayerQuitEvent event) { plugin.getProfileManager().removeProfile(plugin.getProfileManager().getByPlayer(event.getPlayer())); }
    @EventHandler(priority = EventPriority.MONITOR) public void kick(PlayerKickEvent event) { plugin.getProfileManager().removeProfile(plugin.getProfileManager().getByPlayer(event.getPlayer())); }

}
