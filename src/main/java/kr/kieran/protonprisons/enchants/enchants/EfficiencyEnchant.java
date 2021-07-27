package kr.kieran.protonprisons.enchants.enchants;

import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.enchants.AbstractEnchant;
import kr.kieran.protonprisons.enchants.Enchant;
import kr.kieran.protonprisons.enchants.VanillaEnchant;

public class EfficiencyEnchant extends AbstractEnchant implements VanillaEnchant
{

    private final ProtonPrisonsPlugin plugin;

    public EfficiencyEnchant(ProtonPrisonsPlugin plugin)
    {
        super(Enchant.EFFICIENCY);
        this.plugin = plugin;
    }

    @Override public String getEnchantName() { return plugin.getEnchants().getString("enchants.efficiency.name"); }
    @Override public String getLoreName() { return plugin.getEnchants().getString("enchants.efficiency.lore"); }
    @Override public int getMaxLevel() { return plugin.getEnchants().getInt("enchants.efficiency.max-level"); }
    @Override public double getBaseCost() { return plugin.getEnchants().getDouble("enchants.efficiency.base-cost"); }
    @Override public double getCostMultiplier() { return plugin.getEnchants().getDouble("enchants.efficiency.cost-multiplier"); }

}
