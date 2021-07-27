package kr.kieran.protonprisons.managers;

import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.patterns.BlockChance;
import com.sk89q.worldedit.world.block.BlockState;
import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.callbacks.Callback;
import kr.kieran.protonprisons.callbacks.EmptyCallback;
import kr.kieran.protonprisons.objects.BlockChance;
import kr.kieran.protonprisons.objects.ProtonMine;
import kr.kieran.protonprisons.objects.ProtonPlayer;
import kr.kieran.protonprisons.utilities.SerializationUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class MineManager
{

    // PLUGIN
    private final ProtonPrisonsPlugin plugin;

    // MINES
    private final Set<ProtonMine> mines = ConcurrentHashMap.newKeySet();
    public Set<ProtonMine> getMines() { return Collections.unmodifiableSet(mines); }

    // TRACKER
    private final Map<ProtonPlayer, ProtonMine> lastMine = new HashMap<>();

    public MineManager(ProtonPrisonsPlugin plugin)
    {
        this.plugin = plugin;
        this.setupMinesTable(() -> this.setupCompositionsTable(this::registerMines));
    }

    public ProtonMine getMineById(int id)
    {
        for (ProtonMine mine : this.mines) if (mine.getId() == id) return mine;
        return null;
    }
    public ProtonMine getMineByName(String name)
    {
        for (ProtonMine mine : this.mines) if (mine.getName().equals(name)) return mine;
        return null;
    }

    public void createMine(String name, String worldName, int minX, int maxX, int minY, int maxY, int minZ, int maxZ, Callback<ProtonMine> callback)
    {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getDatabaseManager().getConnection())
            {
                try (PreparedStatement statement = connection.prepareStatement("INSERT INTO prisons_mines (name, world, min_x, max_x, min_y, max_y, min_z, max_z) VALUES (?, ?, ?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS))
                {
                    statement.setString(1, name);
                    statement.setString(2, worldName);
                    statement.setInt(3, minX);
                    statement.setInt(4, maxX);
                    statement.setInt(5, minY);
                    statement.setInt(6, maxY);
                    statement.setInt(7, minZ);
                    statement.setInt(8, maxZ);
                    statement.executeUpdate();
                    try (ResultSet generatedKeys = statement.getGeneratedKeys())
                    {
                        // Check if for some reason no keys were returned
                        if (!generatedKeys.next())
                        {
                            callback.complete(null);
                            return;
                        }

                        // Create an instance of the ProtonMine object
                        ProtonMine mine = new ProtonMine(generatedKeys.getInt(1), name, worldName, minX, maxX, minY, maxY, minZ, maxZ);

                        // Add the mine to the list of mines & complete the callback
                        this.mines.add(mine);
                        callback.complete(mine);
                    }
                }
                catch (SQLException e)
                {
                    plugin.getLogger().log(Level.SEVERE, "Failed to insert a new mine '" + name + "' into the database: " + e.getMessage());
                    callback.complete(null);
                }
            }
            catch (SQLException e)
            {
                plugin.getLogger().log(Level.SEVERE, "Failed to open a connection to the database: " + e.getMessage());
                callback.complete(null);
            }
        });
    }

    public void setMineComposition(ProtonMine mine, Callback<ProtonMine> callback)
    {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getDatabaseManager().getConnection())
            {
                // Delete the block chances from the database
                try (PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM prisons_compositions WHERE mine_id = ?;"))
                {
                    deleteStatement.setInt(1, mine.getId());
                    deleteStatement.executeUpdate();
                }

                // Loop over block chances & add them to the database
                for (BlockChance blockChance : mine.getBlockChances())
                {
                    BlockState baseBlock = blockChance.getBlock();
                    ItemStack item = new ItemStack(Material.getMaterial(baseBlock, 1, (short) baseBlock.getData()));
                    String serializedItem = SerializationUtil.serializeItem(item);
                    try (PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO prisons_compositions (mine_id, item, percent) VALUES (?, ?, ?);"))
                    {
                        insertStatement.setInt(1, mine.getId());
                        insertStatement.setString(2, serializedItem);
                        insertStatement.setDouble(3, blockChance.getChance());
                        insertStatement.executeUpdate();
                    }
                }

                // Complete the callback
                callback.complete(mine);
            }
            catch (SQLException e)
            {
                plugin.getLogger().log(Level.SEVERE, "Failed to open a connection to the database: " + e.getMessage());
                callback.complete(null);
            }
        });
    }

    private BlockChance getBlockChanceFromId(List<BlockChance> blockChances, int id, int data)
    {
        for (BlockChance blockChance : blockChances)
        {
            BaseBlock baseBlock = blockChance.getBlock();
            if (baseBlock.getId() == id && baseBlock.getData() == data) return blockChance;
        }
        return null;
    }

    public void setMineTeleport(ProtonMine mine, EmptyCallback callback)
    {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getDatabaseManager().getConnection())
            {
                // Delete the block chances from the database
                try (PreparedStatement statement = connection.prepareStatement("UPDATE prisons_mines SET teleport_location = ? WHERE mine_id = ?;"))
                {
                    statement.setString(1, mine.getSerializedTeleportLocation());
                    statement.setInt(2, mine.getId());
                    statement.executeUpdate();

                    // Complete the callback
                    callback.complete();
                }
            }
            catch (SQLException e)
            {
                plugin.getLogger().log(Level.SEVERE, "Failed to open a connection to the database: " + e.getMessage());
            }
        });
    }

    private ProtonMine getMineFromLocation(Location location)
    {
        Optional<ProtonMine> optional = plugin.getMineManager().getMines().stream().filter(mine -> mine.contains(location)).findFirst();
        return optional.orElse(null);
    }

    public Map<ProtonPlayer, ProtonMine> getLastMine() { return lastMine; }

    public ProtonMine getLastMine(Location location, ProtonPlayer protonPlayer)
    {
        // Get the cached mine from the map
        ProtonMine mine = this.lastMine.get(protonPlayer);

        // If there is no mined cached or the mine which is cached doesn't contain the player's location then find a new mine
        if (mine == null || !mine.contains(location))
        {
            // Get a mine from the location
            mine = this.getMineFromLocation(location);

            // A mine still doesn't exist with the given location, remove it from the cache and return
            if (mine == null)
            {
                this.lastMine.remove(protonPlayer);
                return null;
            }

            // Update the cached mine
            this.lastMine.put(protonPlayer, mine);
        }

        return mine;
    }

    // -------------- //
    // DATABASE STUFF //
    // -------------- //
    private void setupMinesTable(EmptyCallback callback)
    {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getDatabaseManager().getConnection())
            {
                // Metadata
                DatabaseMetaData metaData = connection.getMetaData();

                // Check if the mines table exists
                try (ResultSet resultSet = metaData.getTables(null, null, "prisons_mines", null))
                {
                    // Table already exists
                    if (resultSet.isBeforeFirst())
                    {
                        plugin.getLogger().log(Level.INFO, "Table prisons_mines already exists, continuing...");
                        callback.complete();
                        return;
                    }

                    // Table doesn't exist
                    plugin.getLogger().log(Level.INFO, "Table prisons_mines doesn't exist, creating it now...");
                    try (PreparedStatement statement = connection.prepareStatement("CREATE TABLE prisons_mines (mine_id int NOT NULL AUTO_INCREMENT, name varchar(16) NOT NULL, teleport_location TEXT, world varchar(16) NOT NULL, min_x int NOT NULL, max_x int NOT NULL, min_y int NOT NULL, max_y int NOT NULL, min_z int NOT NULL, max_z int NOT NULL, PRIMARY KEY (mine_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8;"))
                    {
                        statement.executeUpdate();
                        callback.complete();
                    }
                    catch (SQLException e)
                    {
                        plugin.getLogger().log(Level.SEVERE, "Failed to create the table prisons_mines: " + e.getMessage());
                    }
                }
            }
            catch (SQLException e)
            {
                plugin.getLogger().log(Level.SEVERE, "Failed to open a connection to the database: " + e.getMessage());
            }
        });
    }

    private void setupCompositionsTable(EmptyCallback callback)
    {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getDatabaseManager().getConnection())
            {
                // Metadata
                DatabaseMetaData metaData = connection.getMetaData();

                // Check if the mines table exists
                try (ResultSet resultSet = metaData.getTables(null, null, "prisons_compositions", null))
                {
                    // Table already exists
                    if (resultSet.isBeforeFirst())
                    {
                        plugin.getLogger().log(Level.INFO, "Table prisons_compositions already exists, continuing...");
                        callback.complete();
                        return;
                    }

                    // Table doesn't exist
                    plugin.getLogger().log(Level.INFO, "Table prisons_compositions doesn't exist, creating it now...");
                    try (PreparedStatement statement = connection.prepareStatement("CREATE TABLE prisons_compositions (mine_id int NOT NULL, item VARCHAR(255) NOT NULL, percent double NOT NULL, PRIMARY KEY (mine_id, item), FOREIGN KEY (mine_id) REFERENCES prisons_mines(mine_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8;"))
                    {
                        statement.executeUpdate();
                        callback.complete();
                    }
                    catch (SQLException e)
                    {
                        plugin.getLogger().log(Level.SEVERE, "Failed to create the table prisons_compositions: " + e.getMessage());
                    }
                }
            }
            catch (SQLException e)
            {
                plugin.getLogger().log(Level.SEVERE, "Failed to open a connection to the database: " + e.getMessage());
            }
        });
    }

    private void registerMines()
    {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getDatabaseManager().getConnection())
            {
                try (PreparedStatement selectStatement = connection.prepareStatement("SELECT * FROM prisons_mines;"))
                {
                    try (ResultSet resultSet = selectStatement.executeQuery())
                    {
                        // Are there any mines in the database?
                        if (!resultSet.isBeforeFirst())
                        {
                            plugin.getLogger().log(Level.INFO, "No mines exist in the database.");
                            return;
                        }

                        // Loop over all mines in the database
                        plugin.getLogger().log(Level.INFO, "Registering mines from the database...");
                        while (resultSet.next())
                        {
                            // Args
                            int mineId = resultSet.getInt("mine_id");
                            String mineName = resultSet.getString("name");
                            String teleportLocation = resultSet.getString("teleport_location");
                            String world = resultSet.getString("world");
                            int minX = resultSet.getInt("min_x"), maxX = resultSet.getInt("max_x");
                            int minY = resultSet.getInt("min_y"), maxY = resultSet.getInt("max_y");
                            int minZ = resultSet.getInt("min_z"), maxZ = resultSet.getInt("max_z");

                            // Get the block chances from the database
                            List<BlockChance> blockChances = new ArrayList<>();
                            try (PreparedStatement compositionStatement = connection.prepareStatement("SELECT * FROM prisons_compositions WHERE mine_id = ?;"))
                            {
                                compositionStatement.setInt(1, mineId);
                                try (ResultSet compositionSet = compositionStatement.executeQuery())
                                {
                                    if (compositionSet.isBeforeFirst())
                                    {
                                        while (compositionSet.next())
                                        {
                                            String serializedItem = compositionSet.getString("item");
                                            double percent = compositionSet.getDouble("percent");
                                            ItemStack item = SerializationUtil.deserializeItem(serializedItem);
                                            blockChances.add(new BlockChance(new BaseBlock(item.getTypeId(), item.getDurability()), percent));
                                        }
                                    }
                                }
                            }
                            catch (SQLException e)
                            {
                                plugin.getLogger().log(Level.SEVERE, "Failed to retrieve mine composition for '" + mineName + "' from the database: " + e.getMessage());
                                continue;
                            }

                            // Create the mine
                            ProtonMine mine = new ProtonMine(mineId, mineName, teleportLocation, blockChances, world, minX, maxX, minY, maxY, minZ, maxZ);

                            // Log & add the mine to the set
                            plugin.getLogger().log(Level.INFO, "Registering mine with id: " + mineId + ", name: " + mineName + ".");
                            this.mines.add(mine);
                        }

                        // Log
                        plugin.getLogger().log(Level.INFO, "Finished registering mines from the database.");
                    }
                }
                catch (SQLException e)
                {
                    plugin.getLogger().log(Level.SEVERE, "Failed to fetch mines from the database: " + e.getMessage());
                }
            }
            catch (SQLException e)
            {
                plugin.getLogger().log(Level.SEVERE, "Failed to open a connection to the database: " + e.getMessage());
            }
        });
    }

    public void disable() { this.mines.clear(); }

}
