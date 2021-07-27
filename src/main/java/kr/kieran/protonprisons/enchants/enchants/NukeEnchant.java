package kr.kieran.protonprisons.enchants.enchants;

import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.enchants.AbstractEnchant;
import kr.kieran.protonprisons.enchants.CustomEnchant;
import kr.kieran.protonprisons.enchants.Enchant;
import kr.kieran.protonprisons.objects.ProtonMine;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class NukeEnchant extends AbstractEnchant implements CustomEnchant
{

    private final ProtonPrisonsPlugin plugin;

    public NukeEnchant(ProtonPrisonsPlugin plugin)
    {
        super(Enchant.NUKE);
        this.plugin = plugin;
    }

    @Override public String getEnchantName() { return plugin.getEnchants().getString("enchants.nuke.name"); }
    @Override public String getLoreName() { return plugin.getEnchants().getString("enchants.nuke.lore"); }
    @Override public int getMaxLevel() { return plugin.getEnchants().getInt("enchants.nuke.max-level"); }
    @Override public double getBaseCost() { return plugin.getEnchants().getDouble("enchants.nuke.base-cost"); }
    @Override public double getCostMultiplier() { return plugin.getEnchants().getDouble("enchants.nuke.cost-multiplier"); }
    @Override public double getBaseValue() { return plugin.getEnchants().getDouble("enchants.nuke.base-value"); }
    @Override public double getMultiplier() { return plugin.getEnchants().getDouble("enchants.nuke.value-multiplier"); }

    @Override
    public void perform(Player player, ProtonMine mine, Location location, int level)
    {
        // Args

        World world = BukkitAdapter.adapt(location.getWorld());

        // Clear a layer of the mine using FastAsyncWorldEdit
        EditSession editSession = new EditSessionBuilder(world).fastmode(true).changeSet(false, null, 3).autoQueue(true).build();

        // Create the region & pattern
        BlockType air = BlockTypes.AIR;
        if (air == null) air = new BlockType("air", blockState -> null);
        BaseBlock baseBlock = new BaseBlock(air.getDefaultState());

        // Set the blocks
        try
        {
            int blocks = editSession.setBlocks((Region) new CuboidRegion(world, BlockVector3.at(mine.getMaxX(), mine.getMaxY(), mine.getMaxZ()), BlockVector3.at(mine.getMinX(), mine.getMinY(), mine.getMinZ())), baseBlock);
            editSession.flushQueue();
        }
        catch (MaxChangedBlocksException ignored)
        {
        }

        // Reset the mine
        mine.reset();
    }

}
