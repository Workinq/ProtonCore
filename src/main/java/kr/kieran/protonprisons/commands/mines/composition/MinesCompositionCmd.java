package kr.kieran.protonprisons.commands.mines.composition;

import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.commands.CommandRequirements;
import kr.kieran.protonprisons.utilities.Color;
import org.bukkit.command.CommandSender;

import java.util.List;

public class MinesCompositionCmd extends MinesCompositionCommand
{

    public MinesCompositionAddCmd cmdAdd = new MinesCompositionAddCmd();
    public MinesCompositionViewCmd cmdList = new MinesCompositionViewCmd();
    public MinesCompositionDeleteCmd cmdDelete = new MinesCompositionDeleteCmd();

    public MinesCompositionCmd()
    {
        // Aliases
        this.addAliases("composition", "comp");

        // Children
        this.addChildren(cmdAdd, cmdList, cmdDelete);

        // Requirements
        this.setCommandRequirements(new CommandRequirements.Builder("protonprisons.commands.mines.composition").playerOnly(false).build());
    }

    @Override
    public void perform(ProtonPrisonsPlugin plugin, CommandSender sender, List<String> args)
    {
        sender.sendMessage(Color.color(plugin.getConfig().getString("messages.usages.mines.composition.basecommand")));
    }

}
