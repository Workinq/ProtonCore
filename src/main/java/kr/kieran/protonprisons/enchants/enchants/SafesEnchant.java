package kr.kieran.protonprisons.enchants.enchants;

import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.enchants.AbstractEnchant;
import kr.kieran.protonprisons.enchants.CustomEnchant;
import kr.kieran.protonprisons.enchants.Enchant;
import kr.kieran.protonprisons.objects.ProtonMine;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SafesEnchant extends AbstractEnchant implements CustomEnchant
{

    private final ProtonPrisonsPlugin plugin;

    public SafesEnchant(ProtonPrisonsPlugin plugin)
    {
        super(Enchant.SAFES);
        this.plugin = plugin;
    }

    @Override public String getEnchantName() { return plugin.getEnchants().getString("enchants.safes.name"); }
    @Override public String getLoreName() { return plugin.getEnchants().getString("enchants.safes.lore"); }
    @Override public int getMaxLevel() { return plugin.getEnchants().getInt("enchants.safes.max-level"); }
    @Override public double getBaseCost() { return plugin.getEnchants().getDouble("enchants.safes.base-cost"); }
    @Override public double getCostMultiplier() { return plugin.getEnchants().getDouble("enchants.safes.cost-multiplier"); }
    @Override public double getBaseValue() { return plugin.getEnchants().getDouble("enchants.safes.base-value"); }
    @Override public double getMultiplier() { return plugin.getEnchants().getDouble("enchants.safes.value-multiplier"); }

    @Override
    public void perform(Player player, ProtonMine mine, Location location, int level)
    {
    }

}
