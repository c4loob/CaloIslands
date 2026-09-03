package me.calo.islands.events.cerberus;

import me.calo.islands.CaloIslandsPlugin;
import me.calo.islands.combat.BossParticipationService;
import me.calo.islands.core.event.EventState;
import me.calo.islands.core.event.GameEvent;
import me.calo.islands.integration.mythicmobs.MythicMobsHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.UUID;

public final class CerberusEvent implements GameEvent {

    private final CaloIslandsPlugin plugin;
    private final MythicMobsHook mythic;
    private final BossParticipationService participation;

    private EventState state = EventState.WAITING;

    private UUID bossId;
    private long endAtMillis;

    public CerberusEvent(
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
        return "cerberus";
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
                        "events.cerberus.location.world",
                        "world"
                )
        );

        if (world == null) {
            return false;
        }

        Location location = new Location(
                world,
                plugin.getConfig().getDouble(
                        "events.cerberus.location.x"
                ),
                plugin.getConfig().getDouble(
                        "events.cerberus.location.y"
                ),
                plugin.getConfig().getDouble(
                        "events.cerberus.location.z"
                )
        );

        String mythicId =
                plugin.getConfig().getString(
                        "events.cerberus.mythic-id",
                        "CerberusBoss"
                );

        state = EventState.STARTING;

        Entity boss =
                mythic.spawn(mythicId, location);

        if (boss == null) {
            state = EventState.WAITING;
            return false;
        }

        bossId = boss.getUniqueId();
        participation.registerBoss(boss);

        endAtMillis =
                System.currentTimeMillis()
                        + plugin.getConfig().getLong(
                        "events.cerberus.duration-minutes",
                        90
                ) * 60_000L;

        state = EventState.ACTIVE;

        Bukkit.broadcastMessage(
                "§c§lCERBERUS §8» §f¡Cerberus ha despertado!"
        );

        return true;
    }

    @Override
    public void stop() {

        if (state == EventState.WAITING) {
            return;
        }

        state = EventState.CLEANUP;

        if (bossId != null) {

            Entity boss =
                    Bukkit.getEntity(bossId);

            if (boss != null && boss.isValid()) {
                boss.remove();
            }

            participation.remove(bossId);
        }

        bossId = null;
        endAtMillis = 0L;
        state = EventState.WAITING;
    }

    @Override
    public void tick() {

        if (state != EventState.ACTIVE) {
            return;
        }

        Entity boss =
                bossId == null
                        ? null
                        : Bukkit.getEntity(bossId);

        if (boss == null
                || !boss.isValid()
                || boss.isDead()) {

            stop();
            return;
        }

        if (System.currentTimeMillis() >= endAtMillis) {

            Bukkit.broadcastMessage(
                    "§c§lCERBERUS §8» §7El evento ha terminado."
            );

            stop();
        }
    }

    public boolean owns(UUID entityId) {
        return bossId != null
                && bossId.equals(entityId);
    }
}