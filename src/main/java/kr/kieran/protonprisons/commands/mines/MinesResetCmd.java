package kr.kieran.protonprisons.commands.mines;

import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.commands.AbstractCommand;
import kr.kieran.protonprisons.commands.CommandRequirements;
import kr.kieran.protonprisons.objects.ProtonMine;
import kr.kieran.protonprisons.utilities.Color;
import kr.kieran.protonprisons.utilities.NumberUtil;
import org.bukkit.command.CommandSender;

import java.util.List;

public class MinesResetCmd extends AbstractCommand
{

    public MinesResetCmd()
    {
        // Aliases
        this.addAliases("reset");

        // Requirements
        this.setCommandRequirements(new CommandRequirements.Builder("protonprisons.commands.mines.reset").playerOnly(false).build());
    }

    @Override
    public void perform(ProtonPrisonsPlugin plugin, CommandSender sender, List<String> args)
    {
        // Args check
        if (args.size() != 1)
        {
            sender.sendMessage(Color.color(plugin.getConfig().getString("messages.usages.mines.reset")));
            return;
        }

        // Check if a mine doesn't exist with the name or id
        ProtonMine mine = this.getMineFromText(plugin, args.get(0));
        if (mine == null)
        {
            sender.sendMessage(Color.color(plugin.getConfig().getString("messages.mines.doesnt-exist")));
            return;
        }

        // Reset mine & inform
        mine.reset();
        sender.sendMessage(Color.color(plugin.getConfig().getString("messages.mines.reset").replace("%mine%", args.get(0))));
    }

    private ProtonMine getMineFromText(ProtonPrisonsPlugin plugin, String text)
    {
        return NumberUtil.isInt(text) ? plugin.getMineManager().getMineById(Integer.parseInt(text)) : plugin.getMineManager().getMineByName(text);
    }

}
