package com.vagdedes.spartan.functionality.protections;

import com.vagdedes.spartan.abstraction.data.Handlers;
import com.vagdedes.spartan.abstraction.replicates.SpartanPlayer;
import com.vagdedes.spartan.checks.movement.irregularmovements.IrregularMovements;
import com.vagdedes.spartan.compatibility.manual.vanilla.Attributes;
import com.vagdedes.spartan.functionality.identifiers.complex.predictable.BouncingBlocks;
import com.vagdedes.spartan.functionality.identifiers.complex.unpredictable.Velocity;
import com.vagdedes.spartan.functionality.identifiers.simple.DetectionLocation;
import com.vagdedes.spartan.functionality.server.Permissions;

public class ServerFlying {

    public static void run(SpartanPlayer p) {
        if (IrregularMovements.check.getCheck().getBooleanOption("limit_server_flying", false)
                && !Permissions.isBypassing(p, IrregularMovements.check)
                && p.isFlying()
                && p.getVehicle() == null
                && !p.isSleeping()
                && !p.isGliding()
                && !p.isDead()
                && !Velocity.hasCooldown(p)
                && !BouncingBlocks.isBelow(p)
                && !p.isSwimming() && !p.getHandlers().has(Handlers.HandlerType.WaterElevator)
                && !p.getHandlers().has(Handlers.HandlerType.Trident)
                && !p.getHandlers().has(Handlers.HandlerType.ExtremeCollision)
                && !Attributes.has(p, Attributes.GENERIC_FLYING_SPEED)) {
            double limit = (p.getFlySpeed() * 10.0) + 1.0;
            Double nmsDistance = p.getNmsDistance();

            if (nmsDistance != null && nmsDistance >= limit
                    || p.getCustomDistance() >= limit) {
                p.safeTeleport(DetectionLocation.get(p, true));
            }
        }
    }

}
