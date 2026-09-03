package me.calo.islands.goblin;

import me.calo.islands.CaloIslandsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import java.util.List;

public final class GoblinItemService {
    private final NamespacedKey itemIdKey;
    public GoblinItemService(CaloIslandsPlugin plugin){ itemIdKey = new NamespacedKey(plugin, "item_id"); }
    public ItemStack piel(int amount){
        ItemStack item=new ItemStack(Material.RABBIT_HIDE,Math.max(1,amount));
        ItemMeta meta=item.getItemMeta();
        meta.setDisplayName(color("&a&lPiel de Duende"));
        meta.setLore(List.of(color("&7Material de la Aldea Goblin."), color("&8Úsalo en los intercambios de la aldea.")));
        meta.getPersistentDataContainer().set(itemIdKey, PersistentDataType.STRING,"goblin_piel");
        item.setItemMeta(meta); return item;
    }
    public ItemStack colmillo(int amount){
        ItemStack item=new ItemStack(Material.PRISMARINE_SHARD,Math.max(1,amount));
        ItemMeta meta=item.getItemMeta();
        meta.setDisplayName(color("&f&lColmillo de Duende"));
        meta.setLore(List.of(color("&7Material raro de la Aldea Goblin."), color("&8Proviene de los duendes más peligrosos.")));
        meta.getPersistentDataContainer().set(itemIdKey, PersistentDataType.STRING,"goblin_colmillo");
        item.setItemMeta(meta); return item;
    }
    private String color(String text){ return ChatColor.translateAlternateColorCodes('&',text); }
}
