package me.calo.islands.combat;

import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class DamageTracker {

    private final Map<UUID, Double> damage = new HashMap<>();
    private double totalDamage;

    public void addDamage(Player player, double amount) {
        if (player == null || amount <= 0.0) {
            return;
        }

        damage.merge(player.getUniqueId(), amount, Double::sum);
        totalDamage += amount;
    }

    public double damage(UUID playerId) {
        return damage.getOrDefault(playerId, 0.0);
    }

    public double percentage(UUID playerId) {
        if (totalDamage <= 0.0) {
            return 0.0;
        }

        return damage(playerId) / totalDamage * 100.0;
    }

    public Map<UUID, Double> snapshot() {
        return Collections.unmodifiableMap(new HashMap<>(damage));
    }

    public double totalDamage() {
        return totalDamage;
    }

    public void clear() {
        damage.clear();
        totalDamage = 0.0;
    }
}