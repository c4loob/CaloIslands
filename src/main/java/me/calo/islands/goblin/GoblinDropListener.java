package me.calo.islands.goblin;

import me.calo.islands.CaloIslandsPlugin;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import java.util.concurrent.ThreadLocalRandom;

public final class GoblinDropListener implements Listener {
    private final CaloIslandsPlugin plugin;
    private final GoblinVillageService village;
    public GoblinDropListener(CaloIslandsPlugin plugin, GoblinVillageService village){ this.plugin=plugin; this.village=village; }
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        if(!village.isActive())return;
        Entity entity=event.getEntity();
        if(!village.isInside(entity.getLocation()))return;
        GoblinType type=resolve(entity); if(type==null)return;
        Player killer=event.getEntity().getKiller(); if(killer==null)return;
        village.participant(killer.getUniqueId()).addKill();
        int piel=roll(type,"piel"), colmillo=roll(type,"colmillo");
        int gp=village.grantPiel(killer,piel), gc=village.grantColmillo(killer,colmillo);
        if(gp>0||gc>0) killer.sendMessage("§6CaloIslands §8» §a+"+gp+" Piel §8• §f+"+gc+" Colmillo");
    }
    private GoblinType resolve(Entity entity){ for(String tag:entity.getScoreboardTags()){ GoblinType t=GoblinType.fromScoreboardTag(tag); if(t!=null)return t; } return null; }
    private int roll(GoblinType type,String material){ String p="goblin.drops."+type.configId()+"."+material+"."; double chance=plugin.getConfig().getDouble(p+"chance",0D); if(ThreadLocalRandom.current().nextDouble(100D)>=chance)return 0; int min=plugin.getConfig().getInt(p+"min",1),max=plugin.getConfig().getInt(p+"max",min); int lo=Math.min(min,max),hi=Math.max(min,max); return ThreadLocalRandom.current().nextInt(lo,hi+1); }
}
