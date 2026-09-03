package me.calo.islands.events.slime;

import me.calo.islands.CaloIslandsPlugin;
import me.calo.islands.combat.BossParticipationService;
import me.calo.islands.core.event.EventState;
import me.calo.islands.core.event.GameEvent;
import me.calo.islands.integration.mythicmobs.MythicMobsHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class SlimeEvent implements GameEvent {

    private final CaloIslandsPlugin plugin;
    private final MythicMobsHook mythic;
    private final BossParticipationService participation;

    private final Set<UUID> bosses = new HashSet<>();

    private EventState state = EventState.WAITING;
    private long endAtMillis;

    public SlimeEvent(
            CaloIslandsPlugin plugin,
            MythicMobsHook mythic,
            BossParticipationService participation
    ) {
        this.plugin = plugin;
        this.mythic = mythic;
        this.participation = participation;
    }

    @Override
    public String id() {
        return "slime";
    }

    @Override
    public EventState state() {
        return state;
    }

    @Override
    public boolean start() {
        if (state != EventState.WAITING) {
            return false;
        }

        World world = Bukkit.getWorld(
                plugin.getConfig().getString(
                        "events.slime.location.world",
                        "world"
                )
        );

        if (world == null) {
            plugin.getLogger().warning(
                    "Mundo de Slime no encontrado."
            );
            return false;
        }

        Location center = new Location(
                world,
                plugin.getConfig().getDouble(
                        "events.slime.location.x"
                ),
                plugin.getConfig().getDouble(
                        "events.slime.location.y"
                ),
                plugin.getConfig().getDouble(
                        "events.slime.location.z"
                )
        );

        String mythicId =
                plugin.getConfig().getString(
                        "events.slime.mythic-id",
                        "SlimeBoss"
                );

        state = EventState.STARTING;

        for (int i = 0; i < 3; i++) {

            Location spawn =
                    center.clone().add((i - 1) * 4.0, 0, 0);

            Entity boss = mythic.spawn(
                    mythicId,
                    spawn
            );

            if (boss != null) {
                bosses.add(boss.getUniqueId());
                participation.registerBoss(boss);
            }
        }

        if (bosses.isEmpty()) {
            state = EventState.WAITING;
            return false;
        }

        long durationMinutes =
                plugin.getConfig().getLong(
                        "events.slime.duration-minutes",
                        90
                );

        endAtMillis =
                System.currentTimeMillis()
                        + durationMinutes * 60_000L;

        state = EventState.ACTIVE;

        Bukkit.broadcastMessage(
                "§d§lSLIME §8» §f¡Los tres hermanos Slime han aparecido!"
        );

        return true;
    }

    @Override
    public void stop() {

        if (state == EventState.WAITING) {
            return;
        }

        state = EventState.CLEANUP;

        for (UUID uuid : bosses) {
            Entity entity =
                    Bukkit.getEntity(uuid);

            if (entity != null && entity.isValid()) {
                entity.remove();
            }

            participation.remove(uuid);
        }

        bosses.clear();
        endAtMillis = 0L;
        state = EventState.WAITING;
    }

    @Override
    public void tick() {

        if (state != EventState.ACTIVE) {
            return;
        }

        bosses.removeIf(uuid -> {
            Entity entity = Bukkit.getEntity(uuid);
            return entity == null
                    || !entity.isValid()
                    || entity.isDead();
        });

        if (bosses.isEmpty()) {
            stop();
            return;
        }

        if (System.currentTimeMillis() >= endAtMillis) {
            Bukkit.broadcastMessage(
                    "§d§lSLIME §8» §7El evento ha terminado."
            );

            stop();
        }
    }

    public boolean owns(UUID entityId) {
        return bosses.contains(entityId);
    }
}