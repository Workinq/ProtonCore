package kr.kieran.protonprisons.objects;

import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockState;
import kr.kieran.protonprisons.utilities.SerializationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProtonMine
{

    private final int id;
    public int getId() { return id; }

    private final String name;
    public String getName() { return name; }

    private String teleportLocation;
    public String getSerializedTeleportLocation() { return teleportLocation; }
    public Location getTeleportLocation() { return SerializationUtil.deserializeLocation(teleportLocation); }
    public void setTeleportLocation(String teleportLocation) { this.teleportLocation = teleportLocation; }

    private final RandomPattern pattern = new RandomPattern();

    private List<BlockChance> blockChances;
    public List<BlockChance> getBlockChances() { return Collections.unmodifiableList(blockChances); }
    public void setBlockChances(List<BlockChance> blockChances) { this.blockChances = blockChances; }
    public void addBlockChance(BlockChance blockChance)
    {
        // Args
        BlockState blockState = blockChance.getBlock();
        BlockChance oldChance = null, newChance = null;

        // Loop over all block chances and see if there is one with the same id and data
        for (BlockChance chance : this.blockChances)
        {
            BlockState block = chance.getBlock();
            if (blockState.equals(block)) continue;

            // Assign the oldChance & newChance variable
            oldChance = chance;
            newChance = new BlockChance(block, blockChance.getChance() + chance.getChance());
            break;
        }

        // Add the block chance to the list
        this.blockChances.remove(oldChance);
        this.blockChances.add(newChance != null ? newChance : blockChance);
    }
    public void removeBlockChance(BlockChance blockChance) { this.blockChances.remove(blockChance); }

    private final String worldName;
    public String getWorldName() { return worldName; }

    private final World world;
    public World getWorld() { return world; }

    private final int minX, maxX;
    public int getMinX() { return minX; }
    public int getMaxX() { return maxX; }

    private final int minY, maxY;
    public int getMinY() { return minY; }
    public int getMaxY() { return maxY; }

    private final int minZ, maxZ;
    public int getMinZ() { return minZ; }
    public int getMaxZ() { return maxZ; }

    private final CuboidRegion cuboidRegion;
    public CuboidRegion getCuboidRegion() { return cuboidRegion; }

    public ProtonMine(int id, String name, String worldName, int minX, int maxX, int minY, int maxY, int minZ, int maxZ)
    {
        this(id, name, null, new ArrayList<>(), worldName, minX, maxX, minY, maxY, minZ, maxZ);
    }
    public ProtonMine(int id, String name, String teleportLocation, List<BlockChance> blockChances, String worldName, int minX, int maxX, int minY, int maxY, int minZ, int maxZ)
    {
        this.id = id;
        this.name = name;
        this.teleportLocation = teleportLocation;
        this.blockChances = blockChances;
        this.worldName = worldName;
        this.world = Bukkit.getWorld(worldName);
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;
        this.cuboidRegion = new CuboidRegion(BukkitAdapter.adapt(this.world), BlockVector3.at(this.minX, this.minY, this.minZ), BlockVector3.at(this.maxX, this.maxY, this.maxZ));
    }

    public boolean contains(Block block) { return this.contains(block.getLocation()); }
    public boolean contains(Player player) { return this.contains(player.getLocation()); }
    public boolean contains(Entity entity) { return this.contains(entity.getLocation()); }
    public boolean contains(Location location)
    {
        if (!location.getWorld().getName().equals(this.worldName)) return false;
        if (location.getBlockX() > this.maxX) return false;
        if (location.getBlockX() < this.minX) return false;
        if (location.getBlockY() > this.maxY) return false;
        if (location.getBlockY() < this.minY) return false;
        if (location.getBlockZ() > this.maxZ) return false;
        if (location.getBlockZ() < this.minZ) return false;
        return true;
    }

    public void reset()
    {
        // Teleport the players to the teleport location
        if (this.teleportLocation != null)
        {
            Set<Player> players = Bukkit.getOnlinePlayers().stream().filter(this::contains).collect(Collectors.toSet());
            players.forEach(player -> player.teleport(this.getTeleportLocation()));
        }

        // Reset the mine using FastAsyncWorldEdit
        EditSession editSession = new EditSessionBuilder(BukkitAdapter.adapt(this.world)).fastmode(true).changeSet(false, null, 3).autoQueue(true).build();
        RandomPattern pattern = new RandomPattern();
        this.blockChances.forEach(blockChance -> pattern.add(blockChance.getBlock(), blockChance.getChance()));

        // RandomFillPattern pattern = new RandomFillPattern(this.blockChances);

        // Set the blocks
        try
        {
            editSession.setBlocks((Region) this.cuboidRegion, pattern);
            editSession.flushQueue();
        }
        catch (MaxChangedBlocksException ignored)
        {
        }
    }

}
