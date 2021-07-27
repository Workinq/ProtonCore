package kr.kieran.protonprisons.commands.currency;

import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.commands.AbstractCommand;
import kr.kieran.protonprisons.commands.CommandRequirements;
import kr.kieran.protonprisons.objects.ProtonCurrency;
import kr.kieran.protonprisons.utilities.Color;
import kr.kieran.protonprisons.utilities.NumberUtil;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CurrencyDeleteCmd extends AbstractCommand
{

    public CurrencyDeleteCmd()
    {
        // Aliases
        this.addAliases("delete", "del", "d");

        // Requirements
        this.setCommandRequirements(new CommandRequirements.Builder("protonprisons.commands.currency.delete").playerOnly(false).build());
    }

    @Override
    public void perform(ProtonPrisonsPlugin plugin, CommandSender sender, List<String> args)
    {
        // Args check
        if (args.size() != 1)
        {
            sender.sendMessage(Color.color(plugin.getConfig().getString("messages.usages.currency.delete")));
            return;
        }

        // Check if the currency doesn't exist
        ProtonCurrency currency = this.getCurrencyFromText(plugin, args.get(0));
        if (currency == null)
        {
            sender.sendMessage(Color.color(plugin.getConfig().getString("messages.currency.doesnt-exist")));
            return;
        }

        // Delete the currency
        plugin.getCurrencyManager().deleteCurrency(currency, deletedCurrency -> {
            if (deletedCurrency == null)
            {
                sender.sendMessage(Color.color(plugin.getConfig().getString("messages.currency.error")));
                return;
            }
            sender.sendMessage(Color.color(plugin.getConfig().getString("messages.currency.deleted").replace("%symbol%", deletedCurrency.getSymbol()).replace("%currency%", deletedCurrency.getName()).replace("%id%", String.valueOf(deletedCurrency.getId()))));
        });
    }

    private ProtonCurrency getCurrencyFromText(ProtonPrisonsPlugin plugin, String text)
    {
        return NumberUtil.isInt(text) ? plugin.getCurrencyManager().getCurrencyById(Integer.parseInt(text)) : plugin.getCurrencyManager().getCurrencyByName(text);
    }

}
