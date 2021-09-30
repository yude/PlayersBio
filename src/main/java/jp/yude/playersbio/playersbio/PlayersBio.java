package jp.yude.playersbio.playersbio;

import org.bukkit.command.Command;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class PlayersBio extends JavaPlugin {

    private Config config;
    public static PlayersBio instance;

    static Connection connection;

    @Override
    public void onEnable() {
        instance = this;
        config = new Config(this);

        // Plugin startup logic
        getLogger().info("Enabled.");
        config = new Config(this);

        // Connect to MySQL server
        // MySQL configurations / variables
        String host = config.getHost();
        Integer db_port = config.getDbPort();
        String database = config.getDatabase();
        String user = config.getUser();
        String password = config.getPassword();
        String url = "jdbc:mysql://" + host + ":" + db_port + "/" + database + "?autoReconnect=true";
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Initialize table
        String sql = "CREATE TABLE IF NOT EXISTS `players` (" +
                "  `uuid` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL UNIQUE," +
                "  `bio` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;" ;
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Run web server for REST API
        new HttpServer(connection);

        // Register events for "/bio" command
        this.getCommand("bio").setExecutor(new CommandBio());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
