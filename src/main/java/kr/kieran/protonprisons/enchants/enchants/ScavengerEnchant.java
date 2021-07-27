package kr.kieran.protonprisons.enchants.enchants;

import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.enchants.AbstractEnchant;
import kr.kieran.protonprisons.enchants.CustomEnchant;
import kr.kieran.protonprisons.enchants.Enchant;
import kr.kieran.protonprisons.objects.ProtonMine;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ScavengerEnchant extends AbstractEnchant implements CustomEnchant
{

    private final ProtonPrisonsPlugin plugin;

    public ScavengerEnchant(ProtonPrisonsPlugin plugin)
    {
        super(Enchant.SCAVENGER);
        this.plugin = plugin;
    }

    @Override public String getEnchantName() { return plugin.getEnchants().getString("enchants.scavenger.name"); }
    @Override public String getLoreName() { return plugin.getEnchants().getString("enchants.scavenger.lore"); }
    @Override public int getMaxLevel() { return plugin.getEnchants().getInt("enchants.scavenger.max-level"); }
    @Override public double getBaseCost() { return plugin.getEnchants().getDouble("enchants.scavenger.base-cost"); }
    @Override public double getCostMultiplier() { return plugin.getEnchants().getDouble("enchants.scavenger.cost-multiplier"); }
    @Override public double getBaseValue() { return plugin.getEnchants().getDouble("enchants.scavenger.base-value"); }
    @Override public double getMultiplier() { return plugin.getEnchants().getDouble("enchants.scavenger.value-multiplier"); }

    @Override
    public void perform(Player player, ProtonMine mine, Location location, int level)
    {
    }

}
