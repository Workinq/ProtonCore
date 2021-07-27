package kr.kieran.protonprisons.commands.mines;

import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.commands.AbstractCommand;
import kr.kieran.protonprisons.commands.CommandRequirements;
import kr.kieran.protonprisons.commands.mines.composition.MinesCompositionCmd;
import kr.kieran.protonprisons.utilities.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MinesCommand extends AbstractCommand implements CommandExecutor
{

    private final ProtonPrisonsPlugin plugin;
    private final MinesCommand baseCommand;

    public MinesCreateCmd cmdCreate = new MinesCreateCmd();
    public MinesListCmd cmdList = new MinesListCmd();
    public MinesCompositionCmd cmdComposition = new MinesCompositionCmd();
    public MinesResetCmd cmdReset = new MinesResetCmd();
    public MinesSetteleportCmd cmdSetteleport = new MinesSetteleportCmd();

    public MinesCommand(ProtonPrisonsPlugin plugin)
    {
        // Set variables
        this.plugin = plugin;
        this.baseCommand = this;

        // Aliases
        this.addAliases("pickaxe");

        // Children
        this.addChildren(cmdCreate, cmdList, cmdComposition, cmdReset, cmdSetteleport);

        // Requirements
        this.setCommandRequirements(new CommandRequirements.Builder("protonprisons.commands.mines").playerOnly(false).build());
    }

    @Override
    public void perform(ProtonPrisonsPlugin plugin, CommandSender sender, List<String> args)
    {
        sender.sendMessage(Color.color(plugin.getConfig().getString("messages.usages.mines.basecommand")));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        this.execute(plugin, sender, new ArrayList<>(Arrays.asList(args)));
        return true;
    }

}
