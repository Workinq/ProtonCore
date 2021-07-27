package kr.kieran.protonprisons.enchants.enchants;

import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Countable;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockState;
import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.enchants.AbstractEnchant;
import kr.kieran.protonprisons.enchants.CustomEnchant;
import kr.kieran.protonprisons.enchants.Enchant;
import kr.kieran.protonprisons.objects.ProtonMine;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class JackhammerEnchant extends AbstractEnchant implements CustomEnchant
{

    private final ProtonPrisonsPlugin plugin;

    public JackhammerEnchant(ProtonPrisonsPlugin plugin)
    {
        super(Enchant.JACKHAMMER);
        this.plugin = plugin;
    }

    @Override public String getEnchantName() { return plugin.getEnchants().getString("enchants.jackhammer.name"); }
    @Override public String getLoreName() { return plugin.getEnchants().getString("enchants.jackhammer.lore"); }
    @Override public int getMaxLevel() { return plugin.getEnchants().getInt("enchants.jackhammer.max-level"); }
    @Override public double getBaseCost() { return plugin.getEnchants().getDouble("enchants.jackhammer.base-cost"); }
    @Override public double getCostMultiplier() { return plugin.getEnchants().getDouble("enchants.jackhammer.cost-multiplier"); }
    @Override public double getBaseValue() { return plugin.getEnchants().getDouble("enchants.jackhammer.base-value"); }
    @Override public double getMultiplier() { return plugin.getEnchants().getDouble("enchants.jackhammer.value-multiplier"); }

    @Override
    public void perform(Player player, ProtonMine mine, Location location, int level)
    {
        // Args
        int y = location.getBlockY();
        World world = BukkitAdapter.adapt(location.getWorld());

        // Set the blocks
        try (EditSession editSession = new EditSessionBuilder(world).fastmode(true).changeSet(false, null, 3).autoQueue(true).build())
        {
            // Create the region & pattern
            CuboidRegion region = new CuboidRegion(world, BlockVector3.at(mine.getMaxX(), y, mine.getMaxZ()), BlockVector3.at(mine.getMinX(), y, mine.getMinZ()));

            // Handle blocks
            this.handleBlocks(player, editSession.getBlockDistributionWithData(region));

            int blocks = editSession.setBlocks((Region) region, AbstractEnchant.AIR);
            editSession.flushQueue();
        }
        catch (MaxChangedBlocksException ignored)
        {
        }
    }

    private void handleBlocks(Player player, List<Countable<BlockState>> countables)
    {
        for (Countable<BlockState> countable : countables)
        {
            BlockState baseBlock = countable.getID();
            int amount = countable.getAmount();

            // ItemStack item = new ItemStack(Material.getMaterial(baseBlock.getMaterial(), amount, (short) baseBlock.getState(PropertyKey));
        }
    }

}
