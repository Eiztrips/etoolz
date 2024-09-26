package com.eiztripsdev;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class EventListener implements Listener {
    private static final String PREFIX = ChatColor.translateAlternateColorCodes('&', "&l&0[&9E&dT&0]§r§7 ");
    private static final String SERVERPREFIX = ChatColor.translateAlternateColorCodes('&', "&l&0[&9Test&dServer&0]§r§7 ");
    private final File file;
    private FileConfiguration config;

    public EventListener(Main plugin) {
        file = new File(plugin.getDataFolder(), "Players.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public void mutePlayer(String playerName, int muteDurationSeconds) {
        long muteEndTime = System.currentTimeMillis() + (muteDurationSeconds * 1000L);
        config.set("mutedPlayers." + playerName, muteEndTime);
        saveConfig();
    }

    public void banPlayer(String playerName, int muteDurationSeconds) {
        long banEndTime = System.currentTimeMillis() + (muteDurationSeconds * 1000L);
        config.set("bannedPlayers." + playerName, banEndTime);
        saveConfig();
    }

    public void unmutePlayer(String playerName) {
        config.set("mutedPlayers." + playerName, null);
        saveConfig();
    }

    public void unbanPlayer(String playerName) {
        Bukkit.getServer().getBanList(BanList.Type.NAME).pardon(playerName);
        config.set("bannedPlayers." + playerName, null);
        saveConfig();
    }

    public boolean isPlayerMuted(String playerName) {
        reloadConfig();

        if (config.contains("mutedPlayers." + playerName)) {
            long muteEndTime = config.getLong("mutedPlayers." + playerName);

            if (System.currentTimeMillis() < muteEndTime) {
                return true;
            } else {
                unmutePlayer(playerName);
                return false;
            }
        }
        return false;
    }

    public boolean isPlayerBanned(String playerName) {
        reloadConfig();

        if (config.contains("bannedPlayers." + playerName)) {
            long banEndTime = config.getLong("bannedPlayers." + playerName);

            if (System.currentTimeMillis() < banEndTime) {
                return true;
            } else {
                unbanPlayer(playerName);
                return false;
            }
        }
        return false;
    }

    public void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(file); 
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        BanList banList = Bukkit.getServer().getBanList(BanList.Type.NAME);
        Player player = event.getPlayer();
        if (banList.isBanned(player.getName())) {
            if (isPlayerBanned(player.getName())) {
                LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(config.getLong("bannedPlayers." + player.getName())),
                ZoneId.systemDefault()
                );
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy | HH:mm");
                String formattedDate = dateTime.format(formatter);
                event.setKickMessage(event.getKickMessage().substring(40) + "\n\n Время разбана: " + formattedDate);
            } else {
                event.setKickMessage(SERVERPREFIX + " Пожалуйста, перезайдите!");
            }
        };
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();
        event.setJoinMessage(null);
        Bukkit.broadcastMessage(SERVERPREFIX + "§l" + playerName + "§r§7 зашёл на сервер");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String playerName = event.getPlayer().getName();
        event.setQuitMessage(null); 
        Bukkit.broadcastMessage(SERVERPREFIX + "§l" + playerName + "§r§7 вышел с сервера");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        String playerName = player.getName();

        event.setDeathMessage(null);

        String killerName = "Неизвестный";
        if (player.getKiller() != null) {
            killerName = player.getKiller().getName();
            Bukkit.broadcastMessage(SERVERPREFIX + "§l" + playerName + "§r§7 был убит игроком §l" + killerName + "§r§7!");
        } else {
            Bukkit.broadcastMessage(SERVERPREFIX + "§l" + playerName + "§r§7 был убит");
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (player.isInvulnerable()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (isPlayerMuted(player.getName())) {
            player.sendMessage(PREFIX + ChatColor.RED + "Вы замьючены и не можете отправлять сообщения.");
            event.setCancelled(true); 
        }
    }
}
