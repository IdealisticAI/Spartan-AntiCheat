package com.vagdedes.spartan.compatibility.manual.building;

import com.vagdedes.spartan.abstraction.configuration.implementation.Compatibility;
import com.vagdedes.spartan.abstraction.replicates.SpartanPlayer;
import com.vagdedes.spartan.functionality.protections.CheckDelay;
import com.vagdedes.spartan.functionality.server.SpartanBukkit;
import me.rampen88.drills.events.DrillBreakEvent;
import me.vagdedes.spartan.system.Enums;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class RampenDrills implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void Event(DrillBreakEvent e) {
        SpartanPlayer p = SpartanBukkit.getPlayer(e.getPlayer());

        if (p == null) {
            return;
        }
        Compatibility.CompatibilityType compatibilityType = Compatibility.CompatibilityType.RampenDrills;

        if (compatibilityType.isFunctional()) {
            CheckDelay.evadeCommonFalsePositives(p, compatibilityType,
                    new Enums.HackType[]{
                            Enums.HackType.FastBreak,
                            Enums.HackType.NoSwing,
                            Enums.HackType.GhostHand,
                    }, 5);
        }
    }
}
