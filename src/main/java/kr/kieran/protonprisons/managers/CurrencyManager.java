package kr.kieran.protonprisons.managers;

import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.callbacks.Callback;
import kr.kieran.protonprisons.callbacks.EmptyCallback;
import kr.kieran.protonprisons.objects.ProtonCurrency;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class CurrencyManager
{

    // PLUGIN
    private final ProtonPrisonsPlugin plugin;

    // CURRENCIES
    private final Set<ProtonCurrency> currencies = ConcurrentHashMap.newKeySet();
    public Set<ProtonCurrency> getCurrencies() { return Collections.unmodifiableSet(currencies); }

    public CurrencyManager(ProtonPrisonsPlugin plugin)
    {
        this.plugin = plugin;
        this.setupCurrenciesTable(() -> this.setupBalancesTable(this::registerCurrencies));
    }

    public ProtonCurrency getCurrencyById(int id)
    {
        for (ProtonCurrency currency : this.currencies) if (currency.getId() == id) return currency;
        return null;
    }
    public ProtonCurrency getCurrencyByName(String name)
    {
        for (ProtonCurrency currency : this.currencies) if (currency.getName().equals(name)) return currency;
        return null;
    }

    public void createCurrency(String name, String symbol, Callback<ProtonCurrency> callback)
    {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getDatabaseManager().getConnection())
            {
                try (PreparedStatement statement = connection.prepareStatement("INSERT INTO prisons_currencies (name, symbol) VALUES (?, ?);", Statement.RETURN_GENERATED_KEYS))
                {
                    statement.setString(1, name);
                    statement.setString(2, symbol);
                    statement.executeUpdate();
                    try (ResultSet generatedKeys = statement.getGeneratedKeys())
                    {
                        // Check if for some reason no keys were returned
                        if (!generatedKeys.next())
                        {
                            callback.complete(null);
                            return;
                        }

                        // Create an instance of the ProtonCurrency object
                        ProtonCurrency currency = new ProtonCurrency(generatedKeys.getInt(1), name, symbol);

                        // Add the currency to the list of currencies & complete the callback
                        this.currencies.add(currency);
                        callback.complete(currency);
                    }
                }
                catch (SQLException e)
                {
                    plugin.getLogger().log(Level.SEVERE, "Failed to insert a new currency '" + name + "' into the database: " + e.getMessage());
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

    public void deleteCurrency(ProtonCurrency currency, Callback<ProtonCurrency> callback)
    {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getDatabaseManager().getConnection())
            {
                // Delete all rows referencing the currency in the prisons_balances table
                try (PreparedStatement statement = connection.prepareStatement("DELETE FROM prisons_balances WHERE currency_id = ?;"))
                {
                    statement.setInt(1, currency.getId());
                    statement.executeUpdate();
                }

                // Finally, delete the currency from the prisons_currencies table
                try (PreparedStatement statement = connection.prepareStatement("DELETE FROM prisons_currencies WHERE currency_id = ?;"))
                {
                    statement.setInt(1, currency.getId());
                    statement.executeUpdate();

                    // Remove the currency from the list of currencies & complete the callback
                    currencies.remove(currency);
                    callback.complete(currency);
                }
            }
            catch (SQLException e)
            {
                plugin.getLogger().log(Level.SEVERE, "Failed to open a connection to the database: " + e.getMessage());
                callback.complete(null);
            }
        });
    }

    private void setupCurrenciesTable(EmptyCallback callback)
    {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getDatabaseManager().getConnection())
            {
                // Metadata
                DatabaseMetaData metaData = connection.getMetaData();

                // Check if the currencies table exists
                try (ResultSet resultSet = metaData.getTables(null, null, "prisons_currencies", null))
                {
                    // Table already exists
                    if (resultSet.isBeforeFirst())
                    {
                        plugin.getLogger().log(Level.INFO, "Table prisons_currencies already exists, continuing...");
                        callback.complete();
                        return;
                    }

                    // Table doesn't exist
                    plugin.getLogger().log(Level.INFO, "Table prisons_currencies doesn't exist, creating it now...");
                    try (PreparedStatement statement = connection.prepareStatement("CREATE TABLE prisons_currencies (currency_id int NOT NULL AUTO_INCREMENT, name varchar(16) NOT NULL, symbol VARCHAR(1) NOT NULL, PRIMARY KEY (currency_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8;"))
                    {
                        statement.executeUpdate();
                        callback.complete();
                    }
                    catch (SQLException e)
                    {
                        plugin.getLogger().log(Level.SEVERE, "Failed to create the table prisons_currencies: " + e.getMessage());
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
                try (ResultSet resultSet = metaData.getTables(null, null, "prisons_balances", null))
                {
                    // Table already exists
                    if (resultSet.isBeforeFirst())
                    {
                        plugin.getLogger().log(Level.INFO, "Table prisons_balances already exists, continuing...");
                        callback.complete();
                        return;
                    }

                    // Table doesn't exist
                    plugin.getLogger().log(Level.INFO, "Table prisons_balances doesn't exist, creating it now...");
                    try (PreparedStatement statement = connection.prepareStatement("CREATE TABLE prisons_balances (profile_id int NOT NULL, currency_id int NOT NULL, balance double NOT NULL, FOREIGN KEY (profile_id) REFERENCES prisons_profiles(profile_id), FOREIGN KEY (currency_id) REFERENCES prisons_currencies(currency_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8;"))
                    {
                        statement.executeUpdate();
                        callback.complete();
                    }
                    catch (SQLException e)
                    {
                        plugin.getLogger().log(Level.SEVERE, "Failed to create the table prisons_balances: " + e.getMessage());
                    }
                }
            }
            catch (SQLException e)
            {
                plugin.getLogger().log(Level.SEVERE, "Failed to open a connection to the database: " + e.getMessage());
            }
        });
    }

    private void registerCurrencies()
    {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getDatabaseManager().getConnection())
            {
                try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM prisons_currencies;"))
                {
                    try (ResultSet resultSet = statement.executeQuery())
                    {
                        // Are there any currencies in the database?
                        if (!resultSet.isBeforeFirst())
                        {
                            plugin.getLogger().log(Level.INFO, "No currencies exist in the database.");
                            return;
                        }

                        // Loop over all currencies in the database
                        plugin.getLogger().log(Level.INFO, "Registering currencies from the database...");
                        while (resultSet.next())
                        {
                            // Args
                            int currencyId = resultSet.getInt("currency_id");
                            String name = resultSet.getString("name");
                            String symbol = resultSet.getString("symbol");

                            // Create the currency
                            ProtonCurrency currency = new ProtonCurrency(currencyId, name, symbol);

                            // Log & add the currency to the set
                            plugin.getLogger().log(Level.INFO, "Registering currency with id: " + currencyId + ", name: " + name + ".");
                            this.currencies.add(currency);
                        }

                        // Log
                        plugin.getLogger().log(Level.INFO, "Finished registering currencies from the database.");
                    }
                }
                catch (SQLException e)
                {
                    plugin.getLogger().log(Level.SEVERE, "Failed to fetch currencies from the database: " + e.getMessage());
                }
            }
            catch (SQLException e)
            {
                plugin.getLogger().log(Level.SEVERE, "Failed to open a connection to the database: " + e.getMessage());
            }
        });
    }

    public void disable() { this.currencies.clear(); }

}
