package me.calo.islands.combat;

import me.calo.islands.events.cerberus.CerberusEvent;
import me.calo.islands.events.slime.SlimeEvent;
import me.calo.islands.rewards.RewardItemService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public final class BossDeathListener implements Listener {

    private final BossParticipationService participation;
    private final SlimeEvent slime;
    private final CerberusEvent cerberus;
    private final RewardItemService rewards;

    public BossDeathListener(
            BossParticipationService participation,
            SlimeEvent slime,
            CerberusEvent cerberus,
            RewardItemService rewards
    ) {
        this.participation = participation;
        this.slime = slime;
        this.cerberus = cerberus;
        this.rewards = rewards;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(EntityDeathEvent event) {
        UUID entityId = event.getEntity().getUniqueId();

        boolean isSlime = slime.owns(entityId);
        boolean isCerberus = cerberus.owns(entityId);

        if (!isSlime && !isCerberus) {
            return;
        }

        event.getDrops().clear();
        event.setDroppedExp(0);

        DamageTracker tracker = participation.remove(entityId);

        if (tracker == null) {
            return;
        }

        for (Map.Entry<UUID, Double> entry : tracker.snapshot().entrySet()) {
            UUID playerId = entry.getKey();

            double percentage = tracker.percentage(playerId);
            int amount = ContributionRewards.materialAmount(percentage);

            if (amount <= 0) {
                continue;
            }

            Player player = Bukkit.getPlayer(playerId);

            if (player == null || !player.isOnline()) {
                continue;
            }

            ItemStack reward;

            if (isSlime) {
                reward = rewards.lostJewel(amount);

                player.sendMessage(
                        "§d§lSLIME §8» §fContribución: §d"
                                + String.format("%.2f", percentage)
                                + "% §8| §fLost Jewels: §d"
                                + amount
                );
            } else {
                reward = rewards.cerberusAsh(amount);

                player.sendMessage(
                        "§c§lCERBERUS §8» §fContribución: §c"
                                + String.format("%.2f", percentage)
                                + "% §8| §fCenizas: §6"
                                + amount
                );
            }

            Map<Integer, ItemStack> leftovers =
                    player.getInventory().addItem(reward);

            for (ItemStack leftover : leftovers.values()) {
                player.getWorld().dropItemNaturally(
                        player.getLocation(),
                        leftover
                );
            }
        }
    }
}