package me.calo.islands.combat;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

public final class BossDamageListener implements Listener {

    private final BossParticipationService participation;

    public BossDamageListener(BossParticipationService participation) {
        this.participation = participation;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!participation.isTracked(event.getEntity().getUniqueId())) {
            return;
        }

        Player player = resolvePlayer(event);

        if (player == null) {
            return;
        }

        participation.recordDamage(
                event.getEntity().getUniqueId(),
                player,
                event.getFinalDamage()
        );
    }

    private Player resolvePlayer(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            return player;
        }

        if (event.getDamager() instanceof Projectile projectile) {
            ProjectileSource shooter = projectile.getShooter();

            if (shooter instanceof Player player) {
                return player;
            }
        }

        return null;
    }
}