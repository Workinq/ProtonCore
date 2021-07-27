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
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.List;

public class LaserEnchant extends AbstractEnchant implements CustomEnchant
{

    private final ProtonPrisonsPlugin plugin;
    private static final BlockFace[] AXIS = { BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST };

    public LaserEnchant(ProtonPrisonsPlugin plugin)
    {
        super(Enchant.LASER);
        this.plugin = plugin;
    }

    @Override public String getEnchantName() { return plugin.getEnchants().getString("enchants.laser.name"); }
    @Override public String getLoreName() { return plugin.getEnchants().getString("enchants.laser.lore"); }
    @Override public int getMaxLevel() { return plugin.getEnchants().getInt("enchants.laser.max-level"); }
    @Override public double getBaseCost() { return plugin.getEnchants().getDouble("enchants.laser.base-cost"); }
    @Override public double getCostMultiplier() { return plugin.getEnchants().getDouble("enchants.laser.cost-multiplier"); }
    @Override public double getBaseValue() { return plugin.getEnchants().getDouble("enchants.laser.base-value"); }
    @Override public double getMultiplier() { return plugin.getEnchants().getDouble("enchants.laser.value-multiplier"); }

    @Override
    public void perform(Player player, ProtonMine mine, Location location, int level)
    {
        // Get the direction the player is facing
        BlockFace blockFace = AXIS[Math.round(player.getLocation().getYaw() / 90f) & 0x3];
        int startX = location.getBlockX(), startZ = location.getBlockZ();
        World world = BukkitAdapter.adapt(location.getWorld());

        // Get the end coords
        Location blockLocation = location.clone();
        while (mine.contains(blockLocation))
        {
            blockLocation = blockLocation.getBlock().getRelative(blockFace).getLocation();
        }
        int endX = blockLocation.getBlockX(), endZ = blockLocation.getBlockZ();

        try (EditSession editSession = new EditSessionBuilder(world).fastmode(true).changeSet(false, null, 3).autoQueue(true).build())
        {
            // Region
            CuboidRegion region = new CuboidRegion(world, BlockVector3.at(startX, location.getBlockY(), startZ), BlockVector3.at(endX, location.getBlockY(), endZ));

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

            // ItemStack item = new ItemStack(Material.getMaterial(baseBlock.getType()), amount, (short) baseBlock.getData());
        }
    }

}
