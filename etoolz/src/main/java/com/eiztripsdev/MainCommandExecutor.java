package com.eiztripsdev;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
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
            case "fly":
                return FlyCommand(player, args);

            case "vanish":
            case "v":
                return VanishCommand(player);

            case "bc":
            case "broadcast":
                return BroadcastCommand(player, args);

            case "heal":
                return HealCommand(player, args);

            case "god":
                return ToggleGodMode(player, args);

            case "gm":
            case "gamemode":
                return GameModeSwitcherCommand(player, args);

            case "kick":
                return KickCommand(player, args);

            case "ban":
                return BanCommand(player, args);
            case "unban":
                return UnbanCommand(player, args);

            case "inv":
            case "inventory":
                return InvSeeCommand(player, args);
            
            case "echest":
            case "enderchest":
                return EChestCommand(player, args);
                
            default:
                return false;
        }
    }

    private boolean FlyCommand(Player player, String[] args) {
        if (args[0] == null) {
            player.sendMessage(PREFIX + ChatColor.RED + "Игрок " + args[0] + " не найден.");
            return false;
        }
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
                player.sendMessage(PREFIX + "Режим полёта для игрока§l " + targetPlayer.getName() + "§r§7 выключен.");
                targetPlayer.sendMessage(PREFIX + "Режим полёта выключен.");
            } else {
                targetPlayer.setAllowFlight(true);
                player.sendMessage(PREFIX + "Режим полёта для игрока§l " + targetPlayer.getName() + "§r§7 включён.");
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
        if (args[0] == null) {
            player.sendMessage(PREFIX + ChatColor.RED + "Игрок " + args[0] + " не найден.");
            return false;
        }
        if (args.length == 0) {
            player.setHealth(player.getMaxHealth());
            return true;
        } else {
            Player targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer == null) {
                player.sendMessage(PREFIX + ChatColor.RED + "Игрок с таким именем не найден.");
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
                player.sendMessage(PREFIX + "Режим бога для игрока§l " + targetPlayer.getName() + "§r§7 выключен.");
                targetPlayer.sendMessage(PREFIX + "Режим бога выключен.");
                return true;
            } else {
                targetPlayer.setInvulnerable(true);
                player.sendMessage(PREFIX + "Режим бога для игрока§l " + targetPlayer.getName() + "§r§7 включен.");
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

    private boolean KickCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(PREFIX + ChatColor.RED + "Использование: /kick <имя игрока> <сообщение>");
            return false;
        } else {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null || !target.isOnline()) {
                player.sendMessage(PREFIX + ChatColor.RED + "Игрок " + args[0] + " не найден.");
                return false;
            }

            StringBuilder kickMessage = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                kickMessage.append(args[i]).append(" ");
            }

            target.kickPlayer(SERVERPREFIX + ChatColor.RED + " \n\nКикнут по причине: " + kickMessage.toString().trim());
            Bukkit.broadcastMessage(SERVERPREFIX + ChatColor.LIGHT_PURPLE + " §l" + target.getName() + "§r" + ChatColor.LIGHT_PURPLE + " кикнут по причине: " + kickMessage.toString().trim());
            return true;
        }
    }

    private boolean BanCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(PREFIX + ChatColor.RED + "Использование: /ban <имя игрока> <сообщение>");
            return false;
        } else {
            String playerNameToban = args[0];
            StringBuilder banMessage = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                banMessage.append(args[i]).append(" ");
            }

            Bukkit.getBanList(BanList.Type.NAME).addBan(playerNameToban, SERVERPREFIX + ChatColor.RED + " \n\nЗабанен по причине: " + banMessage.toString().trim(), null, player.getName());

            if (Bukkit.getPlayer(args[0]) != null && Bukkit.getPlayer(args[0]).isOnline()) {
                Player target = Bukkit.getPlayer(args[0]);
                target.kickPlayer(SERVERPREFIX + ChatColor.RED + " \n\nЗабанен по причине: " + banMessage.toString().trim());
            }
            
            Bukkit.broadcastMessage(SERVERPREFIX + ChatColor.LIGHT_PURPLE + "§l" + playerNameToban + "§r"  + ChatColor.LIGHT_PURPLE + " забанен по причине: " + banMessage.toString().trim());
            return true;
        }
    }

    private boolean UnbanCommand(Player player, String[] args) {

        if (args.length != 1) {
            player.sendMessage(PREFIX + ChatColor.RED +"Использование: /unban <игрок>");
            return false;
        }

        String playerNameToUnban = args[0];

        BanList banList = Bukkit.getServer().getBanList(BanList.Type.NAME);

        if (banList.isBanned(playerNameToUnban)) {
            banList.pardon(playerNameToUnban);
            Bukkit.broadcastMessage(SERVERPREFIX + "§d" + "Игрок " + "§l" + playerNameToUnban + "§r" + "§d" + " был разбанен.");
        } else {
            player.sendMessage(PREFIX + ChatColor.RED + playerNameToUnban + " не был забанен");
        }

        return true;
    }

    private boolean InvSeeCommand(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage(PREFIX + ChatColor.RED + "Использование: /inv <имя_игрока>");
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null || !target.isOnline()) {
            player.sendMessage(PREFIX + ChatColor.RED + "Игрок " + args[0] + " не найден.");
            return false;
        }

        player.openInventory(target.getInventory());
        player.sendMessage(PREFIX + "Открыт инвентарь игрока " + target.getName());
        return true;
    }

    private boolean EChestCommand(Player player, String[] args) {
        
        if (args.length != 1) {
            player.sendMessage(PREFIX + ChatColor.RED + "Использование: /echest <ник>");
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(PREFIX + ChatColor.RED + "Игрок " + args[0] + " не найден.");
            return false;
        }

        Inventory enderChest = target.getEnderChest();
        player.openInventory(enderChest);
        player.sendMessage(PREFIX + "Открыт эндер-сундук игрока " + target.getName());
        return true;
    }
}