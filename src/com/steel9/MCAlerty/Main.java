package com.steel9.MCAlerty;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;

public class Main extends JavaPlugin {
    public static FileConfiguration config;
    Logger log = Logger.getLogger("Minecraft");

    private MyListener mMyListener;

    public void onEnable() {
        ConfigurationSerialization.registerClass(Alert.class);

        config = this.getConfig();
        config.addDefault("alerts", new ArrayList<Alert>());
        config.options().copyDefaults(true);
        this.saveConfig();

        mMyListener = new MyListener(this);
        getServer().getPluginManager().registerEvents(mMyListener, this);

        log.info(getDescription().getName() + " version " + getDescription().getVersion() + " started.");
    }

    public void onDisable() {
        HandlerList.unregisterAll(this);

        log.info(getDescription().getName() + " version " + getDescription().getVersion() + " stopped.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Implement permissions!

        if (!command.getName().equalsIgnoreCase("alerty")) {
            return super.onCommand(sender, command, label, args);
        }

        if (args.length == 0) {
            sender.sendMessage("Type '/help alerty' for help.");

            return true;
        }

        if (args[0].equalsIgnoreCase("get")) {
            if (!(sender instanceof Player)) {
                String alertsString = getAlertsString(null, "", false);
                if (!alertsString.equals("")) {
                    sender.sendMessage(alertsString);
                } else {
                    sender.sendMessage("There are no messages.");
                }

                return true;
            }

            Player senderPlayer = (Player)sender;
            String alertsString = getAlertsString(senderPlayer.getUniqueId(), "", false);
            if (!alertsString.equals("")) {
                sender.sendMessage(alertsString);
                //updateAlertsRead(this, senderPlayer.getUniqueId());
            } else {
                sender.sendMessage("You have no unread messages. You can view already read messages with '/alerty get-all'");
            }

            return true;
        }
        else if (args[0].equalsIgnoreCase("get-all")) {
            String alertsString = getAlertsString(null, "", false);
            if (!alertsString.equals("")) {
                sender.sendMessage(alertsString);
            } else {
                sender.sendMessage("There are no messages.");
            }

            return true;
        }
        else if (args[0].equalsIgnoreCase("confirm-read") || args[0].equalsIgnoreCase("cr")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
                return true;
            }

            Player senderPlayer = (Player)sender;
            updateAlertsRead(this, senderPlayer.getUniqueId());

            sender.sendMessage("Your messages are now marked as read.");

            return true;
        }
        else if (args[0].equalsIgnoreCase("add")) {
            if ((sender instanceof Player)) {
                Player player = (Player) sender;
                if (!player.hasPermission("alerty.set")) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to execute this command!");
                    return true;
                }
            }

            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Invalid syntax: Insufficient parameter count.\nType '/help alerty' for help.");
                return true;
            }

            StringBuilder msg = new StringBuilder("");
            for (int i = 1; i < args.length; ++i) {
                msg.append(args[i]);
                if (i < args.length - 1) {
                    msg.append(" ");
                }
            }

            ArrayList<Alert> alerts = (ArrayList<Alert>)config.get("alerts");
            alerts.add(new Alert(msg.toString()));
            config.set("alerts", alerts);
            saveConfig();

            for (Player player : Bukkit.getOnlinePlayers()) {
                String playerMsg = Main.getAlertsString(player.getUniqueId(), "", true);

                if (!playerMsg.equalsIgnoreCase("")) {
                    player.sendMessage(playerMsg);
                    //Main.updateAlertsRead(this, player.getUniqueId());
                }
            }

            sender.sendMessage("Alert added and sent successfully.");

