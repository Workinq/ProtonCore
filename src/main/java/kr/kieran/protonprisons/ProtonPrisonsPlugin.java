package kr.kieran.protonprisons;

import kr.kieran.protonprisons.commands.currency.CurrencyCommand;
import kr.kieran.protonprisons.commands.mines.MinesCommand;
import kr.kieran.protonprisons.commands.pickaxe.PickaxeCommand;
import kr.kieran.protonprisons.listeners.BlockBreakListeners;
import kr.kieran.protonprisons.listeners.MineTrackerListeners;
import kr.kieran.protonprisons.listeners.PlayerConnectionListeners;
import kr.kieran.protonprisons.managers.CurrencyManager;
import kr.kieran.protonprisons.managers.DatabaseManager;
import kr.kieran.protonprisons.managers.EnchantManager;
import kr.kieran.protonprisons.managers.MineManager;
import kr.kieran.protonprisons.managers.PickaxeManager;
import kr.kieran.protonprisons.managers.ProfileManager;
import kr.kieran.protonprisons.managers.SafesManager;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class ProtonPrisonsPlugin extends JavaPlugin
{

    // INSTANCE & CONSTRUCT
    private static ProtonPrisonsPlugin instance;
    public static ProtonPrisonsPlugin get() { return instance; }
    public ProtonPrisonsPlugin() { instance = this; }

    // DATABASE MANAGER
    private DatabaseManager databaseManager;
    public DatabaseManager getDatabaseManager() { return databaseManager; }

    // MINE MANAGER
    private MineManager mineManager;
    public MineManager getMineManager() { return mineManager; }

    // PROFILE MANAGER
    private ProfileManager profileManager;
    public ProfileManager getProfileManager() { return profileManager; }

    // CURRENCY MANAGER
    private CurrencyManager currencyManager;
    public CurrencyManager getCurrencyManager() { return currencyManager; }

    // ENCHANT MANAGER
    private EnchantManager enchantManager;
    public EnchantManager getEnchantManager() { return enchantManager; }

    // PICKAXE MANAGER
    private PickaxeManager pickaxeManager;
    public PickaxeManager getPickaxeManager() { return pickaxeManager; }

    // SAFES MANAGER
    private SafesManager safesManager;
    public SafesManager getSafesManager() { return safesManager; }

    // ENCHANTS
    private YamlConfiguration enchants;
    public YamlConfiguration getEnchants() { return enchants; }

    @Override
    public void onLoad()
    {
        // Used to create the data folder
        this.saveDefaultConfig();
    }

    @Override
    public void onEnable()
    {
        this.registerManagers();
        this.registerListeners();
        this.registerCommands();
    }

    @Override
    public void onDisable()
    {
        this.safesManager.disable();
        this.mineManager.disable();
        this.enchantManager.disable();
        this.profileManager.disable();
        this.currencyManager.disable();
        this.databaseManager.disable();
    }

    private void registerManagers()
    {
        this.databaseManager = new DatabaseManager(this);
        this.currencyManager = new CurrencyManager(this);
        this.profileManager = new ProfileManager(this);
        this.enchantManager = new EnchantManager(this);
        this.mineManager = new MineManager(this);
        this.pickaxeManager = new PickaxeManager(this);
        this.safesManager = new SafesManager(this);
    }

    private void registerListeners()
    {
        this.getServer().getPluginManager().registerEvents(new PlayerConnectionListeners(this), this);
        this.getServer().getPluginManager().registerEvents(new MineTrackerListeners(this), this);
        this.getServer().getPluginManager().registerEvents(new BlockBreakListeners(this), this);
    }

    private void registerCommands()
    {
        // Currencies
        CurrencyCommand currencyCommand = new CurrencyCommand(this);
        this.getCommand("currency").setExecutor(currencyCommand);

        // Mines
        MinesCommand minesCommand = new MinesCommand(this);
        this.getCommand("mines").setExecutor(minesCommand);

        // Pickaxes
        PickaxeCommand pickaxeCommand = new PickaxeCommand(this);
        this.getCommand("pickaxe").setExecutor(pickaxeCommand);
    }

    @Override
    public void saveDefaultConfig()
    {
        // Create config.yml file if it doesn't exist
        super.saveDefaultConfig();

        // Check if enchants file exists
        File enchantsFile = new File(this.getDataFolder(), "enchants.yml");
        if (!enchantsFile.exists()) this.saveResource("enchants.yml", false);

        this.enchants = new YamlConfiguration();
        try
        {
            this.enchants.load(enchantsFile);
        }
        catch (IOException | InvalidConfigurationException e)
        {
            this.getLogger().log(Level.SEVERE, "Failed to load enchants.yml: " + e.getMessage());
        }
    }

}
