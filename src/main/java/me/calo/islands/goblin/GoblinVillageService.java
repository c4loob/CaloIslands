package me.calo.islands.goblin;

import me.calo.islands.CaloIslandsPlugin;
import me.calo.islands.core.EventState;
import me.calo.islands.core.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class GoblinVillageService {
    private final CaloIslandsPlugin plugin;
    private final MessageService messages;
    private final GoblinItemService items;
    private final Map<UUID,GoblinParticipant> participants=new HashMap<>();
    private EventState state=EventState.WAITING;
    private long startedAtMillis, nextAutoStartMillis;
    private boolean endingWarningSent;
    private BukkitTask schedulerTask;

    public GoblinVillageService(CaloIslandsPlugin plugin, MessageService messages, GoblinItemService items){
        this.plugin=plugin; this.messages=messages; this.items=items; resetNextAutoStart();
    }
    public void startScheduler(){
        if(schedulerTask!=null) schedulerTask.cancel();
        long periodTicks=Math.max(20L,plugin.getConfig().getLong("scheduler.tick-seconds",1L)*20L);
        schedulerTask=Bukkit.getScheduler().runTaskTimer(plugin,this::tick,periodTicks,periodTicks);
    }
    public void reload(){ resetNextAutoStart(); }
    public void shutdown(){ if(schedulerTask!=null)schedulerTask.cancel(); schedulerTask=null; participants.clear(); state=EventState.WAITING; }
    private void tick(){
        if(!plugin.getConfig().getBoolean("goblin.enabled",true))return;
        if(isActive()){
            long remaining=remainingSeconds();
            if(remaining<=60 && !endingWarningSent){ endingWarningSent=true; state=EventState.ENDING; broadcast("goblin.announcements.ending"); }
            if(remaining<=0) stop();
            return;
        }
        if(plugin.getConfig().getBoolean("goblin.auto-start.enabled",false) && System.currentTimeMillis()>=nextAutoStartMillis) start();
    }
    public boolean start(){
        if(isActive())return false;
        state=EventState.STARTING; participants.clear(); endingWarningSent=false; startedAtMillis=System.currentTimeMillis();
        state=EventState.ACTIVE; broadcast("goblin.announcements.start"); return true;
    }
    public boolean stop(){
        if(!isActive())return false;
        state=EventState.CLEANUP; broadcast("goblin.announcements.end"); participants.clear(); endingWarningSent=false; state=EventState.WAITING; resetNextAutoStart(); return true;
    }
    public boolean isActive(){ return state==EventState.ACTIVE || state==EventState.ENDING; }
    public EventState state(){ return state; }
    public long remainingSeconds(){
        if(!isActive())return 0;
        long durationMs=plugin.getConfig().getLong("goblin.duration-minutes",40L)*60_000L;
        return Math.max(0L,(durationMs-(System.currentTimeMillis()-startedAtMillis))/1000L);
    }
    public int participantCount(){ return participants.size(); }
    public GoblinParticipant participant(UUID id){ return participants.computeIfAbsent(id,GoblinParticipant::new); }
    public int grantPiel(Player player,int requested){ int cap=plugin.getConfig().getInt("goblin.caps.piel",20); int granted=participant(player.getUniqueId()).addPiel(requested,cap); if(granted>0)giveOrDrop(player,items.piel(granted)); return granted; }
    public int grantColmillo(Player player,int requested){ int cap=plugin.getConfig().getInt("goblin.caps.colmillo",3); int granted=participant(player.getUniqueId()).addColmillo(requested,cap); if(granted>0)giveOrDrop(player,items.colmillo(granted)); return granted; }
    public void setCenter(Location l){ plugin.getConfig().set("goblin.region.world",l.getWorld().getName()); plugin.getConfig().set("goblin.region.x",l.getX()); plugin.getConfig().set("goblin.region.y",l.getY()); plugin.getConfig().set("goblin.region.z",l.getZ()); plugin.saveConfig(); }
    public Location center(){ String wn=plugin.getConfig().getString("goblin.region.world","world"); World w=Bukkit.getWorld(wn); if(w==null)return null; return new Location(w,plugin.getConfig().getDouble("goblin.region.x"),plugin.getConfig().getDouble("goblin.region.y"),plugin.getConfig().getDouble("goblin.region.z")); }
    public boolean isInside(Location l){ Location c=center(); if(c==null||l.getWorld()==null||!l.getWorld().equals(c.getWorld()))return false; double r=plugin.getConfig().getDouble("goblin.region.radius",80D); return l.distanceSquared(c)<=r*r; }
    private void giveOrDrop(Player player, ItemStack item){ player.getInventory().addItem(item).values().forEach(left->player.getWorld().dropItemNaturally(player.getLocation(),left)); }
    private void resetNextAutoStart(){ long m=Math.max(1L,plugin.getConfig().getLong("goblin.auto-start.interval-minutes",60L)); nextAutoStartMillis=System.currentTimeMillis()+m*60_000L; }
    private void broadcast(String path){ for(String line:plugin.getConfig().getStringList(path)) Bukkit.broadcastMessage(messages.color(line)); }
}
