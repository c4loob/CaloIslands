package me.calo.islands.integration.mythicmobs;

import me.calo.islands.CaloIslandsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.Locale;

public final class MythicMobsHook {

    private final CaloIslandsPlugin plugin;

    public MythicMobsHook(CaloIslandsPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isAvailable() {
        Plugin mythic = Bukkit.getPluginManager().getPlugin("MythicMobs");

        return mythic != null && mythic.isEnabled();
    }

    public Entity spawn(String mobId, Location location) {
        if (mobId == null || mobId.isBlank() || location == null) {
            return null;
        }

        if (isAvailable()) {
            Entity mythicEntity = spawnMythicMob(mobId, location);

            if (mythicEntity != null) {
                return mythicEntity;
            }

            plugin.getLogger().warning(
                    "No se pudo spawnear MythicMob '" + mobId
                            + "'. Usando fallback vanilla."
            );
        }

        return spawnVanillaFallback(mobId, location);
    }

    private Entity spawnMythicMob(
            String mythicId,
            Location location
    ) {
        try {
            Class<?> mythicClass =
                    Class.forName(
                            "io.lumine.mythic.bukkit.MythicBukkit"
                    );

            Object mythicInstance =
                    mythicClass
                            .getMethod("inst")
                            .invoke(null);

            Object mobManager =
                    mythicClass
                            .getMethod("getMobManager")
                            .invoke(mythicInstance);

            Method spawnMethod = null;

            for (Method method : mobManager.getClass().getMethods()) {
                if (!method.getName().equals("spawnMob")) {
                    continue;
                }

                Class<?>[] parameters =
                        method.getParameterTypes();

                if (parameters.length == 2
                        && parameters[0] == String.class
                        && Location.class.isAssignableFrom(
                        parameters[1]
                )) {
                    spawnMethod = method;
                    break;
                }
            }

            if (spawnMethod == null) {
                return null;
            }

            Object result =
                    spawnMethod.invoke(
                            mobManager,
                            mythicId,
                            location
                    );

            if (result instanceof Entity entity) {
                return entity;
            }

            if (result != null) {
                try {
                    Method getEntity =
                            result.getClass()
                                    .getMethod("getEntity");

                    Object entity =
                            getEntity.invoke(result);

                    if (entity instanceof Entity bukkitEntity) {
                        return bukkitEntity;
                    }
                } catch (ReflectiveOperationException ignored) {
                }
            }

            return null;

        } catch (ReflectiveOperationException exception) {
            plugin.getLogger().warning(
                    "MythicMobs fallback activado para "
                            + mythicId
            );

            return null;
        }
    }

    private Entity spawnVanillaFallback(
            String mobId,
            Location location
    ) {
        String id =
                mobId.toLowerCase(Locale.ROOT);

        LivingEntity entity;

        if (id.contains("slime")) {
            Slime slime =
                    (Slime) location.getWorld().spawnEntity(
                            location,
                            EntityType.SLIME
                    );

            slime.setSize(8);
            slime.setCustomName(
                    "§d§lSlime Boss"
            );
            slime.setCustomNameVisible(true);

            entity = slime;

        } else if (id.contains("cerberus")) {
            entity =
                    (LivingEntity) location.getWorld()
                            .spawnEntity(
                                    location,
                                    EntityType.RAVAGER
                            );

            entity.setCustomName(
                    "§c§lCerberus"
            );
            entity.setCustomNameVisible(true);

        } else if (id.contains("goblin")) {
            entity =
                    (LivingEntity) location.getWorld()
                            .spawnEntity(
                                    location,
                                    EntityType.HUSK
                            );

            entity.setCustomName(
                    "§2§lGoblin"
            );
            entity.setCustomNameVisible(true);

        } else {
            entity =
                    (LivingEntity) location.getWorld()
                            .spawnEntity(
                                    location,
                                    EntityType.ZOMBIE
                            );

            entity.setCustomName(
                    "§7" + mobId
            );
            entity.setCustomNameVisible(true);
        }

        entity.setRemoveWhenFarAway(false);

        return entity;
    }
}