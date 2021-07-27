package kr.kieran.protonprisons.commands.currency;

import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.commands.AbstractCommand;
import kr.kieran.protonprisons.commands.CommandRequirements;
import kr.kieran.protonprisons.objects.ProtonCurrency;
import kr.kieran.protonprisons.utilities.Color;
import kr.kieran.protonprisons.utilities.NumberUtil;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CurrencyViewCmd extends AbstractCommand
{

    public CurrencyViewCmd()
    {
        // Aliases
        this.addAliases("view", "v");

        // Requirements
        this.setCommandRequirements(new CommandRequirements.Builder("protonprisons.commands.currency.view").playerOnly(false).build());
    }

    @Override
    public void perform(ProtonPrisonsPlugin plugin, CommandSender sender, List<String> args)
    {
        // Args check
        if (args.size() != 1)
        {
            sender.sendMessage(Color.color(plugin.getConfig().getString("messages.usages.currency.view")));
            return;
        }

        // Check if the currency doesn't exist
        ProtonCurrency currency = this.getCurrencyFromText(plugin, args.get(0));
        if (currency == null)
        {
            sender.sendMessage(Color.color(plugin.getConfig().getString("messages.currency.doesnt-exist")));
            return;
        }

        // Inform
        sender.sendMessage(Color.color(plugin.getConfig().getString("messages.currency.formats.currency-view").replace("%currency%", args.get(0)).replace("%name%", currency.getName()).replace("%symbol%", currency.getSymbol()).replace("%id%", String.valueOf(currency.getId()))));
    }

    private ProtonCurrency getCurrencyFromText(ProtonPrisonsPlugin plugin, String text)
    {
        return NumberUtil.isInt(text) ? plugin.getCurrencyManager().getCurrencyById(Integer.parseInt(text)) : plugin.getCurrencyManager().getCurrencyByName(text);
    }

}
