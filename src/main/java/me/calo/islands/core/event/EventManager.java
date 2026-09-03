package me.calo.islands.core.event;

import me.calo.islands.CaloIslandsPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class EventManager {

    private final CaloIslandsPlugin plugin;
    private final Map<String, GameEvent> events = new LinkedHashMap<>();

    private BukkitTask schedulerTask;

    public EventManager(CaloIslandsPlugin plugin) {
        this.plugin = plugin;
    }

    public void register(GameEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("event no puede ser null");
        }

        String id = normalize(event.id());

        if (events.containsKey(id)) {
            throw new IllegalStateException(
                    "Ya existe un evento registrado con ID: " + id
            );
        }

        events.put(id, event);
    }

    public GameEvent get(String id) {
        if (id == null) {
            return null;
        }

        return events.get(normalize(id));
    }

    public Collection<GameEvent> events() {
        return events.values();
    }

    public boolean start(String id) {
        GameEvent event = get(id);

        if (event == null) {
            return false;
        }

        return event.start();
    }

    public boolean stop(String id) {
        GameEvent event = get(id);

        if (event == null) {
            return false;
        }

        event.stop();
        return true;
    }

    public void startScheduler() {
        if (schedulerTask != null) {
            return;
        }

        schedulerTask = plugin.getServer()
                .getScheduler()
                .runTaskTimer(
                        plugin,
                        this::tick,
                        20L,
                        20L
                );
    }

    private void tick() {
        for (GameEvent event : events.values()) {
            try {
                event.tick();
            } catch (Exception exception) {
                plugin.getLogger().severe(
                        "Error actualizando evento " + event.id()
                );
                exception.printStackTrace();
            }
        }
    }

    public void shutdown() {
        if (schedulerTask != null) {
            schedulerTask.cancel();
            schedulerTask = null;
        }

        for (GameEvent event : events.values()) {
            try {
                event.stop();
            } catch (Exception exception) {
                plugin.getLogger().severe(
                        "Error cerrando evento " + event.id()
                );
                exception.printStackTrace();
            }
        }

        events.clear();
    }

    private String normalize(String id) {
        return id.toLowerCase(Locale.ROOT).trim();
    }
}