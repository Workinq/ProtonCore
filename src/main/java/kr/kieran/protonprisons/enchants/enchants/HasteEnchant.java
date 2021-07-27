package kr.kieran.protonprisons.enchants.enchants;

import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.enchants.AbstractEnchant;
import kr.kieran.protonprisons.enchants.Enchant;
import kr.kieran.protonprisons.enchants.PotionEnchant;
import org.bukkit.entity.Player;

public class HasteEnchant extends AbstractEnchant implements PotionEnchant
{

    private final ProtonPrisonsPlugin plugin;

    public HasteEnchant(ProtonPrisonsPlugin plugin)
    {
        super(Enchant.HASTE);
        this.plugin = plugin;
    }

    @Override public String getEnchantName() { return plugin.getEnchants().getString("enchants.haste.name"); }
    @Override public String getLoreName() { return plugin.getEnchants().getString("enchants.haste.lore"); }
    @Override public int getMaxLevel() { return plugin.getEnchants().getInt("enchants.haste.max-level"); }
    @Override public double getBaseCost() { return plugin.getEnchants().getDouble("enchants.haste.base-cost"); }
    @Override public double getCostMultiplier() { return plugin.getEnchants().getDouble("enchants.haste.cost-multiplier"); }

    @Override
    public void apply(Player player, int level)
    {
    }

}
