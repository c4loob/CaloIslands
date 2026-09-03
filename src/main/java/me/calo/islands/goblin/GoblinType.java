package me.calo.islands.goblin;
public enum GoblinType {
    NORMAL, SPECIAL, ELITE, CAPTAIN, KING;
    public static GoblinType fromScoreboardTag(String tag) {
        if (tag == null || !tag.startsWith("caloislands_goblin_")) return null;
        try { return valueOf(tag.substring("caloislands_goblin_".length()).toUpperCase()); }
        catch (IllegalArgumentException ex) { return null; }
    }
    public String configId() { return name().toLowerCase(); }
}
