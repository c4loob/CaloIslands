package me.calo.islands.combat;

import me.calo.islands.CaloIslandsPlugin;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class BossParticipationService {

    private final CaloIslandsPlugin plugin;

    private final Map<UUID, DamageTracker> trackers =
            new HashMap<>();

    public BossParticipationService(CaloIslandsPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerBoss(Entity entity) {
        if (entity == null) {
            return;
        }

        trackers.put(entity.getUniqueId(), new DamageTracker());
    }

    public boolean isTracked(UUID entityId) {
        return trackers.containsKey(entityId);
    }

    public void recordDamage(
            UUID entityId,
            Player player,
            double amount
    ) {
        DamageTracker tracker = trackers.get(entityId);

        if (tracker == null) {
            return;
        }

        tracker.addDamage(player, amount);
    }

    public DamageTracker remove(UUID entityId) {
        return trackers.remove(entityId);
    }

    public DamageTracker tracker(UUID entityId) {
        return trackers.get(entityId);
    }

    public void clear() {
        trackers.clear();
    }

    public int trackedBosses() {
        return trackers.size();
    }
}