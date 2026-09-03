package me.calo.islands.data;

import me.calo.islands.CaloIslandsPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseManager {

    private final CaloIslandsPlugin plugin;
    private final File databaseFile;

    public DatabaseManager(CaloIslandsPlugin plugin) {
        this.plugin = plugin;
        this.databaseFile = new File(plugin.getDataFolder(), "data.db");
    }

    public void initialize() throws SQLException {
        if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
            throw new IllegalStateException(
                    "No se pudo crear la carpeta de CaloIslands."
            );
        }

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("PRAGMA journal_mode=WAL;");
            statement.execute("PRAGMA synchronous=NORMAL;");
            statement.execute("PRAGMA foreign_keys=ON;");
            statement.execute("PRAGMA busy_timeout=5000;");

            createTables(statement);
        }
    }

    private void createTables(Statement statement) throws SQLException {

        statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS player_weekly_limits (
                    player_uuid TEXT NOT NULL,
                    limit_id TEXT NOT NULL,
                    period_key TEXT NOT NULL,
                    amount INTEGER NOT NULL DEFAULT 0,
                    updated_at INTEGER NOT NULL,
                    PRIMARY KEY (player_uuid, limit_id, period_key)
                );
                """);

        statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS player_purchases (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    player_uuid TEXT NOT NULL,
                    purchase_id TEXT NOT NULL,
                    amount INTEGER NOT NULL DEFAULT 1,
                    purchased_at INTEGER NOT NULL
                );
                """);

        statement.executeUpdate("""
                CREATE INDEX IF NOT EXISTS idx_player_purchases_player
                ON player_purchases(player_uuid);
                """);

        statement.executeUpdate("""
                CREATE INDEX IF NOT EXISTS idx_player_purchases_type
                ON player_purchases(purchase_id);
                """);
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:sqlite:" + databaseFile.getAbsolutePath()
        );
    }

    public File getDatabaseFile() {
        return databaseFile;
    }
}