            return true;
        }
        else if (args[0].equalsIgnoreCase("update")) {
            if ((sender instanceof Player)) {
                Player player = (Player) sender;
                if (!player.hasPermission("alerty.set")) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to execute this command!");
                    return true;
                }
            }

            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Invalid syntax: Insufficient parameter count.\nType '/help alerty' for help.");
                return true;
            }

            int index;
            try {
                index = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid index: Index must be an Integer.\nType '/help alerty' for help.");
                return true;
            }

            ArrayList<Alert> alerts = (ArrayList<Alert>)config.get("alerts");
            if (index < 0 || index >= alerts.size()) {
                sender.sendMessage(ChatColor.RED + "Invalid index: Alert with specified index does not exist.\nType '/help alerty' for help.");
                return true;
            }

            //String msg = args[2];
            StringBuilder msg = new StringBuilder("");
            for (int i = 2; i < args.length; ++i) {
                msg.append(args[i]);
                if (i < args.length - 1) {
                    msg.append(" ");
                }
            }

            alerts.get(index).setMessage(msg.toString());
            config.set("alerts", alerts);
            saveConfig();

            sender.sendMessage("Alert updated successfully.");

            return true;
        }
        else if (args[0].equalsIgnoreCase("remove")) {
            if ((sender instanceof Player)) {
                Player player = (Player) sender;
                if (!player.hasPermission("alerty.set")) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to execute this command!");
                    return true;
                }
            }

            if (args.length != 2) {
                sender.sendMessage(ChatColor.RED + "Invalid syntax: Invalid parameter count.\nType '/help alerty' for help.");
                return true;
            }

            int index;
            try {
                index = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid index: Index must be an Integer.\nType '/help alerty' for help.");
                return true;
            }

            ArrayList<Alert> alerts = (ArrayList<Alert>)config.get("alerts");
            if (index < 0 || index >= alerts.size()) {
                sender.sendMessage(ChatColor.RED + "Invalid index: Alert with specified index does not exist.\nType '/help alerty' for help.");
                return true;
            }

            alerts.remove(index);
            config.set("alerts", alerts);
            saveConfig();

            sender.sendMessage("Alert removed successfully.");

            return true;
        }
        else {
            sender.sendMessage("Unknown command. Type '/help alerty' for help.");

            return false;
        }
    }

    /**
     * Gets the number of unread alerts for a player.
     * @param playerID The UUID of the player. Null to get count of all alerts.
     * @return Returns the number of unread alerts.
     */
    public static int getUnreadAlertsCount(UUID playerID) {
        ArrayList<Alert> alerts = (ArrayList<Alert>)config.get("alerts");

        if (playerID == null) {
            // Return number of alerts in total.
            return alerts.size();
        }

        int count = 0;
        for (Alert alert : alerts) {
            if (!alert.shownFor(playerID)) {
                ++count;
            }
        }

        return count;
    }

    /**
     * Gets all alerts for a specific player, or also read messages, ready to be sent.
     * @param playerID The UUID of the player to get alerts for. Null to get all alerts.
     * @param welcomeMsg The welcome message to be shown, if any.
     * @param askToConfirm True if the user should be prompted, to confirm the messages as read.
     * @return Returns the alerts as a string.
     */
    public static String getAlertsString(UUID playerID, String welcomeMsg, boolean askToConfirm) {
        ArrayList<Alert> alerts = (ArrayList<Alert>)config.get("alerts");
        int unreadAlertsCount = getUnreadAlertsCount(playerID);

        if (unreadAlertsCount == 0) {
            return "";
        }

        if (welcomeMsg == null) {
            welcomeMsg = "";
        }
        else if (!welcomeMsg.equals("")) {
            // Add new line after welcomeMsg
            welcomeMsg += "\n";
        }

        StringBuilder joinMessage = new StringBuilder();
        if (playerID != null) {
            joinMessage.append(ChatColor.AQUA + String.format("\n%sYou have (%d) unread messages:\n==============================\n", welcomeMsg, unreadAlertsCount));
        }
        else {
            joinMessage.append(ChatColor.AQUA + String.format("\n%sYou have (%d) messages in total:\n==============================\n", welcomeMsg, unreadAlertsCount));
        }

        for (int i = 0; i < alerts.size(); ++i) {
            Alert alert = alerts.get(i);

            if (alert.shownFor(playerID) && playerID != null) {
                continue;
            }

            joinMessage.append(ChatColor.LIGHT_PURPLE + String.format("\n[%d]: %s", i, alert.getMessage()));

            if (i < alerts.size() - 1) {
                // Not last one
                joinMessage.append("\n******************************");
            }
        }

        joinMessage.append(ChatColor.AQUA + "\n==============================\n");

        if (askToConfirm) {
            joinMessage.append(ChatColor.GOLD + "Confirm the messages as read with '/alerty confirm-read' or '/alerty cr'.");
        }

        return joinMessage.toString();
    }

    /**
     * Marks all alerts as read for a specific player.
     * @param plugin The instance of this Plugin.
     * @param playerID The UUID of the player.
     */
    public static void updateAlertsRead(Plugin plugin, UUID playerID) {
        ArrayList<Alert> alerts = (ArrayList<Alert>)config.get("alerts");
        for (Alert alert : alerts) {
            alert.setRead(playerID);
        }
        config.set("alerts", alerts);
        plugin.saveConfig();
    }
}
