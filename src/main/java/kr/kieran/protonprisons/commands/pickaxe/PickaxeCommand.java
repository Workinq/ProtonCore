/*
 * Copyright (c) 2021. Made by Kieraaaan for the plugin ProtonCore
 */

package kr.kieran.protonprisons.commands.pickaxe;

import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.commands.AbstractCommand;
import kr.kieran.protonprisons.commands.CommandRequirements;
import kr.kieran.protonprisons.utilities.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PickaxeCommand extends AbstractCommand implements CommandExecutor
{

    private final ProtonPrisonsPlugin plugin;
    private final PickaxeCommand baseCommand;

    public PickaxeGiveCmd giveCmd = new PickaxeGiveCmd();

    public PickaxeCommand(ProtonPrisonsPlugin plugin)
    {
        // Set variables
        this.plugin = plugin;
        this.baseCommand = this;

        // Aliases
        this.addAliases("pickaxe");

        // Children
        this.addChild(giveCmd);

        // Requirements
        this.setCommandRequirements(new CommandRequirements.Builder("protonprisons.commands.pickaxe").playerOnly(false).build());
    }

    @Override
    public void perform(ProtonPrisonsPlugin plugin, CommandSender sender, List<String> args)
    {
        sender.sendMessage(Color.color(plugin.getConfig().getString("messages.usages.pickaxe.basecommand")));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        this.execute(plugin, sender, new ArrayList<>(Arrays.asList(args)));
        return true;
    }

}
