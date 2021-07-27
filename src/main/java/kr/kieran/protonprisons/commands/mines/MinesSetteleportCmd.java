/*
 * Copyright (c) 2021. Made by Kieraaaan for the plugin ProtonCore
 */

package kr.kieran.protonprisons.commands.mines;

import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.commands.AbstractCommand;
import kr.kieran.protonprisons.commands.CommandRequirements;
import kr.kieran.protonprisons.objects.ProtonMine;
import kr.kieran.protonprisons.utilities.Color;
import kr.kieran.protonprisons.utilities.NumberUtil;
import kr.kieran.protonprisons.utilities.SerializationUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class MinesSetteleportCmd extends AbstractCommand
{

    public MinesSetteleportCmd()
    {
        // Aliases
        this.addAliases("setteleport", "settp", "tp");

        // Requirements
        this.setCommandRequirements(new CommandRequirements.Builder("protonprisons.commands.mines.setteleport").playerOnly(true).build());
    }

    @Override
    public void perform(ProtonPrisonsPlugin plugin, CommandSender sender, List<String> args)
    {
        // Args check
        if (args.size() != 1)
        {
            sender.sendMessage(Color.color(plugin.getConfig().getString("messages.usages.mines.setteleport")));
            return;
        }

        // Check if a mine doesn't exist with the name or id
        ProtonMine mine = this.getMineFromText(plugin, args.get(0));
        if (mine == null)
        {
            sender.sendMessage(Color.color(plugin.getConfig().getString("messages.mines.doesnt-exist")));
            return;
        }

        // Set the location
        Player player = (Player) sender;
        mine.setTeleportLocation(SerializationUtil.serializeLocation(player.getLocation()));

        // Update the teleport location
        plugin.getMineManager().setMineTeleport(mine, () -> sender.sendMessage(Color.color(plugin.getConfig().getString("messages.mines.teleport-set").replace("%mine%", args.get(0)))));
    }

    private ProtonMine getMineFromText(ProtonPrisonsPlugin plugin, String text)
    {
        return NumberUtil.isInt(text) ? plugin.getMineManager().getMineById(Integer.parseInt(text)) : plugin.getMineManager().getMineByName(text);
    }

}
