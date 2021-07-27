package kr.kieran.protonprisons.enchants.enchants;

import com.boydti.fawe.object.RegionWrapper;
import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.factory.SphereRegionFactory;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockTypes;
import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.enchants.AbstractEnchant;
import kr.kieran.protonprisons.enchants.CustomEnchant;
import kr.kieran.protonprisons.enchants.Enchant;
import kr.kieran.protonprisons.objects.ProtonMine;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class DragonsFistEnchant extends AbstractEnchant implements CustomEnchant
{

    private final ProtonPrisonsPlugin plugin;

    public DragonsFistEnchant(ProtonPrisonsPlugin plugin)
    {
        super(Enchant.DRAGONS_FIST);
        this.plugin = plugin;
    }

    @Override public String getEnchantName() { return plugin.getEnchants().getString("enchants.dragons-fist.name"); }
    @Override public String getLoreName() { return plugin.getEnchants().getString("enchants.dragons-fist.lore"); }
    @Override public int getMaxLevel() { return plugin.getEnchants().getInt("enchants.dragons-fist.max-level"); }
    @Override public double getBaseCost() { return plugin.getEnchants().getDouble("enchants.dragons-fist.base-cost"); }
    @Override public double getCostMultiplier() { return plugin.getEnchants().getDouble("enchants.dragons-fist.cost-multiplier"); }
    @Override public double getBaseValue() { return plugin.getEnchants().getDouble("enchants.dragons-fist.base-value"); }
    @Override public double getMultiplier() { return plugin.getEnchants().getDouble("enchants.dragons-fist.value-multiplier"); }

    @Override
    public void perform(Player player, ProtonMine mine, Location location, int level)
    {
        // Args
        World world = BukkitAdapter.adapt(location.getWorld());
        // TODO: Figure out how to calculate the radius based on the level of the enchant
        int radius = (int) Math.max(1, 0.1 * level);
        Region region = new SphereRegionFactory().createCenteredAt(BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()), radius);

        // Clear a layer of the mine using FastAsyncWorldEdit
        try (
                EditSession editSession = new EditSessionBuilder(world)
                        .fastmode(true)
                        .changeSet(false, null, 3)
                        .autoQueue(true)
                        .allowedRegions(new RegionWrapper(BlockVector3.at(mine.getMaxX(), mine.getMaxY(), mine.getMaxZ()), BlockVector3.at(mine.getMinX(), mine.getMinY(), mine.getMinZ())))
                        .build()
        )
        {
            // Set the blocks
            editSession.setBlocks(region, BlockTypes.AIR);
//            int blocks = editSession.makeSphere(BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()), AbstractEnchant.AIR, radius, true);
            editSession.flushQueue();
        }
        catch (MaxChangedBlocksException ignored)
        {
        }
    }

}
