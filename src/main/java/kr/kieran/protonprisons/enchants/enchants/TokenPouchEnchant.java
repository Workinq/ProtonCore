package kr.kieran.protonprisons.enchants.enchants;

import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.enchants.AbstractEnchant;
import kr.kieran.protonprisons.enchants.CustomEnchant;
import kr.kieran.protonprisons.enchants.Enchant;
import kr.kieran.protonprisons.objects.ProtonMine;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TokenPouchEnchant extends AbstractEnchant implements CustomEnchant
{

    private final ProtonPrisonsPlugin plugin;

    public TokenPouchEnchant(ProtonPrisonsPlugin plugin)
    {
        super(Enchant.TOKEN_POUCH);
        this.plugin = plugin;
    }

    @Override public String getEnchantName() { return plugin.getEnchants().getString("enchants.token-pouch.name"); }
    @Override public String getLoreName() { return plugin.getEnchants().getString("enchants.token-pouch.lore"); }
    @Override public int getMaxLevel() { return plugin.getEnchants().getInt("enchants.token-pouch.max-level"); }
    @Override public double getBaseCost() { return plugin.getEnchants().getDouble("enchants.token-pouch.base-cost"); }
    @Override public double getCostMultiplier() { return plugin.getEnchants().getDouble("enchants.token-pouch.cost-multiplier"); }
    @Override public double getBaseValue() { return plugin.getEnchants().getDouble("enchants.token-pouch.base-value"); }
    @Override public double getMultiplier() { return plugin.getEnchants().getDouble("enchants.token-pouch.value-multiplier"); }

    @Override
    public void perform(Player player, ProtonMine mine, Location location, int level)
    {
    }

}
