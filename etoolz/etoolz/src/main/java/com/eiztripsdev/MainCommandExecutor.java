package com.eiztripsdev;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class MainCommandExecutor implements CommandExecutor {
    private static final String PREFIX = ChatColor.translateAlternateColorCodes('&', "&l&0[&9E&dT&0]§r§7 ");
    private static final String SERVERPREFIX = ChatColor.translateAlternateColorCodes('&', "&l&0[&9Test&dServer&0]§r§7 ");

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Эту команду может выполнить только игрок.");
            return false;
        }
        
        Player player = (Player) sender;

        
        switch (cmd.getName().toLowerCase()) {
            case "efly":
                return FlyCommand(player, args);
            case "evanish":
                return VanishCommand(player);
            case "ebc":
            case "ebroadcast":
                return BroadcastCommand(player, args);
            case "eheal":
                return HealCommand(player, args);
            case "egod":
                return ToggleGodMode(player, args);
            case "egm":
            case "egamemode":
                return GameModeSwitcherCommand(player, args);
            default:
                return false;
        }
    }

    private boolean FlyCommand(Player player, String[] args) {
        if (args.length == 0) {
            Player targetPlayer = player;
            if (targetPlayer.getAllowFlight()) {
                targetPlayer.setAllowFlight(false);
                targetPlayer.sendMessage(PREFIX + "Режим полёта выключен.");
            } else {
                targetPlayer.setAllowFlight(true);
                targetPlayer.sendMessage(PREFIX + "Режим полёта включён.");
            }
        } else {
            Player targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer.getAllowFlight()) {
                targetPlayer.setAllowFlight(false);
                player.sendMessage(PREFIX + "Режим полёта для игрока" + player.getName() + " выключен.");
                targetPlayer.sendMessage(PREFIX + "Режим полёта выключен.");
            } else {
                targetPlayer.setAllowFlight(true);
                player.sendMessage(PREFIX + "Режим полёта для игрока" + player.getName() + " включён.");
                targetPlayer.sendMessage(PREFIX + "Режим полёта включён.");
            }
        }
        return true;
    }

    private boolean VanishCommand(Player player) {
        if (player.hasMetadata("vanished")) {
            for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                otherPlayer.showPlayer(player);
            }
            Bukkit.broadcastMessage(SERVERPREFIX + "§l" + player.getName() + "§r§7 зашёл на сервер");
            player.removeMetadata("vanished", JavaPlugin.getProvidingPlugin(getClass()));
            player.sendMessage(PREFIX + "Вы больше не невидимы.");
        } else {
            for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                if (!otherPlayer.equals(player)) {
                    otherPlayer.hidePlayer(player);
                }
            }
            Bukkit.broadcastMessage(SERVERPREFIX + "§l" + player.getName() + "§r§7 вышел с сервера");
            player.setMetadata("vanished", new FixedMetadataValue(JavaPlugin.getProvidingPlugin(getClass()), true));
            player.sendMessage(PREFIX + "Вы теперь невидимы для других игроков.");
        }

        return true;
    }

    private boolean BroadcastCommand(Player player, String[] args) {
            if (args.length == 0) {
                player.sendMessage(PREFIX + "Использование: /broadcast <сообщение>");
                return false;
            } else {
                StringBuilder message = new StringBuilder();
                for (int i = 0; i < args.length; i++) {
                    message.append(args[i]).append(" ");
                }
                Bukkit.broadcastMessage(SERVERPREFIX + ChatColor.translateAlternateColorCodes('&', message.toString().trim()));
                return true;
            }
        }

    private boolean HealCommand(Player player, String[] args) {
        if (args.length == 0) {
            player.setHealth(player.getMaxHealth());
            return true;
        } else {
            Player targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer == null) {
                player.sendMessage(PREFIX + "Игрок с таким именем не найден.");
                return false;
            } else {
                targetPlayer.setHealth(targetPlayer.getMaxHealth());
                targetPlayer.sendMessage(PREFIX + "Вам восстановил здоровье игрок &l" + player.getName() + "&r&7.");
                player.sendMessage(PREFIX + "Вы восстановили здоровье игроку &l" + targetPlayer.getName() + "&r&7.");
                return true;
            }
        }
    }

    private boolean ToggleGodMode(Player player, String[] args) {
        if (args.length == 0) {
            if (player.isInvulnerable()) {
                player.setInvulnerable(false);
                player.sendMessage(PREFIX + "Режим бога выключен.");
                return true;
            } else {
                player.setInvulnerable(true);
                player.sendMessage(PREFIX + "Режим бога включен.");
                return true;
            }
        } else {
            Player targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer.isInvulnerable()) {
                targetPlayer.setInvulnerable(false);
                targetPlayer.sendMessage(PREFIX + "Режим бога выключен.");
                return true;
            } else {
                targetPlayer.setInvulnerable(true);
                targetPlayer.sendMessage(PREFIX + "Режим бога включен.");
                return true;
            }
        }
    }

    private boolean GameModeSwitcherCommand(Player player, String[] args) {
        if (args.length == 0) {
            return false;
        } else {
            if (args.length == 2) {
                player = Bukkit.getPlayer(args[1]);
            }
            switch (args[0]) {
                case "survival":
                case "s":
                case "0":
                    player.setGameMode(org.bukkit.GameMode.SURVIVAL);
                    player.sendMessage(PREFIX +"Режим игры изменён на выживание");
                    return true;
                case "creative":
                case "c":
                case "1":
                    player.setGameMode(org.bukkit.GameMode.CREATIVE);
                    player.sendMessage(PREFIX +"Режим игры изменён на творческий");
                    return true;
                case "adventure":
                case "a":
                case "2":
                    player.setGameMode(org.bukkit.GameMode.ADVENTURE);
                    player.sendMessage(PREFIX + "Режим игры изменён на приключенческий");
                    return true;
                case "spectator":
                case "sp":
                case "3":
                    player.setGameMode(org.bukkit.GameMode.SPECTATOR);
                    player.sendMessage(PREFIX + "Режим игры изменён на наблюдатель");
                    return true;
                default:
                    return false;
            }
        }
    }
}