package me.calo.islands.core.event;

public interface GameEvent {

    String id();

    EventState state();

    boolean start();

    void stop();

    void tick();

    default boolean isActive() {
        return state() == EventState.ACTIVE;
    }
}