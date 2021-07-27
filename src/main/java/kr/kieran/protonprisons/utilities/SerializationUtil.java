package kr.kieran.protonprisons.utilities;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class SerializationUtil
{

    public static String serializeLocation(Location location)
    {
        return "" + location.getWorld().getName() + ":" + location.getX() + ":" + location.getY() + ":" + location.getZ() + ":" + location.getYaw() + ":" + location.getPitch() + "";
    }

    public static String serializeItem(ItemStack item)
    {
        return "" + item.getType().name() + ":" + item.getDurability() + "";
    }

    public static String serializeChunk(Chunk chunk)
    {
        return "" + chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ() + "";
    }

    public static Location deserializeLocation(String serialized)
    {
        String[] split = serialized.split(":");
        return new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
    }

    public static ItemStack deserializeItem(String serialized)
    {
        String[] split = serialized.split(":");
        return new ItemStack(Material.getMaterial(split[0]), 1, Short.parseShort(split[1]));
    }

    public static Chunk deserializeChunk(String serialized)
    {
        String[] split = serialized.split(":");
        return Bukkit.getWorld(split[0]).getChunkAt(Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    }

}
