package kr.kieran.protonprisons.managers;

import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.callbacks.EmptyCallback;
import kr.kieran.protonprisons.objects.ProtonCurrency;
import kr.kieran.protonprisons.objects.ProtonPlayer;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class ProfileManager
{

    private final ProtonPrisonsPlugin plugin;
    private final Set<ProtonPlayer> profiles = ConcurrentHashMap.newKeySet();

    public ProfileManager(ProtonPrisonsPlugin plugin)
    {
        this.plugin = plugin;
        this.setupProfilesTable(this::setupPrestigesTable);
    }

    public void loadProfile(UUID uniqueId)
    {
        try (Connection connection = plugin.getDatabaseManager().getConnection())
        {
            try (PreparedStatement selectStatement = connection.prepareStatement("SELECT * FROM prisons_profiles WHERE uuid = ?;"))
            {
                selectStatement.setString(1, uniqueId.toString());
                try (ResultSet resultSet = selectStatement.executeQuery())
                {
                    // The player doesn't exist in the database
                    if (!resultSet.next())
                    {
                        try (PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO prisons_profiles (uuid) VALUES (?);", Statement.RETURN_GENERATED_KEYS))
                        {
                            insertStatement.setString(1, uniqueId.toString());
                            insertStatement.executeUpdate();
                            try (ResultSet generatedKeys = insertStatement.getGeneratedKeys())
                            {
                                // Check if for some reason no keys were returned
                                if (!generatedKeys.next()) return;

                                // Create an instance of the ProtonPlayer object
                                ProtonPlayer player = new ProtonPlayer(generatedKeys.getInt(1), uniqueId, 0, 0, new Timestamp(System.currentTimeMillis()), new HashMap<>());

                                // Add the player to the players set
                                profiles.add(player);
                            }
                        }
                    }
                    else
                    {
                        // Variables from profiles table
                        int playerId = resultSet.getInt("profile_id");
                        UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                        int prestige = resultSet.getInt("prestige");
                        int blocksMined = resultSet.getInt("blocks_mined");
                        Timestamp createdAt = resultSet.getTimestamp("created_at");

                        // Get currencies which the player owns
                        Map<ProtonCurrency, Double> currencies = new HashMap<>();
                        try (PreparedStatement balancesStatement = connection.prepareStatement("SELECT prisons_balances.currency_id, balance FROM prisons_balances INNER JOIN prisons_currencies ON prisons_balances.currency_id = prisons_currencies.currency_id WHERE profile_id = ?;"))
                        {
                            balancesStatement.setInt(1, playerId);
                            try (ResultSet currenciesResult = balancesStatement.executeQuery())
                            {
                                // There are balances to add
                                if (currenciesResult.isBeforeFirst())
                                {
                                    while (currenciesResult.next())
                                    {
                                        currencies.put(plugin.getCurrencyManager().getCurrencyById(currenciesResult.getInt("currency_id")), currenciesResult.getDouble("balance"));
                                    }
                                }
                            }
                        }

                        // Create an instance of the ProtonPlayer object
                        ProtonPlayer player = new ProtonPlayer(playerId, uuid, prestige, blocksMined, createdAt, currencies);

                        // Add the player to the players set
                        profiles.add(player);
                    }
                }
            }
            catch (SQLException e)
            {
                plugin.getLogger().log(Level.SEVERE, "Failed to fetch the profile for '" + uniqueId.toString() + "' from the database: " + e.getMessage());
            }
        }
        catch (SQLException e)
        {
            plugin.getLogger().log(Level.SEVERE, "Failed to open a connection to the database: " + e.getMessage());
        }
    }

    public void removeProfile(ProtonPlayer player)
    {
        this.saveProfile(player, () -> profiles.remove(player));
    }

    public void saveProfile(ProtonPlayer player, EmptyCallback callback)
    {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            this.updatePrestige(player);
            this.updateBlocksMined(player);
            this.updateCurrencies(player);
            callback.complete();
        });
    }

    public void updatePrestige(ProtonPlayer player)
    {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getDatabaseManager().getConnection())
            {
                try (PreparedStatement statement = connection.prepareStatement("UPDATE prisons_profiles SET prestige = ? WHERE profile_id = ?;"))
                {
                    statement.setInt(1, player.getPrestige());
                    statement.setInt(2, player.getId());
                    statement.executeUpdate();
                }
                catch (SQLException e)
                {
                    plugin.getLogger().log(Level.SEVERE, "Failed to update prestige for '" + player.getUniqueId().toString() + "': " + e.getMessage());
                }
            }
            catch (SQLException e)
            {
                plugin.getLogger().log(Level.SEVERE, "Failed to open a connection to the database: " + e.getMessage());
            }
        });
    }

    public void updateBlocksMined(ProtonPlayer player)
    {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getDatabaseManager().getConnection())
            {
                try (PreparedStatement statement = connection.prepareStatement("UPDATE prisons_profiles SET blocks_mined = ? WHERE profile_id = ?;"))
                {
                    statement.setInt(1, player.getBlocksMined());
                    statement.setInt(2, player.getId());
                    statement.executeUpdate();
                }
                catch (SQLException e)
                {
                    plugin.getLogger().log(Level.SEVERE, "Failed to update blocks_mined for '" + player.getUniqueId().toString() + "': " + e.getMessage());
                }
            }
            catch (SQLException e)
            {
                plugin.getLogger().log(Level.SEVERE, "Failed to open a connection to the database: " + e.getMessage());
            }
        });
    }

    public void updateCurrencies(ProtonPlayer player)
    {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getDatabaseManager().getConnection())
            {
                // Loop over all the player's currencies
                for (Map.Entry<ProtonCurrency, Double> entry : player.getCurrencies().entrySet())
                {
                    // Args
                    ProtonCurrency currency = entry.getKey();
                    double balance = entry.getValue();

                    try (PreparedStatement selectStatement = connection.prepareStatement("SELECT * FROM prisons_balances WHERE profile_id = ? AND currency_id = ?;"))
                    {
                        selectStatement.setInt(1, player.getId());
                        selectStatement.setInt(2, currency.getId());
                        try (ResultSet resultSet = selectStatement.executeQuery())
                        {
                            // The player's balance for this currency isn't stored AND the player actually has a balance
                            if (!resultSet.next() && balance != 0.0d)
                            {
                                try (PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO prisons_balances (profile_id, currency_id, balance) VALUES (?, ?, ?);"))
                                {
                                    insertStatement.setInt(1, player.getId());
                                    insertStatement.setInt(2, currency.getId());
                                    insertStatement.setDouble(3, balance);
                                    insertStatement.executeUpdate();
                                }
                            }
                            // The player has a balance stored in the database
                            else
                            {
                                if (balance == 0.0d)
                                {
                                    // Delete the player's balance from the database as there's no point storing it
                                    try (PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM prisons_balances WHERE profile_id = ? AND currency_id = ?;"))
                                    {
                                        deleteStatement.setInt(1, player.getId());
                                        deleteStatement.setInt(2, currency.getId());
                                        deleteStatement.executeUpdate();
                                    }
                                }
                                else
                                {
                                    // Update the player's balance
                                    try (PreparedStatement updateStatement = connection.prepareStatement("UPDATE prisons_balances SET balance = ? WHERE profile_id = ? AND currency_id = ?;"))
                                    {
                                        updateStatement.setDouble(1, balance);
                                        updateStatement.setInt(2, player.getId());
                                        updateStatement.setInt(3, currency.getId());
                                        updateStatement.executeUpdate();
                                    }
                                }
                            }
                        }
                    }
                    catch (SQLException e)
                    {
                        plugin.getLogger().log(Level.SEVERE, "Failed to fetch balance for '" + player.getUniqueId().toString() + "' from database: " + e.getMessage());
                    }
                }
            }
            catch (SQLException e)
            {
                plugin.getLogger().log(Level.SEVERE, "Failed to open a connection to the database: " + e.getMessage());
            }
        });
    }

    public ProtonPlayer getById(int id)
    {
        for (ProtonPlayer player : this.profiles) if (player.getId() == id) return player;
        return null;
    }
    public ProtonPlayer getByPlayer(Player player) { return this.getByUniqueId(player.getUniqueId()); }
    public ProtonPlayer getByUniqueId(UUID uniqueId)
    {
        for (ProtonPlayer player : this.profiles) if (player.getUniqueId().equals(uniqueId)) return player;
        return null;
    }
    public boolean isProfileLoaded(Player player) { return this.getByPlayer(player) != null; }

    private void setupProfilesTable(EmptyCallback callback)
    {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getDatabaseManager().getConnection())
            {
                // Metadata
                DatabaseMetaData metaData = connection.getMetaData();

                // Check if the profiles table exists
                try (ResultSet resultSet = metaData.getTables(null, null, "prisons_profiles", null))
                {
                    // Table already exists
                    if (resultSet.isBeforeFirst())
                    {
                        plugin.getLogger().log(Level.INFO, "Table prisons_profiles already exists, continuing...");
                        callback.complete();
                        return;
                    }

                    // Table doesn't exist
                    plugin.getLogger().log(Level.INFO, "Table prisons_profiles doesn't exist, creating it now...");
                    try (PreparedStatement statement = connection.prepareStatement("CREATE TABLE prisons_profiles (profile_id int NOT NULL AUTO_INCREMENT, uuid VARCHAR(36) NOT NULL, prestige int NOT NULL DEFAULT '0', blocks_mined bigint NOT NULL DEFAULT '0', created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (profile_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8;"))
                    {
                        statement.executeUpdate();
                        callback.complete();
                    }
                    catch (SQLException e)
                    {
                        plugin.getLogger().log(Level.SEVERE, "Failed to create the table prisons_profiles: " + e.getMessage());
                    }
                }
            }
            catch (SQLException e)
            {
                plugin.getLogger().log(Level.SEVERE, "Failed to open a connection to the database: " + e.getMessage());
            }
        });
    }

    private void setupPrestigesTable()
    {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getDatabaseManager().getConnection())
            {
                // Metadata
                DatabaseMetaData metaData = connection.getMetaData();

                // Check if the prestiges table exists
                try (ResultSet resultSet = metaData.getTables(null, null, "prisons_prestiges", null))
                {
                    // Table already exists
                    if (resultSet.isBeforeFirst())
                    {
                        plugin.getLogger().log(Level.INFO, "Table prisons_prestiges already exists, continuing...");
                        return;
                    }

                    // Table doesn't exist
                    plugin.getLogger().log(Level.INFO, "Table prisons_prestiges doesn't exist, creating it now...");
                    try (PreparedStatement statement = connection.prepareStatement("CREATE TABLE prisons_prestiges (profile_id int NOT NULL, prestige int NOT NULL, prestige_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (profile_id) REFERENCES prisons_profiles(profile_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8;"))
                    {
                        statement.executeUpdate();
                    }
                    catch (SQLException e)
                    {
                        plugin.getLogger().log(Level.SEVERE, "Failed to create the table prisons_prestiges: " + e.getMessage());
                    }
                }
            }
            catch (SQLException e)
            {
                plugin.getLogger().log(Level.SEVERE, "Failed to open a connection to the database: " + e.getMessage());
            }
        });
    }

    public void disable() { this.profiles.clear(); }

}
