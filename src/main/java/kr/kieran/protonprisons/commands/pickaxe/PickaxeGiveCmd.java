/*
 * Copyright (c) 2021. Made by Kieraaaan for the plugin ProtonCore
 */

package kr.kieran.protonprisons.commands.pickaxe;

import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.commands.AbstractCommand;
import kr.kieran.protonprisons.commands.CommandRequirements;
import kr.kieran.protonprisons.utilities.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PickaxeGiveCmd extends AbstractCommand
{

    public PickaxeGiveCmd()
    {
        // Aliases
        this.addAliases("give");

        // Requirements
        this.setCommandRequirements(new CommandRequirements.Builder("protonprisons.commands.pickaxe.give").playerOnly(false).build());
    }

    @Override
    public void perform(ProtonPrisonsPlugin plugin, CommandSender sender, List<String> args)
    {
        // Args check
        if (args.size() != 1 && args.size() != 2)
        {
            sender.sendMessage(Color.color(plugin.getConfig().getString("messages.usages.pickaxe.give")));
            return;
        }

        // Get the target & check if they exist
        Player target = plugin.getServer().getPlayer(args.get(0));
        if (target == null)
        {
            sender.sendMessage(Color.color(plugin.getConfig().getString("messages.invalid-player").replace("%player%", args.get(0))));
            return;
        }

        // Get the pickaxe to be given
        String pickaxeName = args.size() == 2 ? args.get(1) : "default";
        if (!plugin.getConfig().isSet("pickaxes." + pickaxeName))
        {
            sender.sendMessage(Color.color(plugin.getConfig().getString("messages.pickaxe.invalid-pickaxe").replace("%pickaxe%", pickaxeName)));
            return;
        }

        // Give the pickaxe
        ItemStack pickaxe = plugin.getPickaxeManager().getPickaxe(pickaxeName);
        if (target.getInventory().firstEmpty() == -1)
        {
            target.getWorld().dropItemNaturally(target.getLocation(), pickaxe);
        }
        else
        {
            target.getInventory().addItem(pickaxe);
        }

        // Inform
        sender.sendMessage(Color.color(plugin.getConfig().getString("messages.pickaxe.gave-pickaxe").replace("%player%", target.getName()).replace("%pickaxe%", pickaxeName)));
        target.sendMessage(Color.color(plugin.getConfig().getString("messages.pickaxe.received-pickaxe").replace("%pickaxe%", pickaxeName)));
    }

}
