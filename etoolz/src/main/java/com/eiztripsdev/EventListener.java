package com.eiztripsdev;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class EventListener implements Listener {
    private static final String SERVERPREFIX = ChatColor.translateAlternateColorCodes('&', "&l&0[&9Test&dServer&0]§r§7 ");
    
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
        Player player = event.getEntity(); // Игрок, который умер
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
}
