package kr.kieran.protonprisons.enchants.enchants;

import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.enchants.AbstractEnchant;
import kr.kieran.protonprisons.enchants.Enchant;
import kr.kieran.protonprisons.enchants.PotionEnchant;
import org.bukkit.entity.Player;

public class SpeedEnchant extends AbstractEnchant implements PotionEnchant
{

    private final ProtonPrisonsPlugin plugin;

    public SpeedEnchant(ProtonPrisonsPlugin plugin)
    {
        super(Enchant.SPEED);
        this.plugin = plugin;
    }

    @Override public String getEnchantName() { return plugin.getEnchants().getString("enchants.speed.name"); }
    @Override public String getLoreName() { return plugin.getEnchants().getString("enchants.speed.lore"); }
    @Override public int getMaxLevel() { return plugin.getEnchants().getInt("enchants.speed.max-level"); }
    @Override public double getBaseCost() { return plugin.getEnchants().getDouble("enchants.speed.base-cost"); }
    @Override public double getCostMultiplier() { return plugin.getEnchants().getDouble("enchants.speed.cost-multiplier"); }

    @Override
    public void apply(Player player, int level)
    {
    }

}
