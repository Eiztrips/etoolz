package com.eiztripsdev;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class EventListener implements Listener {
    private static final String PREFIX = Main.PREFIX;
    private static final String SERVERPREFIX = Main.SERVERPREFIX;

    private final ConfigMethod configMethod;
    private final MainCommandExecutor mainCommandExecutor;
    private final Plugin plugin;
    public EventListener(ConfigMethod configMethod, MainCommandExecutor mainCommandExecutor, Plugin plugin) {
        this.configMethod = configMethod;
        this.mainCommandExecutor = mainCommandExecutor;
        this.plugin = plugin;
    }

    private void startVanishActionBar(Player player) {
    new BukkitRunnable() {
        @Override
        public void run() {
                if (!player.hasMetadata("vanished") || !player.isOnline()) {
                    player.spigot().sendMessage(
                        net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                        net.md_5.bungee.api.chat.TextComponent.fromLegacyText("")
                    );
                    cancel();
                    return;
                }
            
                player.spigot().sendMessage(
                    net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                    net.md_5.bungee.api.chat.TextComponent.fromLegacyText(Main.textFormater("&l&eVanish - enabled!"))
                );
            }
        }.runTaskTimer(plugin, 0L, 40L); // Используем plugin для запуска задачи
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        BanList banList = Bukkit.getServer().getBanList(BanList.Type.NAME);
        Player player = event.getPlayer();
        if (banList.isBanned(player.getName())) {
            if (configMethod.isPlayerBanned(player.getName())) {
                LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(configMethod.config.getLong("bannedPlayers." + player.getName())),
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
        if (!configMethod.isPlayerVanished(event.getPlayer().getName())) {
            event.getPlayer().getLocation().getWorld().playSound(
                event.getPlayer().getLocation(),
                Sound.ENTITY_FIREWORK_ROCKET_LAUNCH,
                2.0F,
                1.0F
            );
            event.getPlayer().getWorld().spawnParticle(
                Particle.SPELL_MOB,
                event.getPlayer().getLocation(),
                100,
                1, 1, 1
            );
            String playerName = event.getPlayer().getName();
            event.setJoinMessage(null);
            Bukkit.broadcastMessage(SERVERPREFIX + "§l" + playerName + "§r§7 зашёл на сервер");
        } else {
            Player player = event.getPlayer();
            event.setJoinMessage(null);
            if (player.hasMetadata("vanished")) {
                for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                    otherPlayer.showPlayer(player);
                }
                player.removeMetadata("vanished", JavaPlugin.getProvidingPlugin(getClass()));
                for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                    otherPlayer.hidePlayer(player);
                }
                player.setMetadata("vanished", new FixedMetadataValue(JavaPlugin.getProvidingPlugin(getClass()), true));
                startVanishActionBar(player);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!configMethod.isPlayerBanned(event.getPlayer().getName()) && !configMethod.isPlayerVanished(event.getPlayer().getName())) {
            event.getPlayer().getLocation().getWorld().playSound(
                event.getPlayer().getLocation(),
                Sound.ENTITY_FIREWORK_ROCKET_BLAST,
                2.0F,
                1.0F
            );
            event.getPlayer().getWorld().spawnParticle(
                Particle.SPELL_MOB,
                event.getPlayer().getLocation(),
                100,
                1, 1, 1
            );
            String playerName = event.getPlayer().getName();
            event.setQuitMessage(null); 
            Bukkit.broadcastMessage(SERVERPREFIX + "§l" + playerName + "§r§7 вышел с сервера");
        } else if (configMethod.isPlayerVanished(event.getPlayer().getName())) {
            event.setQuitMessage(null);
        }
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
        if (configMethod.isPlayerMuted(player.getName())) {
            player.sendMessage(PREFIX + ChatColor.RED + "Вы замьючены и не можете отправлять сообщения.");
            event.setCancelled(true); 
        }
    }
}
