package com.vagdedes.spartan.functionality.chat;

import com.vagdedes.spartan.abstraction.replicates.SpartanLocation;
import com.vagdedes.spartan.abstraction.replicates.SpartanPlayer;
import com.vagdedes.spartan.compatibility.necessary.Authentication;
import com.vagdedes.spartan.functionality.connection.cloud.CrossServerInformation;
import com.vagdedes.spartan.functionality.management.Config;
import com.vagdedes.spartan.functionality.server.Permissions;
import com.vagdedes.spartan.functionality.server.SpartanBukkit;
import com.vagdedes.spartan.utils.server.ConfigUtils;
import me.vagdedes.spartan.system.Enums;

import java.util.List;

public class StaffChat {

    public static boolean run(SpartanPlayer p, String msg) {
        if (Permissions.has(p, Enums.Permission.STAFF_CHAT) && (!Authentication.isEnabled() || (System.currentTimeMillis() - p.creationTime) > 60_000L)) {
            String character = Config.settings.getString("Chat.staff_chat_character");

            if (character != null && character.length() > 0 && msg.startsWith(character.toLowerCase())) {
                msg = msg.substring(1);
                String message = Config.messages.getColorfulString("staff_chat_message");
                message = message.replace("{message}", msg);
                message = ConfigUtils.replaceWithSyntax(p, message, null);
                List<SpartanPlayer> players = SpartanBukkit.getPlayers();

                if (!players.isEmpty()) {
                    for (SpartanPlayer o : players) {
                        if (Permissions.has(o, Enums.Permission.STAFF_CHAT)) {
                            o.sendMessage(message);
                        }
                    }
                }

                SpartanLocation location = p.getLocation();
                CrossServerInformation.queueNotificationWithWebhook(p.uuid, p.name,
                        location.getBlockX(), location.getBlockY(), location.getBlockZ(),
                        "Staff Chat", msg,
                        false);
                return true;
            }
        }
        return false;
    }
}
