package me.calo.islands.events.goldenchest;

import me.calo.islands.CaloIslandsPlugin;
import me.calo.islands.core.event.EventState;
import me.calo.islands.core.event.GameEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class GoldenChestEvent implements GameEvent {

    private final CaloIslandsPlugin plugin;

    private final Map<UUID, Integer> points =
            new HashMap<>();

    private final SecureRandom random =
            new SecureRandom();

    private EventState state =
            EventState.WAITING;

    private long endAtMillis;

    public GoldenChestEvent(
            CaloIslandsPlugin plugin
    ) {
        this.plugin = plugin;
    }

    @Override
    public String id() {
        return "goldenchest";
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

        points.clear();

        long duration =
                plugin.getConfig().getLong(
                        "events.golden-chest.duration-minutes",
                        15
                );

        endAtMillis =
                System.currentTimeMillis()
                        + duration * 60_000L;

        state = EventState.ACTIVE;

        Bukkit.broadcastMessage(
                "§6§lCOFRE DEL ORO §8» §f¡El evento ha comenzado!"
        );

        return true;
    }

    public void addPoints(
            Player player,
            int amount
    ) {
        if (state != EventState.ACTIVE
                || player == null
                || amount <= 0) {
            return;
        }

        points.merge(
                player.getUniqueId(),
                amount,
                Integer::sum
        );
    }

    public int points(UUID playerId) {
        return points.getOrDefault(
                playerId,
                0
        );
    }

    @Override
    public void stop() {

        if (state == EventState.WAITING) {
            return;
        }

        state = EventState.ENDING;

        distributeRewards();

        points.clear();
        endAtMillis = 0L;
        state = EventState.WAITING;
    }

    private void distributeRewards() {

        List<Player> eligible =
                new ArrayList<>();

        for (Map.Entry<UUID, Integer> entry
                : points.entrySet()) {

            Player player =
                    Bukkit.getPlayer(entry.getKey());

            if (player == null
                    || !player.isOnline()) {
                continue;
            }

            int score = entry.getValue();

            if (score >= 300) {

                Bukkit.dispatchCommand(
                        Bukkit.getConsoleSender(),
                        "eco give " + player.getName() + " 250000"
                );

                player.sendMessage(
                        "§6Cofre del Oro §8» §fRango: §eCampeón"
                );

            } else if (score >= 150) {

                Bukkit.dispatchCommand(
                        Bukkit.getConsoleSender(),
                        "eco give " + player.getName() + " 125000"
                );

                player.sendMessage(
                        "§6Cofre del Oro §8» §fRango: §eGuerrero"
                );

            } else if (score >= 50) {

                Bukkit.dispatchCommand(
                        Bukkit.getConsoleSender(),
                        "eco give " + player.getName() + " 50000"
                );

                player.sendMessage(
                        "§6Cofre del Oro §8» §fRango: §eParticipante"
                );
            }

            if (score >= 50) {
                eligible.add(player);
            }
        }

        if (eligible.isEmpty()) {

            Bukkit.broadcastMessage(
                    "§6§lCOFRE DEL ORO §8» §7No hubo ganador elegible."
            );

            return;
        }

        Player winner =
                eligible.get(
                        random.nextInt(
                                eligible.size()
                        )
                );

        Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                "eco give " + winner.getName() + " 5000000"
        );

        Bukkit.broadcastMessage(
                "§6§lCOFRE DEL ORO §8» §fGanador: §e"
                        + winner.getName()
        );
    }

    @Override
    public void tick() {

        if (state != EventState.ACTIVE) {
            return;
        }

        if (System.currentTimeMillis()
                >= endAtMillis) {
            stop();
        }
    }
}