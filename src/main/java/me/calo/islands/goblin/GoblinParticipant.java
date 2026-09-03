package me.calo.islands.goblin;
import java.util.UUID;
public final class GoblinParticipant {
    private final UUID playerId;
    private int piel, colmillo, kills;
    public GoblinParticipant(UUID playerId) { this.playerId = playerId; }
    public UUID playerId(){ return playerId; }
    public int piel(){ return piel; }
    public int colmillo(){ return colmillo; }
    public int kills(){ return kills; }
    public void addKill(){ kills++; }
    public int addPiel(int requested, int cap){ int g=Math.max(0,Math.min(requested,cap-piel)); piel+=g; return g; }
    public int addColmillo(int requested, int cap){ int g=Math.max(0,Math.min(requested,cap-colmillo)); colmillo+=g; return g; }
}
