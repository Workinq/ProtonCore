package kr.kieran.protonprisons.enchants.enchants;

import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.enchants.AbstractEnchant;
import kr.kieran.protonprisons.enchants.Enchant;
import kr.kieran.protonprisons.enchants.VanillaEnchant;

public class FortuneEnchant extends AbstractEnchant implements VanillaEnchant
{

    private final ProtonPrisonsPlugin plugin;

    public FortuneEnchant(ProtonPrisonsPlugin plugin)
    {
        super(Enchant.FORTUNE);
        this.plugin = plugin;
    }

    @Override public String getEnchantName() { return plugin.getEnchants().getString("enchants.fortune.name"); }
    @Override public String getLoreName() { return plugin.getEnchants().getString("enchants.fortune.lore"); }
    @Override public int getMaxLevel() { return plugin.getEnchants().getInt("enchants.fortune.max-level"); }
    @Override public double getBaseCost() { return plugin.getEnchants().getDouble("enchants.fortune.base-cost"); }
    @Override public double getCostMultiplier() { return plugin.getEnchants().getDouble("enchants.fortune.cost-multiplier"); }

}
