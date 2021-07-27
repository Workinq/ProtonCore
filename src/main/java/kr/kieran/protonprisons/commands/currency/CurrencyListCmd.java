package kr.kieran.protonprisons.commands.currency;

import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.commands.AbstractCommand;
import kr.kieran.protonprisons.commands.CommandRequirements;
import kr.kieran.protonprisons.objects.ProtonCurrency;
import kr.kieran.protonprisons.utilities.Color;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CurrencyListCmd extends AbstractCommand
{

    public CurrencyListCmd()
    {
        // Aliases
        this.addAliases("list", "l");

        // Requirements
        this.setCommandRequirements(new CommandRequirements.Builder("protonprisons.commands.currency.list").playerOnly(false).build());
    }

    @Override
    public void perform(ProtonPrisonsPlugin plugin, CommandSender sender, List<String> args)
    {
        // Create a string builder
        StringBuilder builder = new StringBuilder();
        for (ProtonCurrency currency : plugin.getCurrencyManager().getCurrencies())
        {
            // Append currency to builder
            builder.append(Color.color(plugin.getConfig().getString("messages.currency.formats.currency").replace("%symbol%", currency.getSymbol()).replace("%name%", currency.getName()).replace("%id%", String.valueOf(currency.getId()))));
        }

        // Send message
        sender.sendMessage(Color.color(plugin.getConfig().getString("messages.currency.formats.currency-list").replace("%currencies%", builder.toString().trim())));
    }

}
