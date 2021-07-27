package kr.kieran.protonprisons.commands.currency;

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

public class CurrencyCommand extends AbstractCommand implements CommandExecutor
{

    private final ProtonPrisonsPlugin plugin;
    private final CurrencyCommand baseCommand;

    public CurrencyCreateCmd cmdCreate = new CurrencyCreateCmd();
    public CurrencyDeleteCmd cmdDelete = new CurrencyDeleteCmd();
    public CurrencyListCmd cmdList = new CurrencyListCmd();
    public CurrencyViewCmd cmdView = new CurrencyViewCmd();

    public CurrencyCommand(ProtonPrisonsPlugin plugin)
    {
        // Set variables
        this.plugin = plugin;
        this.baseCommand = this;

        // Aliases
        this.addAliases("currency");

        // Children
        this.addChildren(cmdCreate, cmdDelete, cmdList, cmdView);

        // Requirements
        this.setCommandRequirements(new CommandRequirements.Builder("protonprisons.commands.currency").playerOnly(false).build());
    }

    @Override
    public void perform(ProtonPrisonsPlugin plugin, CommandSender sender, List<String> args)
    {
        sender.sendMessage(Color.color(plugin.getConfig().getString("messages.usages.currency.basecommand")));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        this.execute(plugin, sender, new ArrayList<>(Arrays.asList(args)));
        return true;
    }

}
