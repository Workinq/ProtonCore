package kr.kieran.protonprisons.managers;

import com.zaxxer.hikari.HikariDataSource;
import kr.kieran.protonprisons.ProtonPrisonsPlugin;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager
{

    private final ProtonPrisonsPlugin plugin;
    private final HikariDataSource dataSource = new HikariDataSource();

    public DatabaseManager(ProtonPrisonsPlugin plugin)
    {
        this.plugin = plugin;
        this.registerProperties();
    }

    private void registerProperties()
    {
        // Driver & pool size
        dataSource.setMaximumPoolSize(10);
        dataSource.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");

        // Credentials
        dataSource.addDataSourceProperty("serverName", plugin.getConfig().getString("mysql.host"));
        dataSource.addDataSourceProperty("port", plugin.getConfig().getInt("mysql.port"));
        dataSource.addDataSourceProperty("databaseName", plugin.getConfig().getString("mysql.database"));
        dataSource.addDataSourceProperty("user", plugin.getConfig().getString("mysql.username"));
        dataSource.addDataSourceProperty("password", plugin.getConfig().getString("mysql.password"));
    }

    public Connection getConnection() throws SQLException
    {
        return dataSource.getConnection();
    }

    public void disable() { this.dataSource.close(); }

}
