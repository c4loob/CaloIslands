package me.calo.islands.core;

import me.calo.islands.CaloIslandsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

public final class MessageService {
    private final CaloIslandsPlugin plugin;
    private YamlConfiguration messages;
    public MessageService(CaloIslandsPlugin plugin) { this.plugin = plugin; reload(); }
    public void reload() { messages = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml")); }
    public String color(String text) { return ChatColor.translateAlternateColorCodes('&', text == null ? "" : text); }
    public String prefix() { return color(plugin.getConfig().getString("general.prefix", "&6&lCaloIslands &8» &f")); }
    public String get(String path) { return color(messages.getString(path, path)); }
    public void send(CommandSender sender, String path) { sender.sendMessage(prefix() + get(path)); }
}
