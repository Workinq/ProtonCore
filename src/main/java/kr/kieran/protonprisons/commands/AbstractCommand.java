package kr.kieran.protonprisons.commands;

import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.utilities.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class AbstractCommand
{

    // Aliases
    private final List<String> aliases = new ArrayList<>();
    public void addAliases(String... aliases) { this.aliases.addAll(Arrays.asList(aliases)); }
    public List<String> getAliases() { return Collections.unmodifiableList(aliases); }

    // Children
    private final List<AbstractCommand> children = new ArrayList<>();
    public List<AbstractCommand> getChildren() { return Collections.unmodifiableList(children); }

    // Requirements
    private CommandRequirements commandRequirements = new CommandRequirements.Builder(null).build();
    public CommandRequirements getCommandRequirements() { return commandRequirements; }
    public void setCommandRequirements(CommandRequirements commandRequirements) { this.commandRequirements = commandRequirements; }

    // Abstract
    public abstract void perform(ProtonPrisonsPlugin plugin, CommandSender sender, List<String> args);

    public void execute(ProtonPrisonsPlugin plugin, CommandSender sender, List<String> args)
    {
        // Check if there are child commands
        if (args.size() > 0)
        {
            // Check for a child command with the same alias
            for (AbstractCommand child : this.children)
            {
                if (child.getAliases().contains(args.get(0).toLowerCase()))
                {
                    args.remove(0);
                    child.execute(plugin, sender, args);
                    return;
                }
            }
        }

        // Command requirements check
        if (!this.canExecuteCommand(plugin, sender)) return;

        // Execute base command
        this.perform(plugin, sender, args);
    }

    private boolean canExecuteCommand(ProtonPrisonsPlugin plugin, CommandSender sender)
    {
        // Check if the command is player-only
        if (!(sender instanceof Player) && commandRequirements.isPlayerOnly())
        {
            sender.sendMessage(Color.color(plugin.getConfig().getString("messages.not-a-player")));
            return false;
        }

        // If the permission node is null, anybody can use the command
        if (commandRequirements.getPermission() == null) return true;

        // Check if the player has permission to use the command
        if (!sender.hasPermission(commandRequirements.getPermission()))
        {
            sender.sendMessage(Color.color(plugin.getConfig().getString("messages.no-permission")));
            return false;
        }

        // Return
        return true;
    }

    public void addChild(AbstractCommand child) { this.children.add(child); }
    public void addChildren(AbstractCommand... children) { this.children.addAll(Arrays.asList(children)); }

}
