package kr.kieran.protonprisons.enchants.enchants;

import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.enchants.AbstractEnchant;
import kr.kieran.protonprisons.enchants.CustomEnchant;
import kr.kieran.protonprisons.enchants.Enchant;
import kr.kieran.protonprisons.objects.ProtonMine;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class KeyFinderEnchant extends AbstractEnchant implements CustomEnchant
{

    private final ProtonPrisonsPlugin plugin;

    public KeyFinderEnchant(ProtonPrisonsPlugin plugin)
    {
        super(Enchant.KEY_FINDER);
        this.plugin = plugin;
    }

    @Override public String getEnchantName() { return plugin.getEnchants().getString("enchants.key-finder.name"); }
    @Override public String getLoreName() { return plugin.getEnchants().getString("enchants.key-finder.lore"); }
    @Override public int getMaxLevel() { return plugin.getEnchants().getInt("enchants.key-finder.max-level"); }
    @Override public double getBaseCost() { return plugin.getEnchants().getDouble("enchants.key-finder.base-cost"); }
    @Override public double getCostMultiplier() { return plugin.getEnchants().getDouble("enchants.key-finder.cost-multiplier"); }
    @Override public double getBaseValue() { return plugin.getEnchants().getDouble("enchants.key-finder.base-value"); }
    @Override public double getMultiplier() { return plugin.getEnchants().getDouble("enchants.key-finder.value-multiplier"); }

    @Override
    public void perform(Player player, ProtonMine mine, Location location, int level)
    {
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), plugin.getEnchants().getString("enchants.key-finder.command").replace("%player%", player.getName()));
    }

}
