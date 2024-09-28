package com.eiztripsdev;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.lang.reflect.Field;

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
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String commandMessage = event.getMessage().toLowerCase();
        String[] commandParts = commandMessage.split(" ");
        String commandName = commandParts[0].substring(1);
        Command command = getCommand(commandName);
        if (command == null || !player.hasPermission(command.getPermission())) {
            event.setCancelled(true);
        }
    }

    private Command getCommand(String name) {
        try {
            Field commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getPluginManager());
            return commandMap.getCommand(name);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        BanList banList = Bukkit.getServer().getBanList(BanList.Type.NAME);
        Player player = event.getPlayer();
        if (!configMethod.isPlayerExist(event.getPlayer().getName())) {
            configMethod.joinPlayer(event.getPlayer().getName());
        }
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
                configMethod.unbanPlayer(player.getName());
            }
        }
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
        event.setQuitMessage(null);
        if (configMethod.isPlayerVanished(event.getPlayer().getName())) {
        } else if (configMethod.isPlayerBanned(event.getPlayer().getName())) {
            event.setQuitMessage(null); 
        } else {
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
            Bukkit.broadcastMessage(SERVERPREFIX + "§l" + playerName + "§r§7 вышел с сервера");
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
            Bukkit.broadcastMessage("§c§l§o" + playerName + "§r§c§o был убит игроком §l§o" + killerName + "§r§c§o");
        } else {
            Bukkit.broadcastMessage("§l§o§c" + playerName + "§r§o§c был убит");
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

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        event.setLeaveMessage("");
    }
}
