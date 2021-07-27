/*
 * Copyright (c) 2021. Made by Kieraaaan for the plugin ProtonCore
 */

package kr.kieran.protonprisons.managers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.callbacks.EmptyCallback;
import kr.kieran.protonprisons.objects.ProtonSafe;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class SafesManager
{

    // PLUGIN
    private final ProtonPrisonsPlugin plugin;

    // SAFES
    private final Set<ProtonSafe> safes = ConcurrentHashMap.newKeySet();
    public Set<ProtonSafe> getSafes() { return Collections.unmodifiableSet(safes); }

    // MISC
    private static final Type LIST_TYPE = new TypeToken<List<String>>(){}.getType();
    private static final Gson GSON = new Gson();

    public SafesManager(ProtonPrisonsPlugin plugin)
    {
        this.plugin = plugin;
        this.setupSafesTable(() -> this.setupBalancesTable(this::registerSafes));
    }

    public ProtonSafe getSafeById(int id)
    {
        for (ProtonSafe safe : this.safes) if (safe.getId() == id) return safe;
        return null;
    }
    public ProtonSafe getSafeByName(String name)
    {
        for (ProtonSafe safe : this.safes) if (safe.getName().equals(name)) return safe;
        return null;
    }

    private void setupSafesTable(EmptyCallback callback)
    {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getDatabaseManager().getConnection())
            {
                // Metadata
                DatabaseMetaData metaData = connection.getMetaData();

                // Check if the safes table exists
                try (ResultSet resultSet = metaData.getTables(null, null, "prisons_safes", null))
                {
                    // Table already exists
                    if (resultSet.isBeforeFirst())
                    {
                        plugin.getLogger().log(Level.INFO, "Table prisons_safes already exists, continuing...");
                        callback.complete();
                        return;
                    }

                    // Table doesn't exist
                    plugin.getLogger().log(Level.INFO, "Table prisons_safes doesn't exist, creating it now...");
                    try (PreparedStatement statement = connection.prepareStatement("CREATE TABLE prisons_safes (safe_id int NOT NULL AUTO_INCREMENT, name varchar(16) NOT NULL, PRIMARY KEY (safe_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8;"))
                    {
                        statement.executeUpdate();
                        callback.complete();
                    }
                    catch (SQLException e)
                    {
                        plugin.getLogger().log(Level.SEVERE, "Failed to create the table prisons_safes: " + e.getMessage());
                    }
                }
            }
            catch (SQLException e)
            {
                plugin.getLogger().log(Level.SEVERE, "Failed to open a connection to the database: " + e.getMessage());
            }
        });
    }

    private void setupBalancesTable(EmptyCallback callback)
    {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getDatabaseManager().getConnection())
            {
                // Metadata
                DatabaseMetaData metaData = connection.getMetaData();

                // Check if the balances table exists
                try (ResultSet resultSet = metaData.getTables(null, null, "prisons_safes_balances", null))
                {
                    // Table already exists
                    if (resultSet.isBeforeFirst())
                    {
                        plugin.getLogger().log(Level.INFO, "Table prisons_safes_balances already exists, continuing...");
                        callback.complete();
                        return;
                    }

                    // Table doesn't exist
                    plugin.getLogger().log(Level.INFO, "Table prisons_safes_balances doesn't exist, creating it now...");
                    try (PreparedStatement statement = connection.prepareStatement("CREATE TABLE prisons_safes_balances (profile_id int NOT NULL, safe_id int NOT NULL, balance int NOT NULL, FOREIGN KEY (profile_id) REFERENCES prisons_profiles(profile_id), FOREIGN KEY (safe_id) REFERENCES prisons_safes(safe_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8;"))
                    {
                        statement.executeUpdate();
                        callback.complete();
                    }
                    catch (SQLException e)
                    {
                        plugin.getLogger().log(Level.SEVERE, "Failed to create the table prisons_safes_balances: " + e.getMessage());
                    }
                }
            }
            catch (SQLException e)
            {
                plugin.getLogger().log(Level.SEVERE, "Failed to open a connection to the database: " + e.getMessage());
            }
        });
    }

    private void registerSafes()
    {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getDatabaseManager().getConnection())
            {
                try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM prisons_safes;"))
                {
                    try (ResultSet resultSet = statement.executeQuery())
                    {
                        // Are there any safes in the database?
                        if (!resultSet.isBeforeFirst())
                        {
                            plugin.getLogger().log(Level.INFO, "No safes exist in the database.");
                            return;
                        }

                        // Loop over all safes in the database
                        plugin.getLogger().log(Level.INFO, "Registering safes from the database...");
                        while (resultSet.next())
                        {
                            // Args
                            int safeId = resultSet.getInt("safe_id");
                            String name = resultSet.getString("name");

                            // Get the safe contents
                            List<String> contents;
                            try (PreparedStatement contentsStatement = connection.prepareStatement("SELECT contents FROM prisons_safes_loot WHERE safe_id = ?;"))
                            {
                                contentsStatement.setInt(1, safeId);
                                try (ResultSet contentsResult = contentsStatement.executeQuery())
                                {
                                    if (!resultSet.next()) contents = new ArrayList<>();
                                    else contents = GSON.fromJson(contentsResult.getString("contents"), LIST_TYPE);
                                }
                            }

                            // Create the safe
                            ProtonSafe safe = new ProtonSafe(safeId, name, contents);

                            // Log & add the safe to the set
                            plugin.getLogger().log(Level.INFO, "Registering safe with id: " + safeId + ", name: " + name + ".");
                            this.safes.add(safe);
                        }

                        // Log
                        plugin.getLogger().log(Level.INFO, "Finished registering safes from the database.");
                    }
                }
                catch (SQLException e)
                {
                    plugin.getLogger().log(Level.SEVERE, "Failed to fetch safes from the database: " + e.getMessage());
                }
            }
            catch (SQLException e)
            {
                plugin.getLogger().log(Level.SEVERE, "Failed to open a connection to the database: " + e.getMessage());
            }
        });
    }

    public void disable() { this.safes.clear(); }

}
