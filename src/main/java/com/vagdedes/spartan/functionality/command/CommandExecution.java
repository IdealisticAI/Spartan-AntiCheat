package com.vagdedes.spartan.functionality.command;

import com.vagdedes.spartan.Register;
import com.vagdedes.spartan.abstraction.check.Check;
import com.vagdedes.spartan.abstraction.configuration.implementation.Compatibility;
import com.vagdedes.spartan.abstraction.replicates.SpartanPlayer;
import com.vagdedes.spartan.compatibility.manual.essential.MinigameMaker;
import com.vagdedes.spartan.functionality.connection.IDs;
import com.vagdedes.spartan.functionality.connection.cloud.CloudConnections;
import com.vagdedes.spartan.functionality.connection.cloud.SpartanEdition;
import com.vagdedes.spartan.functionality.inventory.InteractiveInventory;
import com.vagdedes.spartan.functionality.management.Config;
import com.vagdedes.spartan.functionality.moderation.Wave;
import com.vagdedes.spartan.functionality.notifications.AwarenessNotifications;
import com.vagdedes.spartan.functionality.notifications.DetectionNotifications;
import com.vagdedes.spartan.functionality.notifications.clickable.ClickableMessage;
import com.vagdedes.spartan.functionality.server.Permissions;
import com.vagdedes.spartan.functionality.server.SpartanBukkit;
import com.vagdedes.spartan.utils.math.AlgebraUtils;
import com.vagdedes.spartan.utils.server.ConfigUtils;
import com.vagdedes.spartan.utils.server.NetworkUtils;
import io.signality.utils.system.Events;
import me.vagdedes.spartan.api.API;
import me.vagdedes.spartan.system.Enums;
import me.vagdedes.spartan.system.Enums.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class CommandExecution implements CommandExecutor {

    public static final int maxConnectedArgumentLength = 4096;

    private static void buildCommand(CommandSender sender, ChatColor chatColor, String command, String description) {
        ClickableMessage.sendCommand(sender, chatColor + command, description, command);
    }

    public static boolean spartanMessage(CommandSender sender, boolean isPlayer) {
        if (!isPlayer || Permissions.has((Player) sender)) {
            String v = API.getVersion();
            sender.sendMessage("");
            String command = "§4" + SpartanEdition.getProductName(false)
                    + " §8[§7(§fVersion: " + v + "§7)§8, §7(§fID: "
                    + IDs.hide(IDs.user()) + "/" + IDs.hide(IDs.nonce()) + "§7)§8]";

            sender.sendMessage(command);
            sender.sendMessage("§8§l<> §7Required command argument");
            sender.sendMessage("§8§l[] §7Optional command argument");
            return true;
        }
        sender.sendMessage(Config.messages.getColorfulString("unknown_command"));
        return false;
    }

    public static void completeMessage(CommandSender sender, String list) {
        boolean isPlayer = sender instanceof Player;
        SpartanPlayer player = isPlayer ? SpartanBukkit.getPlayer((Player) sender) : null;
        isPlayer &= player != null;

        if (spartanMessage(sender, isPlayer)) {
            String command = Register.plugin.getName().toLowerCase();
            boolean info = !isPlayer || Permissions.has(player, Enums.Permission.INFO),
                    manage = !isPlayer || Permissions.has(player, Enums.Permission.MANAGE);

            switch (list) {
                case "default":
                    if ((info || manage) && isPlayer) {
                        buildCommand(sender, ChatColor.GREEN, "/" + command + " menu",
                                "Click this command to open the plugin's inventory menu.");
                    }
                    if (!isPlayer || Permissions.has(player, Permission.ADMIN)) {
                        ClickableMessage.sendCommand(sender, ChatColor.GREEN + "/" + command + " ai-assistance <question>", "This command can be used to ask our AI Assistant about most of the help you may need.", null);
                        ClickableMessage.sendCommand(sender, ChatColor.GREEN + "/" + command + " customer-support <discord-tag>", "This command can be used to provide crucial details to the developers about problematic detections.", null);
                        ClickableMessage.sendCommand(sender, ChatColor.GREEN + "/" + command + " customer-support <check> <discord-tag> [explanation]", "This command can be used to provide crucial details to the developers about a check.", null);
                    }
                    if (manage) {
                        ClickableMessage.sendCommand(sender, ChatColor.RED + "/" + command + " toggle <check>",
                                "This command can be used to enable/disable a check and its detections.", null);
                    }
                    if (!isPlayer || Permissions.has(player, Permission.RELOAD)) {
                        buildCommand(sender, ChatColor.RED, "/" + command + " reload",
                                "Click this command to reload the plugin's cache.");
                    }
                    if (!isPlayer || info
                            || Permissions.has(player, Permission.KICK)
                            || Permissions.has(player, Permission.WARN)
                            || Permissions.has(player, Permission.USE_BYPASS)
                            || Permissions.has(player, Permission.WAVE)) {
                        buildCommand(sender, ChatColor.RED, "/" + command + " moderation",
                                "Click this command to view a list of moderation commands.");
                    }
                    if (!isPlayer || Permissions.has(player, Permission.CONDITION)) {
                        buildCommand(sender, ChatColor.RED, "/" + command + " conditions",
                                "Click this command to view a list of conditional commands.");
                    }
                    break;
                case "moderation":
                    boolean permission = false;

                    if (isPlayer && DetectionNotifications.hasPermission(player)) {
                        ClickableMessage.sendCommand(sender, ChatColor.RED + "/" + command + " notifications [frequency]",
                                "This command can be used to receive chat messages whenever a player is suspected of using hack modules.", null);
                    }
                    if (isPlayer && info) {
                        ClickableMessage.sendCommand(sender, ChatColor.RED + "/" + command + " info [player]",
                                "This command can be used to view useful information about a player and execute actions upon them.", null);
                    }
                    if (!isPlayer || Permissions.has(player, Permission.USE_BYPASS)) {
                        permission = true;
                        ClickableMessage.sendCommand(sender, ChatColor.RED + "/" + command + " bypass <player> <check> [seconds]",
                                "This command can be used to cause a player to temporarily bypass a check and its detections.", null);
                    }
                    if (!isPlayer || Permissions.has(player, Permission.WARN)) {
                        permission = true;
                        ClickableMessage.sendCommand(sender, ChatColor.RED + "/" + command + " warn <player> <reason>",
                                "This command can be used to individually warn a player about something important.", null);
                    }
                    if (!isPlayer || Permissions.has(player, Permission.KICK)) {
                        permission = true;
                        ClickableMessage.sendCommand(sender, ChatColor.RED + "/" + command + " kick <player> <reason>",
                                "This command can be used to kick players from the server for a specific reason.", null);
                    }
                    if (!isPlayer || Permissions.has(player, Permission.WAVE)) {
                        permission = true;
                        ClickableMessage.sendCommand(sender, ChatColor.RED + "/" + command + " wave <add/remove/clear/run/list> [player] [command]",
                                "This command can be used to add a player to a list with a command representing their punishment. " +
                                        "This list can be executed manually by a player or automatically based on the plugin's configuration, " +
                                        "and cause added players to punished all at once and in order."
                                        + "\n\n"
                                        + "Example: /" + command + " wave add playerName ban {player} You have been banned for hacking!", null);
                    }
                    if (!isPlayer || Permissions.has(player, Permission.ADMIN)) {
                        permission = true;
                        ClickableMessage.sendCommand(sender, ChatColor.RED + "/" + command + " proxy-command <command>",
                                "This command can be used to transfer commands to the proxy/network of servers. (Example: BungeeCord)", null);
                    }

                    if (!permission) {
                        completeMessage(sender, "default");
                    }
                    break;
                case "conditions":
                    if (!isPlayer || Permissions.has(player, Permission.CONDITION)) {
                        sender.sendMessage(ChatColor.RED + "/" + command + " <player> if <condition> equals <result> do <command>");
                        sender.sendMessage(ChatColor.RED + "/" + command + " <player> if <condition> contains <result> do <command>");
                        sender.sendMessage(ChatColor.RED + "/" + command + " <player> if <number> is-less-than <result> do <command>");
                        sender.sendMessage(ChatColor.RED + "/" + command + " <player> if <number> is-greater-than <result> do <command>");
                    } else {
                        completeMessage(sender, "default");
                    }
                    break;
                case "commands":
                    sender.sendMessage(ChatColor.RED + "Run '/spartan commands' for a list of commands");
                default:
                    break;
            }
        }
    }

    public static int num(final String s) {
        return Integer.parseInt(s);
    }

    public static double dbl(final String s) {
        return Double.parseDouble(s);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        boolean isPlayer = sender instanceof Player;

        if (label.equalsIgnoreCase(Register.plugin.getName()) && (isPlayer || sender instanceof ConsoleCommandSender)) {
            SpartanPlayer player = isPlayer ? SpartanBukkit.getPlayer((Player) sender) : null;

            if (isPlayer && player == null) {
                return false;
            }
            if (args.length == 0) {
                if (isPlayer && InteractiveInventory.mainMenu.open(player, false)) {
                    completeMessage(sender, "commands");
                } else {
                    completeMessage(sender, "default");
                }
            } else if (args.length == 1) {
                if (isPlayer && args[0].equalsIgnoreCase("Menu")) {
                    InteractiveInventory.mainMenu.open(player);

                } else if (args[0].equalsIgnoreCase("Moderation")) {
                    completeMessage(sender, args[0].toLowerCase());

                } else if (args[0].equalsIgnoreCase("Conditions")) {
                    completeMessage(sender, args[0].toLowerCase());

                } else if (args[0].equalsIgnoreCase("Reload") || args[0].equalsIgnoreCase("Rl")) {
                    if (isPlayer && !Permissions.has((Player) sender, Permission.RELOAD)) {
                        sender.sendMessage(Config.messages.getColorfulString("no_permission"));
                        return true;
                    }
                    Config.reload(sender);

                } else if (isPlayer && args[0].equalsIgnoreCase("Info")) {
                    if (!Permissions.has((Player) sender, Permission.INFO)) {
                        sender.sendMessage(Config.messages.getColorfulString("no_permission"));
                        return true;
                    }
                    InteractiveInventory.playerInfo.open(player, sender.getName());

                } else if (isPlayer && args[0].equalsIgnoreCase("Notifications")) {
                    if (!DetectionNotifications.hasPermission(player)) {
                        sender.sendMessage(Config.messages.getColorfulString("no_permission"));
                        return true;
                    }
                    DetectionNotifications.toggle(player, 0);

                } else if (Compatibility.CompatibilityType.MinigameMaker.isFunctional() && args[0].equalsIgnoreCase("Add-Incompatible-Item")) {
                    if (isPlayer && !Permissions.has((Player) sender, Permission.MANAGE)) {
                        sender.sendMessage(Config.messages.getColorfulString("no_permission"));
                        return true;
                    }
                    int counter = 0;
                    StringBuilder events = new StringBuilder();

                    for (Events.EventType eventType : Events.EventType.values()) {
                        counter++;
                        String event = eventType.toString().toLowerCase().replace("_", "-");

                        if (counter % 2 == 0) {
                            events.append(ChatColor.YELLOW).append(event).append(ChatColor.DARK_GRAY).append(", ");
                        } else {
                            events.append(ChatColor.GRAY).append(event).append(ChatColor.DARK_GRAY).append(", ");
                        }
                    }
                    events = new StringBuilder(events.substring(0, events.length() - 2));
                    sender.sendMessage("");
                    sender.sendMessage(ChatColor.GOLD + "Events: " + events);
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "Hint: use %spc% for space characters.");
                    sender.sendMessage(ChatColor.RED + "Usage: /spartan add-incompatible-item <event> <material> [name] <check(s)> <bypass-seconds>");
                    sender.sendMessage(ChatColor.RED + "Usage: /spartan add-incompatible-item <event> <material> [name] <check(s)> <bypass-seconds>");
                    sender.sendMessage(ChatColor.GREEN + "Example #1: /spartan add-incompatible-item block-break diamond-pickaxe FastPlace|FastBreak|BlockReach 3");
                    sender.sendMessage(ChatColor.GREEN + "Example #2: /spartan add-incompatible-item player-hand-damage-player iron-sword arthur's%spc%excalibur KillAura 1");
                    sender.sendMessage("");

                    if (isPlayer) {
                        InteractiveInventory.supportIncompatibleItems.open(player);
                    }

                } else {
                    completeMessage(sender, "default");
                }
            } else {
                if (args[0].equalsIgnoreCase("AI-Assistance")) {
                    if (isPlayer && !Permissions.has((Player) sender, Permission.ADMIN)) {
                        sender.sendMessage(Config.messages.getColorfulString("no_permission"));
                        return true;
                    }
                    StringBuilder argumentsToStringBuilder = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        argumentsToStringBuilder.append(args[i]).append(" ");
                    }
                    String argumentsToString = argumentsToStringBuilder.substring(0, argumentsToStringBuilder.length() - 1);

                    if (isPlayer ? argumentsToString.length() > player.getMaxChatLength() : argumentsToString.length() > maxConnectedArgumentLength) {
                        sender.sendMessage(Config.messages.getColorfulString("massive_command_reason"));
                        return true;
                    }
                    sender.sendMessage(AwarenessNotifications.getNotification("Please wait..."));
                    SpartanBukkit.connectionThread.execute(() -> {
                        String assistance = CloudConnections.getAiAssistance(argumentsToStringBuilder.toString());

                        if (assistance != null && !assistance.isEmpty()) {
                            sender.sendMessage(AwarenessNotifications.getNotification(assistance));
                        } else {
                            sender.sendMessage(Config.messages.getColorfulString("failed_command"));
                        }
                    });
                } else if (args[0].equalsIgnoreCase("Proxy-Command")) {
                    if (isPlayer && !Permissions.has((Player) sender, Permission.ADMIN)) {
                        sender.sendMessage(Config.messages.getColorfulString("no_permission"));
                        return true;
                    }
                    StringBuilder argumentsToStringBuilder = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        argumentsToStringBuilder.append(args[i]).append(" ");
                    }
                    String argumentsToString = argumentsToStringBuilder.substring(0, argumentsToStringBuilder.length() - 1);

                    if (isPlayer ? argumentsToString.length() > player.getMaxChatLength() : argumentsToString.length() > maxConnectedArgumentLength) {
                        sender.sendMessage(Config.messages.getColorfulString("massive_command_reason"));
                        return true;
                    }
                    if (!NetworkUtils.executeCommand(isPlayer ? player.getPlayer() : null, argumentsToString)) {
                        sender.sendMessage(Config.messages.getColorfulString("failed_command"));
                        return true;
                    }
                    sender.sendMessage(Config.messages.getColorfulString("successful_command"));
                } else {
                    if (args.length == 2) {
                        if (args[0].equalsIgnoreCase("Wave")) {
                            String command = args[1];

                            if (isPlayer && !Permissions.has((Player) sender, Permission.WAVE)) {
                                sender.sendMessage(Config.messages.getColorfulString("no_permission"));
                                return true;
                            }
                            if (command.equalsIgnoreCase("Run")) {
                                if (Wave.getWaveList().length == 0) {
                                    sender.sendMessage(Config.messages.getColorfulString("empty_wave_list"));
                                    return true;
                                }
                                if (!Wave.start()) {
                                    sender.sendMessage(Config.messages.getColorfulString("failed_command"));
                                }
                            } else if (command.equalsIgnoreCase("Clear")) {
                                Wave.clear();
                                sender.sendMessage(Config.messages.getColorfulString("wave_clear_message"));
                            } else if (command.equalsIgnoreCase("List")) {
                                sender.sendMessage(ChatColor.GRAY + "Wave Queued Players" + ChatColor.DARK_GRAY + ":");
                                sender.sendMessage(Wave.getWaveListString());
                            } else {
                                completeMessage(sender, "moderation");
                            }

                        } else if (isPlayer && args[0].equalsIgnoreCase("Info")) {
                            if (!Permissions.has((Player) sender, Permission.INFO)) {
                                sender.sendMessage(Config.messages.getColorfulString("no_permission"));
                                return true;
                            }
                            InteractiveInventory.playerInfo.open(player, ConfigUtils.replaceWithSyntax(args[1], null));

                        } else if (args[0].equalsIgnoreCase("Toggle")) {
                            String check = args[1];

                            if (isPlayer && !Permissions.has((Player) sender, Permission.MANAGE)) {
                                sender.sendMessage(Config.messages.getColorfulString("no_permission"));
                                return true;
                            }
                            boolean exists = false;

                            for (Enums.HackType hackType : Enums.HackType.values()) {
                                if (hackType.getCheck().getName().equalsIgnoreCase(check)) {
                                    check = hackType.toString();
                                    exists = true;
                                    break;
                                }
                            }
                            if (exists) {
                                Enums.HackType type = Enums.HackType.valueOf(check);
                                Check checkObj = type.getCheck();

                                if (checkObj.isEnabled(null, null, null)) {
                                    checkObj.setEnabled(null, false);
                                    String message = Config.messages.getColorfulString("check_disable_message");
                                    message = ConfigUtils.replaceWithSyntax(isPlayer ? (Player) sender : null, message, type);
                                    sender.sendMessage(message);
                                } else {
                                    checkObj.setEnabled(null, true);
                                    String message = Config.messages.getColorfulString("check_enable_message");
                                    message = ConfigUtils.replaceWithSyntax(isPlayer ? (Player) sender : null, message, type);
                                    sender.sendMessage(message);
                                }
                            } else {
                                sender.sendMessage(Config.messages.getColorfulString("non_existing_check"));
                            }

                        } else if (args[0].equalsIgnoreCase("Customer-Support")) {
                            if (isPlayer && !Permissions.has((Player) sender, Permission.ADMIN)) {
                                sender.sendMessage(Config.messages.getColorfulString("no_permission"));
                                return true;
                            }
                            AwarenessNotifications.forcefullySend(sender, "Please wait...");

                            SpartanBukkit.connectionThread.execute(() -> {
                                Enums.HackType[] hackTypes = Enums.HackType.values();
                                Set<Enums.HackType> set = new HashSet<>(hackTypes.length);

                                for (Enums.HackType hackType : hackTypes) {
                                    if (hackType.getCheck().getProblematicDetections() > 0) {
                                        set.add(hackType);
                                    }
                                }
                                int size = set.size();

                                if (size > 0) {
                                    String discordTag = args[1];

                                    for (Enums.HackType hackType : set) {
                                        AwarenessNotifications.forcefullySend(sender,
                                                hackType.getCheck().getName() + ": " + CloudConnections.sendCustomerSupport(discordTag, hackType.toString(), "Identified Problematic Detection")
                                        );
                                    }
                                } else {
                                    AwarenessNotifications.forcefullySend(sender, "No problematic checks found, please specify a check name instead. (Example: /spartan customer-support " + Enums.HackType.values()[0] + " DiscordName#0001 Explanation)");
                                }
                            });

                        } else if (isPlayer && args[0].equalsIgnoreCase("Notifications")) {
                            if (!DetectionNotifications.hasPermission(player)) {
                                sender.sendMessage(Config.messages.getColorfulString("no_permission"));
                                return true;
                            }
                            String divisorString = args[1];

                            if (AlgebraUtils.validInteger(divisorString)) {
                                int divisor = Math.min(Integer.parseInt(divisorString), Check.maxViolationsPerCycle);

                                if (divisor > 0) {
                                    Integer cachedDivisor = DetectionNotifications.getDivisor(player, true);

                                    if (cachedDivisor != null && cachedDivisor != divisor) {
                                        DetectionNotifications.change(player, divisor, true);
                                    } else {
                                        DetectionNotifications.toggle(player, divisor);
                                    }
                                } else {
                                    completeMessage(sender, "moderation");
                                }
                            } else {
                                completeMessage(sender, "moderation");
                            }

                        } else {
                            completeMessage(sender, "default");
                        }
                    } else { // 3 or more arguments
                        StringBuilder argumentsToStringBuilder = new StringBuilder();
                        for (int i = 2; i < args.length; i++) {
                            argumentsToStringBuilder.append(args[i]).append(" ");
                        }
                        String argumentsToString = argumentsToStringBuilder.substring(0, argumentsToStringBuilder.length() - 1);

                        if (args[0].equalsIgnoreCase("Kick")) {
                            if (isPlayer && !Permissions.has((Player) sender, Permission.KICK)) {
                                sender.sendMessage(Config.messages.getColorfulString("no_permission"));
                                return true;
                            }
                            if (isPlayer ? argumentsToString.length() > player.getMaxChatLength() : argumentsToString.length() > maxConnectedArgumentLength) {
                                sender.sendMessage(Config.messages.getColorfulString("massive_command_reason"));
                                return true;
                            }
                            SpartanPlayer t = SpartanBukkit.getPlayer(args[1]);

                            if (t == null) {
                                sender.sendMessage(Config.messages.getColorfulString("player_not_found_message"));
                                return true;
                            }
                            t.kick(sender, argumentsToString);

                        } else if (args[0].equalsIgnoreCase("Warn")) {
                            if (isPlayer && !Permissions.has((Player) sender, Permission.WARN)) {
                                sender.sendMessage(Config.messages.getColorfulString("no_permission"));
                                return true;
                            }
                            if (isPlayer ? argumentsToString.length() > player.getMaxChatLength() : argumentsToString.length() > maxConnectedArgumentLength) {
                                sender.sendMessage(Config.messages.getColorfulString("massive_command_reason"));
                                return true;
                            }
                            SpartanPlayer t = SpartanBukkit.getPlayer(args[1]);

                            if (t == null) {
                                sender.sendMessage(Config.messages.getColorfulString("player_not_found_message"));
                                return true;
                            }
                            t.warn(sender, argumentsToString);

                        } else if (args[0].equalsIgnoreCase("Bypass")) {
                            boolean noSeconds = args.length == 3;

                            if (noSeconds || args.length == 4) {
                                Enums.HackType[] hackTypes = Enums.HackType.values();
                                int maxHackTypes = hackTypes.length;
                                String[] checks = args[2].split(",", maxHackTypes);
                                String sec = noSeconds ? null : args[3];

                                if (isPlayer && !Permissions.has((Player) sender, Permission.USE_BYPASS)) {
                                    sender.sendMessage(Config.messages.getColorfulString("no_permission"));
                                    return true;
                                }
                                SpartanPlayer t = SpartanBukkit.getPlayer(args[1]);

                                if (t == null) {
                                    sender.sendMessage(Config.messages.getColorfulString("player_not_found_message"));
                                    return true;
                                }
                                List<Enums.HackType> found = new ArrayList<>(maxHackTypes);

                                for (String check : checks) {
                                    for (Enums.HackType hackType : hackTypes) {
                                        if (hackType.getCheck().getName().equalsIgnoreCase(check)) {
                                            found.add(hackType);
                                            break;
                                        }
                                    }
                                }
                                if (!found.isEmpty()) {
                                    for (Enums.HackType hackType : found) {
                                        int seconds = noSeconds ? 0 : Integer.parseInt(sec);

                                        if (noSeconds) {
                                            t.getViolations(hackType).addDisableCause("Command-" + sender.getName(), null, 0);
                                        } else {
                                            if (seconds < 1 || seconds > 3600) {
                                                sender.sendMessage(ChatColor.RED + "Seconds must be between 1 and 3600.");
                                                return true;
                                            }
                                            t.getViolations(hackType).addDisableCause("Command-" + sender.getName(), null, seconds * 20);
                                        }
                                        String message = ConfigUtils.replaceWithSyntax(t, Config.messages.getColorfulString("bypass_message"), hackType)
                                                .replace("{time}", noSeconds ? "infinite" : String.valueOf(seconds));
                                        sender.sendMessage(message);
                                    }
                                } else {
                                    sender.sendMessage(Config.messages.getColorfulString("non_existing_check"));
                                }
                            } else {
                                completeMessage(sender, "moderation");
                            }
                        } else if (args[0].equalsIgnoreCase("Wave")) {
                            String command = args[1];
                            OfflinePlayer t = Bukkit.getOfflinePlayer(args[2]);

                            if (isPlayer && !Permissions.has((Player) sender, Permission.WAVE)) {
                                sender.sendMessage(Config.messages.getColorfulString("no_permission"));
                                return true;
                            }
                            if (command.equalsIgnoreCase("add") && args.length >= 4) {
                                if (Wave.getWaveList().length >= 100) {
                                    sender.sendMessage(Config.messages.getColorfulString("full_wave_list"));
                                    return true;
                                }
                                argumentsToStringBuilder = new StringBuilder();
                                for (int i = 3; i < args.length; i++) {
                                    argumentsToStringBuilder.append(args[i]).append(" ");
                                }
                                argumentsToString = argumentsToStringBuilder.substring(0, argumentsToStringBuilder.length() - 1);

                                if (isPlayer ? argumentsToString.length() > player.getMaxChatLength() : argumentsToString.length() > maxConnectedArgumentLength) {
                                    sender.sendMessage(Config.messages.getColorfulString("massive_command_reason"));
                                    return true;
                                }
                                String message = Config.messages.getColorfulString("wave_add_message");
                                message = ConfigUtils.replaceWithSyntax(t, message, null);
                                sender.sendMessage(message);
                                Wave.add(t.getUniqueId(), argumentsToString); // After to allow for further messages to take palce
                            } else if (command.equalsIgnoreCase("remove")) {
                                UUID uuid = t.getUniqueId();

                                if (Wave.getCommand(uuid) == null) {
                                    String message = Config.messages.getColorfulString("wave_not_added_message");
                                    message = ConfigUtils.replaceWithSyntax(t, message, null);
                                    sender.sendMessage(message);
                                    return true;
                                }
                                Wave.remove(uuid);
                                String message = Config.messages.getColorfulString("wave_remove_message");
                                message = ConfigUtils.replaceWithSyntax(t, message, null);
                                sender.sendMessage(message);
                            } else {
                                completeMessage(sender, "moderation");
                            }

                        } else if (args.length == 3 && args[0].equalsIgnoreCase("Customer-Support")) {
                            if (isPlayer && !Permissions.has((Player) sender, Permission.ADMIN)) {
                                sender.sendMessage(Config.messages.getColorfulString("no_permission"));
                                return true;
                            }
                            AwarenessNotifications.forcefullySend(sender, "Please wait...");

                            SpartanBukkit.connectionThread.execute(() -> {
                                String check = args[1];
                                String discordTag = args[2];
                                AwarenessNotifications.forcefullySend(sender, CloudConnections.sendCustomerSupport(discordTag, check, "No Provided Description"));
                            });
                        } else if (args.length >= 4) {
                            argumentsToStringBuilder = new StringBuilder();
                            for (int i = 3; i < args.length; i++) {
                                argumentsToStringBuilder.append(args[i]).append(" ");
                            }
                            argumentsToString = argumentsToStringBuilder.substring(0, argumentsToStringBuilder.length() - 1);

                            if (args[0].equalsIgnoreCase("Customer-Support")) {
                                if (isPlayer && !Permissions.has((Player) sender, Permission.ADMIN)) {
                                    sender.sendMessage(Config.messages.getColorfulString("no_permission"));
                                    return true;
                                }
                                if (isPlayer ? argumentsToString.length() > player.getMaxChatLength() : argumentsToString.length() > maxConnectedArgumentLength) {
                                    sender.sendMessage(Config.messages.getColorfulString("failed_command")); // Different reply, do not change
                                    return true;
                                }
                                String argumentsToStringThreaded = argumentsToString;
                                AwarenessNotifications.forcefullySend(sender, "Please wait...");

                                SpartanBukkit.connectionThread.execute(() -> {
                                    String check = args[1];
                                    String discordTag = args[2];
                                    AwarenessNotifications.forcefullySend(sender, CloudConnections.sendCustomerSupport(discordTag, check, argumentsToStringThreaded));
                                });

                            } else if (Compatibility.CompatibilityType.MinigameMaker.isFunctional() && args[0].equalsIgnoreCase("Add-Incompatible-Item")) {
                                if (args.length == 6) {
                                    if (isPlayer && !Permissions.has((Player) sender, Permission.MANAGE)) {
                                        sender.sendMessage(Config.messages.getColorfulString("no_permission"));
                                        return true;
                                    }
                                    String seconds = args[5];

                                    if (AlgebraUtils.validInteger(seconds) && MinigameMaker.addItem(args[1], args[2], args[4], args[3], Math.max(Integer.parseInt(seconds), 1))) {
                                        if (isPlayer) {
                                            InteractiveInventory.supportIncompatibleItems.open(player);
                                        } else {
                                            sender.sendMessage(ChatColor.GREEN + "Incompatible item successfully added.");
                                        }
                                    } else {
                                        Bukkit.dispatchCommand(sender, "spartan add-incompatible-item");
                                    }
                                } else if (args.length == 5) {
                                    if (isPlayer && !Permissions.has((Player) sender, Permission.MANAGE)) {
                                        sender.sendMessage(Config.messages.getColorfulString("no_permission"));
                                        return true;
                                    }
                                    String seconds = args[4];

                                    if (AlgebraUtils.validInteger(seconds) && MinigameMaker.addItem(args[1], args[2], args[3], "%spc%", Math.max(Integer.parseInt(seconds), 1))) {
                                        if (isPlayer) {
                                            InteractiveInventory.supportIncompatibleItems.open(player);
                                        } else {
                                            sender.sendMessage(ChatColor.GREEN + "Incompatible item successfully added.");
                                        }
                                    } else {
                                        Bukkit.dispatchCommand(sender, "spartan add-incompatible-item");
                                    }
                                } else {
                                    Bukkit.dispatchCommand(sender, "spartan add-incompatible-item");
                                }
                            } else if (args.length >= 7) {
                                if (isPlayer && !Permissions.has((Player) sender, Permission.CONDITION)) {
                                    sender.sendMessage(Config.messages.getColorfulString("no_permission"));
                                    return true;
                                }
                                SpartanPlayer t = SpartanBukkit.getPlayer(args[0]);

                                if (t == null) {
                                    sender.sendMessage(Config.messages.getColorfulString("player_not_found_message"));
                                    return true;
                                }
                                if (args[1].equalsIgnoreCase("if") && args[5].equalsIgnoreCase("do")) {
                                    final String condition = ConfigUtils.replaceWithSyntax(t, args[2], null);
                                    final String result = ConfigUtils.replaceWithSyntax(t, args[4], null);

                                    argumentsToStringBuilder = new StringBuilder();
                                    for (int i = 6; i < args.length; i++) {
                                        argumentsToStringBuilder.append(args[i]).append(" ");
                                    }
                                    final String command = ConfigUtils.replaceWithSyntax(t, argumentsToStringBuilder.substring(0, argumentsToStringBuilder.length() - 1), null);

                                    switch (args[3].toLowerCase()) {
                                        case "equals":
                                        case "=":
                                            if (condition.equalsIgnoreCase(result)) {
                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                                            }
                                            break;
                                        case "not-equals":
                                        case "/=":
                                            if (!condition.equalsIgnoreCase(result)) {
                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                                            }
                                            break;
                                        case "contains":
                                            if (condition.contains(result)) {
                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                                            }
                                            break;
                                        case "is-less-than":
                                        case "<":
                                            if (AlgebraUtils.validInteger(condition) && AlgebraUtils.validInteger(result) && num(condition) < num(result)
                                                    || AlgebraUtils.validDecimal(condition) && AlgebraUtils.validDecimal(result) && dbl(condition) < dbl(result)
                                                    || AlgebraUtils.validInteger(condition) && AlgebraUtils.validDecimal(result) && num(condition) < dbl(result)
                                                    || AlgebraUtils.validDecimal(condition) && AlgebraUtils.validInteger(result) && dbl(condition) < num(result)) {
                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                                            }
                                            break;
                                        case "is-greater-than":
                                        case ">":
                                            if (AlgebraUtils.validInteger(condition) && AlgebraUtils.validInteger(result) && num(condition) > num(result)
                                                    || AlgebraUtils.validDecimal(condition) && AlgebraUtils.validDecimal(result) && dbl(condition) > dbl(result)
                                                    || AlgebraUtils.validInteger(condition) && AlgebraUtils.validDecimal(result) && num(condition) > dbl(result)
                                                    || AlgebraUtils.validDecimal(condition) && AlgebraUtils.validInteger(result) && dbl(condition) > num(result)) {
                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            } else {
                                completeMessage(sender, "default");
                            }
                        } else {
                            completeMessage(sender, "default");
                        }
                    }
                }
            }
        }
        return false;
    }
}
