package me.calo.islands.rewards;

import me.calo.islands.CaloIslandsPlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public final class RewardItemService {

    private final NamespacedKey itemIdKey;

    public RewardItemService(CaloIslandsPlugin plugin) {
        this.itemIdKey = new NamespacedKey(plugin, "item_id");
    }

    public ItemStack lostJewel(int amount) {
        return create(
                Material.AMETHYST_SHARD,
                "lost_jewel",
                "§d§lLost Jewel",
                amount
        );
    }

    public ItemStack cerberusAsh(int amount) {
        return create(
                Material.BLAZE_POWDER,
                "cerberus_ash",
                "§6§lCeniza de Cerberus",
                amount
        );
    }

    public ItemStack volcanicRock(int amount) {
        return create(
                Material.MAGMA_CREAM,
                "volcanic_rock",
                "§c§lRoca Volcánica",
                amount
        );
    }

    private ItemStack create(
            Material material,
            String id,
            String displayName,
            int amount
    ) {
        ItemStack item = new ItemStack(material, Math.max(1, amount));
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(displayName);
        meta.getPersistentDataContainer().set(
                itemIdKey,
                PersistentDataType.STRING,
                id
        );

        item.setItemMeta(meta);
        return item;
    }
}