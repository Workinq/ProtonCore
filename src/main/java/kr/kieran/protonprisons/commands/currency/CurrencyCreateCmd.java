package kr.kieran.protonprisons.commands.currency;

import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.commands.AbstractCommand;
import kr.kieran.protonprisons.commands.CommandRequirements;
import kr.kieran.protonprisons.objects.ProtonCurrency;
import kr.kieran.protonprisons.utilities.Color;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CurrencyCreateCmd extends AbstractCommand
{

    public CurrencyCreateCmd()
    {
        // Aliases
        this.addAliases("create", "c");

        // Requirements
        this.setCommandRequirements(new CommandRequirements.Builder("protonprisons.commands.currency.create").playerOnly(false).build());
    }

    @Override
    public void perform(ProtonPrisonsPlugin plugin, CommandSender sender, List<String> args)
    {
        // Args check
        if (args.size() != 2)
        {
            sender.sendMessage(Color.color(plugin.getConfig().getString("messages.usages.currency.create")));
            return;
        }

        // Symbol length check
        if (args.get(1).length() != 1)
        {
            sender.sendMessage(Color.color(plugin.getConfig().getString("messages.currency.invalid-symbol")));
            return;
        }

        // Check if a currency with the same name already exists
        ProtonCurrency currency = plugin.getCurrencyManager().getCurrencyByName(args.get(0));
        if (currency != null)
        {
            sender.sendMessage(Color.color(plugin.getConfig().getString("messages.currency.already-exists")));
            return;
        }

        // Create the currency
        plugin.getCurrencyManager().createCurrency(args.get(0), args.get(1), newCurrency -> {
            if (newCurrency == null)
            {
                sender.sendMessage(Color.color(plugin.getConfig().getString("messages.currency.error")));
                return;
            }
            sender.sendMessage(Color.color(plugin.getConfig().getString("messages.currency.created").replace("%symbol%", newCurrency.getSymbol()).replace("%currency%", newCurrency.getName()).replace("%id%", String.valueOf(newCurrency.getId()))));
        });
    }

}
