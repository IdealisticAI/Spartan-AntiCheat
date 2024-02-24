package com.vagdedes.spartan.objects.profiling;

import com.vagdedes.spartan.configuration.Config;
import com.vagdedes.spartan.functionality.configuration.AntiCheatLogs;
import com.vagdedes.spartan.functionality.synchronicity.CrossServerInformation;
import com.vagdedes.spartan.objects.replicates.SpartanLocation;
import com.vagdedes.spartan.objects.replicates.SpartanPlayer;

public class PunishmentHistory {

    public static final String
            punishmentMessage = " was punished with the commands: ",
            warningMessage = " was warned for ",
            kickMessage = " was kicked for ";

    private final PlayerProfile profile;
    private int kicks, warnings, punishments;

    PunishmentHistory(PlayerProfile profile) {
        this.profile = profile;
        this.kicks = 0;
        this.warnings = 0;
        this.punishments = 0;
    }

    public int getOverall() {
        return kicks + warnings + punishments;
    }

    public int getKicks() {
        return kicks;
    }

    public void increaseKicks(SpartanPlayer player, String reason) {
        kicks++;

        if (reason != null) {
            AntiCheatLogs.logInfo(Config.getConstruct() + profile.getName() + kickMessage + reason, true);
            SpartanLocation location = player.getLocation();
            CrossServerInformation.queueNotificationWithWebhook(
                    player.getUniqueId(),
                    player.getName(),
                    location.getBlockX(),
                    location.getBlockY(),
                    location.getBlockZ(),
                    "Kick",
                    reason,
                    true
            );
        }
    }

    public int getPunishments() {
        return punishments;
    }

    public int getWarnings() {
        return warnings;
    }

    public void increaseWarnings(SpartanPlayer player, String reason) {
        warnings++;

        if (reason != null) {
            AntiCheatLogs.logInfo(Config.getConstruct() + profile.getName() + warningMessage + reason, true);
            SpartanLocation location = player.getLocation();
            CrossServerInformation.queueNotificationWithWebhook(
                    player.getUniqueId(),
                    player.getName(),
                    location.getBlockX(),
                    location.getBlockY(),
                    location.getBlockZ(),
                    "Warning",
                    reason,
                    true
            );
        }
    }

    public void increasePunishments(SpartanPlayer player, String commands) {
        punishments++;

        if (commands != null) {
            AntiCheatLogs.logInfo(Config.getConstruct() + profile.getName() + punishmentMessage + commands, true);
            SpartanLocation location = player.getLocation();
            CrossServerInformation.queueWebhook(
                    player.getUniqueId(),
                    player.getName(),
                    location.getBlockX(),
                    location.getBlockY(),
                    location.getBlockZ(),
                    "Punishment",
                    commands
            );
        }
    }
}