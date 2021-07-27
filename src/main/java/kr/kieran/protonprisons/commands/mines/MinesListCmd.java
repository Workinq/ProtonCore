package kr.kieran.protonprisons.commands.mines;

import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.commands.AbstractCommand;
import kr.kieran.protonprisons.commands.CommandRequirements;
import kr.kieran.protonprisons.objects.ProtonMine;
import kr.kieran.protonprisons.utilities.Color;
import org.bukkit.command.CommandSender;

import java.util.List;

public class MinesListCmd extends AbstractCommand
{

    public MinesListCmd()
    {
        // Aliases
        this.addAliases("list", "l");

        // Requirements
        this.setCommandRequirements(new CommandRequirements.Builder("protonprisons.commands.mines.list").playerOnly(false).build());
    }

    @Override
    public void perform(ProtonPrisonsPlugin plugin, CommandSender sender, List<String> args)
    {
        // Create a string builder
        StringBuilder builder = new StringBuilder();
        for (ProtonMine mine : plugin.getMineManager().getMines())
        {
            // Append currency to builder
            builder.append(Color.color(plugin.getConfig().getString("messages.mines.formats.mines").replace("%name%", mine.getName()).replace("%id%", String.valueOf(mine.getId()))));
        }

        // Send message
        sender.sendMessage(Color.color(plugin.getConfig().getString("messages.mines.formats.mines-list").replace("%mines%", builder.toString().trim())));
    }

}
