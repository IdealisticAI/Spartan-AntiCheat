package me.vagdedes.spartan.api;

import com.vagdedes.spartan.functionality.notifications.AwarenessNotifications;
import me.vagdedes.spartan.system.Enums.HackType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerViolationEvent extends Event implements Cancellable {

    private final Player p;
    private final HackType h;
    private final String m;
    private final int v;
    private final boolean fp;
    private boolean cancelled;

    public PlayerViolationEvent(Player player, HackType HackType, Integer violation, String message, boolean falsePositive) {
        p = player;
        h = HackType;
        v = violation;
        m = message;
        fp = falsePositive;
        cancelled = false;
    }

    public Player getPlayer() {
        return p;
    }

    public HackType getHackType() {
        return h;
    }

    public String getMessage() {
        return m;
    }

    public int getViolation() {
        return v;
    }

    public boolean isFalsePositive() {
        return fp;
    }

    @Deprecated
    public String getCategory() {
        AwarenessNotifications.forcefullySend("The Event API method 'getCategory' has been removed.");
        return null;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
