package kr.kieran.protonprisons.enchants.enchants;

import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.enchants.AbstractEnchant;
import kr.kieran.protonprisons.enchants.CustomEnchant;
import kr.kieran.protonprisons.enchants.Enchant;
import kr.kieran.protonprisons.objects.ProtonMine;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RelocationsPaypalEnchant extends AbstractEnchant implements CustomEnchant
{

    private final ProtonPrisonsPlugin plugin;

    public RelocationsPaypalEnchant(ProtonPrisonsPlugin plugin)
    {
        super(Enchant.RELOCATIONS_PAYPAL);
        this.plugin = plugin;
    }

    @Override public String getEnchantName() { return plugin.getEnchants().getString("enchants.relocations-paypal.name"); }
    @Override public String getLoreName() { return plugin.getEnchants().getString("enchants.relocations-paypal.lore"); }
    @Override public int getMaxLevel() { return plugin.getEnchants().getInt("enchants.relocations-paypal.max-level"); }
    @Override public double getBaseCost() { return plugin.getEnchants().getDouble("enchants.relocations-paypal.base-cost"); }
    @Override public double getCostMultiplier() { return plugin.getEnchants().getDouble("enchants.relocations-paypal.cost-multiplier"); }
    @Override public double getBaseValue() { return plugin.getEnchants().getDouble("enchants.relocations-paypal.base-value"); }
    @Override public double getMultiplier() { return plugin.getEnchants().getDouble("enchants.relocations-paypal.value-multiplier"); }

    @Override
    public void perform(Player player, ProtonMine mine, Location location, int level)
    {
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), plugin.getEnchants().getString("enchants.relocations-paypal.command").replace("%player%", player.getName()));
    }

}
