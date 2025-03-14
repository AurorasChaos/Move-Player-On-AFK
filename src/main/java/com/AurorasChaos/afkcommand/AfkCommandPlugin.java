package com.AurorasChaos.afkcommand;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AfkCommandPlugin extends JavaPlugin implements Listener {

    // Stores the last activity timestamp for each player.
    private final Map<Player, Long> lastActivity = new HashMap<>();
    // Keeps track of players flagged as AFK.
    private final Set<Player> afkPlayers = new HashSet<>();

    // Configuration values
    private long afkTimeMs;
    private String afkCommand;
    private String messageAfk;
    private String messageBack;
    private String bypassPermission;
    private long checkInterval; // in ticks

    @Override
    public void onEnable() {
        // Save the default config file if it doesn't exist
        saveDefaultConfig();
        loadConfigValues();

        // Register event listeners
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("AfkCommandPlugin enabled!");

        // Schedule a repeating task to check for AFK players.
        // checkInterval is loaded from the config (default 60 seconds * 20 ticks).
        getServer().getScheduler().runTaskTimer(this, () -> {
            long now = System.currentTimeMillis();
            for (Player player : getServer().getOnlinePlayers()) {
                // Skip players with the bypass permission.
                if (player.hasPermission(bypassPermission)) {
                    continue;
                }
                long last = lastActivity.getOrDefault(player, now);
                // If player has been inactive for the specified duration and not already flagged...
                if ((now - last) >= afkTimeMs && !afkPlayers.contains(player)) {
                    afkPlayers.add(player);
                    // Replace {player} in the command with the player's name.
                    String commandToRun = afkCommand.replace("{player}", player.getName());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandToRun);
                    // Notify the player that they are now flagged as AFK.
                    player.sendMessage(messageAfk);
                    getLogger().info("Executed command for AFK player: " + player.getName());
                }
            }
        }, checkInterval, checkInterval);
    }

    @Override
    public void onDisable() {
        getLogger().info("AfkCommandPlugin disabled!");
    }

    // Loads configuration values from config.yml.
    private void loadConfigValues() {
        // Read the AFK timeout in minutes and convert to milliseconds.
        long timeoutMinutes = getConfig().getLong("afk-timeout", 5);
        afkTimeMs = timeoutMinutes * 60 * 1000;
        // Command to run when a player is flagged as AFK.
        // {player} will be replaced with the player's name.
        afkCommand = getConfig().getString("afk-command", "switchserver {player} PlayerReady-Lobby");
        // Message sent to the player when they are flagged as AFK.
        messageAfk = getConfig().getString("message-afk", "You are now flagged as AFK.");
        // Message sent to the player when they become active again.
        messageBack = getConfig().getString("message-back", "Welcome back!");
        // Permission node that allows bypassing the AFK check.
        bypassPermission = getConfig().getString("bypass-permission", "afk.bypass");
        // Check interval in seconds (default 60 seconds), converted to ticks (20 ticks = 1 second).
        long checkIntervalSeconds = getConfig().getLong("check-interval", 60);
        checkInterval = checkIntervalSeconds * 20;
    }

    // Event handler to update the player's last activity on movement.
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        // Avoid updating if the move is negligible.
        if (event.getFrom().distance(event.getTo()) == 0) return;
        // Skip if player has the bypass permission.
        if (player.hasPermission(bypassPermission)) return;
        lastActivity.put(player, System.currentTimeMillis());
        // If the player was flagged as AFK, remove them and notify.
        if (afkPlayers.contains(player)) {
            afkPlayers.remove(player);
            player.sendMessage(messageBack);
            getLogger().info(player.getName() + " is no longer AFK.");
        }
    }

    // Update activity on chat.
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission(bypassPermission)) return;
        lastActivity.put(player, System.currentTimeMillis());
        if (afkPlayers.contains(player)) {
            afkPlayers.remove(player);
            player.sendMessage(messageBack);
            getLogger().info(player.getName() + " is no longer AFK.");
        }
    }

    // Set last activity on player join.
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission(bypassPermission)) return;
        lastActivity.put(player, System.currentTimeMillis());
    }

    // Clean up when a player leaves.
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        lastActivity.remove(player);
        afkPlayers.remove(player);
    }

    // Command to reload the plugin configuration.
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("afkreload")) {
            // Allow console or players with the "afk.reload" permission to reload.
            if (sender.hasPermission("afk.reload") || !(sender instanceof Player)) {
                reloadConfig();
                loadConfigValues();
                sender.sendMessage("AFK configuration reloaded!");
                getLogger().info("AFK configuration reloaded by " + sender.getName());
                return true;
            } else {
                sender.sendMessage("You do not have permission to reload the AFK configuration.");
                return true;
            }
        }
        return false;
    }
}
