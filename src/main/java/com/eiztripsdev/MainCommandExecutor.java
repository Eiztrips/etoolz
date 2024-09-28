package com.eiztripsdev;

import java.util.Arrays;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class MainCommandExecutor implements CommandExecutor {
    private static final String PREFIX = Main.PREFIX;
    private static final String SERVERPREFIX = Main.SERVERPREFIX;
    private final ConfigMethod configMethod;
    private final Plugin plugin;
    
    public MainCommandExecutor(ConfigMethod configMethod, Plugin plugin) {
        this.configMethod = configMethod;
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
        }.runTaskTimer(plugin, 0L, 40L);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(PREFIX + ChatColor.RED + "эту команду может выполнить только игрок");
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
            
            case "mute":
                return MuteCommand(player, args);
            
            case "unmute":
                return UnmuteCommand(player, args);
                
            default:
                return false;
        }
    }

    private boolean FlyCommand(Player player, String[] args) {
        if (args.length == 0) {
            Player targetPlayer = player;
            if (targetPlayer.getAllowFlight()) {
                targetPlayer.setAllowFlight(false);
                targetPlayer.sendMessage(PREFIX + "Режим полёта выключен");
            } else {
                targetPlayer.setAllowFlight(true);
                targetPlayer.sendMessage(PREFIX + "Режим полёта включён");
            }
        } else if (Bukkit.getPlayer(args[0]) == null) {
            player.sendMessage(PREFIX + ChatColor.RED + "Игрок §l" + args[0] + "§r§c не найден");
            return false;
        } else {
            Player targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer.getAllowFlight()) {
                targetPlayer.setAllowFlight(false);
                player.sendMessage(PREFIX + "Режим полёта для игрока§l " + targetPlayer.getName() + "§r§a выключен");
                targetPlayer.sendMessage(PREFIX + "Режим полёта выключен");
            } else {
                targetPlayer.setAllowFlight(true);
                player.sendMessage(PREFIX + "Режим полёта для игрока§l " + targetPlayer.getName() + "§r§a включён");
                targetPlayer.sendMessage(PREFIX + "Режим полёта включён");
            }
        }
        return true;
    }

    private boolean VanishCommand(Player player) {
        if (player.hasMetadata("vanished")) {
            player.getLocation().getWorld().playSound(
                player.getLocation(),
                Sound.ENTITY_FIREWORK_ROCKET_LAUNCH,
                2.0F,
                1.0F
            );
            player.getWorld().spawnParticle(
                Particle.SPELL_MOB,
                player.getLocation(),
                100,
                1, 1, 1
            );
            for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                otherPlayer.showPlayer(player);
            }
            Bukkit.broadcastMessage(SERVERPREFIX + "§l" + player.getName() + "§r§7 зашёл на сервер");
            configMethod.unvanishPlayer(player.getName());
            player.removeMetadata("vanished", JavaPlugin.getProvidingPlugin(getClass()));
            player.sendTitle(Main.textFormater("&l&3Вы вышли из режима невидимки!"), Main.textFormater("&3Вас видят все"), 10, 40, 20 );
            startVanishActionBar(player);
        } else {
            player.getLocation().getWorld().playSound(
                player.getLocation(),
                Sound.ENTITY_FIREWORK_ROCKET_BLAST,
                2.0F,
                1.0F
            );
            player.getWorld().spawnParticle(
                Particle.SPELL_MOB,
                player.getLocation(),
                100,
                1, 1, 1
            );
            for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                if (!otherPlayer.equals(player)) {
                    otherPlayer.hidePlayer(player);
                }
            }
            configMethod.VanishPlayer(player.getName());
            Bukkit.broadcastMessage(SERVERPREFIX + "§l" + player.getName() + "§r§7 вышел с сервера");
            player.setMetadata("vanished", new FixedMetadataValue(JavaPlugin.getProvidingPlugin(getClass()), true));
            player.sendTitle(Main.textFormater("&l&3Вы вошли в режим невидимки!"), Main.textFormater("&3Вас никто не видит"), 10, 40, 20 );
            startVanishActionBar(player);
        }

        return true;
    }

    private boolean BroadcastCommand(Player player, String[] args) {
            if (args.length == 0) {
                player.sendMessage(PREFIX + "Использование > §l/broadcast <сообщение>");
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
            player.sendMessage(PREFIX + "Здоровье восстановлено");
            return true;
        } else {
            if (Bukkit.getPlayer(args[0]) == null) {
                player.sendMessage(PREFIX + ChatColor.RED + "Игрок §l" + args[0] + "§r§c не найден");
                return false;
            } else {
                Player targetPlayer = Bukkit.getPlayer(args[0]);
                targetPlayer.setHealth(targetPlayer.getMaxHealth());
                targetPlayer.sendMessage(PREFIX + "Вам восстановил здоровье игрок §l" + player.getName());
                player.sendMessage(PREFIX + "Вы восстановили здоровье игроку §l" + targetPlayer.getName());
                return true;
            }
        }
    }

    private boolean ToggleGodMode(Player player, String[] args) {
        if (args.length == 0) {
            if (player.isInvulnerable()) {
                player.setInvulnerable(false);
                player.sendMessage(PREFIX + "Режим бога выключен");
                return true;
            } else {
                player.setInvulnerable(true);
                player.sendMessage(PREFIX + "Режим бога включен");
                return true;
            }
        } else if (Bukkit.getPlayer(args[0]) == null) {
            player.sendMessage(PREFIX + ChatColor.RED + "Игрок §l" + args[0] + "§r§c не найден");
            return false;
        } else {
            Player targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer.isInvulnerable()) {
                targetPlayer.setInvulnerable(false);
                player.sendMessage(PREFIX + "Режим бога для игрока§l " + targetPlayer.getName() + "§r§a выключен");
                targetPlayer.sendMessage(PREFIX + "Режим бога выключен");
                return true;
            } else {
                targetPlayer.setInvulnerable(true);
                player.sendMessage(PREFIX + "Режим бога для игрока§l " + targetPlayer.getName() + "§r§a включен");
                targetPlayer.sendMessage(PREFIX + "Режим бога включен");
                return true;
            }
        }
    }

    private boolean GameModeSwitcherCommand(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(PREFIX + ChatColor.RED + Main.textFormater("Введите режим игры > §l0, 1, 2, 3"));
            return false;
        } else {
            if (args.length == 2) {
                if (!configMethod.isPlayerExist(args[1])) {
                    player.sendMessage(PREFIX + ChatColor.RED + "Игрок §l" + args[0] + "§r§a не найден");
                    return false; 
                }
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
                    player.sendMessage(PREFIX + ChatColor.RED + Main.textFormater("Введите существующий режим игры > §l0, 1, 2, 3"));
                    return false;
            }
        }
    }

    private boolean KickCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(PREFIX + ChatColor.RED + "Использование > §l/kick <имя игрока> <сообщение>");
            return false;
        } else {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null || !target.isOnline()) {
                player.sendMessage(PREFIX + ChatColor.RED + "Игрок §l" + args[0] + "§r§c не найден");
                return false;
            }

            StringBuilder kickMessage = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                kickMessage.append(args[i]).append(" ");
            }

            target.kickPlayer(SERVERPREFIX + ChatColor.RED + " \n\nКикнут по причине: \n" + kickMessage.toString().trim());
            Bukkit.broadcastMessage(SERVERPREFIX + ChatColor.LIGHT_PURPLE + "§l" + target.getName() + "§r" + ChatColor.LIGHT_PURPLE + " кикнут по причине: " + kickMessage.toString().trim());
            return true;
        }
    }

    private boolean BanCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(PREFIX + ChatColor.RED + "Использование > §l/ban <имя игрока> <время> <сообщение>");
            return false;
        } else if (!configMethod.isPlayerExist(args[0])) {
            player.sendMessage(PREFIX + ChatColor.RED + "Игрок §l" + args[0] + "§r§c не найден");
            return false;
        } else if (!Arrays.asList('s', 'm', 'h', 'd').contains(args[1].charAt(args[1].length() - 1)) || !args[1].substring(0, args[1].length() - 1).matches("-?\\d+(\\.\\d+)?")) {
            StringBuilder banMessage = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                banMessage.append(args[i]).append(" ");
            }
            configMethod.banPlayer(args[0], 86400*365*30);
            if (Bukkit.getPlayer(args[0]) != null && Bukkit.getPlayer(args[0]).isOnline()) {
                Player target = Bukkit.getPlayer(args[0]);
                target.getLocation().getWorld().playSound(
                    target.getLocation(),
                    Sound.ENTITY_BAT_DEATH,
                    3.0F,
                    1.0F
                );
                target.getWorld().spawnParticle(
                    Particle.VILLAGER_ANGRY,
                    target.getLocation(),
                    10,
                    1, 1, 1
                );
                target.kickPlayer(SERVERPREFIX + " перманентно забанен" + "\n§l" + banMessage.toString().trim());
            }
            Bukkit.broadcastMessage(SERVERPREFIX + ChatColor.LIGHT_PURPLE + "§l" + player.getName() + "§r"  + ChatColor.LIGHT_PURPLE + " перманентно забанил " + "§l" + args[0] + "§r"  + ChatColor.LIGHT_PURPLE + " за §l" + banMessage.toString().trim());
            Bukkit.getBanList(BanList.Type.NAME).addBan(args[0], SERVERPREFIX + ChatColor.RED + " \n\nЗабанен по причине: " + banMessage.toString().trim(), null, player.getName());
            return true;
        }

        String playerNameToban = args[0];

        int time = Integer.parseInt(args[1].substring(0, args[1].length() - 1));
        char timeType = args[1].charAt(args[1].length() - 1);
        String timeTypeStr = "Секунд";
        int timeForChat = time;
        switch (timeType) {
            case ('s'):
                if (time >= 31536000) {player.sendMessage(PREFIX + ChatColor.RED + "Для перманентного бана не указывайте время!"); return false;}
                break;
            case ('m'):
                if (time >= 525600) {player.sendMessage(PREFIX + ChatColor.RED + "Для перманентного бана не указывайте время!"); return false;}
                time = time*60;
                timeTypeStr = "Минут";
                break;
            case ('h'):
                if (time >= 8760) {player.sendMessage(PREFIX + ChatColor.RED + "Для перманентного бана не указывайте время!"); return false;}
                time = time*3600;
                timeTypeStr = "Часов";
                break;
            case ('d'):
                if (time >= 365) {player.sendMessage(PREFIX + ChatColor.RED + "Для перманентного бана не указывайте время!"); return false;}
                time = time*86400;
                timeTypeStr = "Дней";
                break;
        }

        StringBuilder banMessage = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            banMessage.append(args[i]).append(" ");
        }

        configMethod.banPlayer(playerNameToban, time);
        Bukkit.getBanList(BanList.Type.NAME).addBan(playerNameToban, "\n" + SERVERPREFIX + ChatColor.RED + " \n\nЗабанен по причине: " + "'" + banMessage.toString().trim() + "'", null, player.getName());
        if (Bukkit.getPlayer(args[0]) != null && Bukkit.getPlayer(args[0]).isOnline()) {
            Player target = Bukkit.getPlayer(args[0]);
            target.getLocation().getWorld().playSound(
                target.getLocation(),
                Sound.ENTITY_BAT_DEATH,
                3.0F,
                1.0F
            );
            target.getWorld().spawnParticle(
                Particle.VILLAGER_ANGRY,
                target.getLocation(),
                10,
                1, 1, 1
            );
            target.kickPlayer(SERVERPREFIX + ChatColor.RED + " \n\nЗабанен по причине: " + banMessage.toString().trim() + "\n\nДо разбана " + timeForChat + " " + timeTypeStr);
        }
        Bukkit.broadcastMessage(SERVERPREFIX + ChatColor.LIGHT_PURPLE + "§l" + playerNameToban + "§r"  + ChatColor.LIGHT_PURPLE + " забанен на " + "§l" + timeForChat + " " + timeTypeStr + "§r" + ChatColor.LIGHT_PURPLE + " за §l'" + banMessage.toString().trim() + "'");
        return true;
    }

    private boolean UnbanCommand(Player player, String[] args) {

        if (args.length != 1) {
            player.sendMessage(PREFIX + ChatColor.RED +"Использование > §l/unban <игрок>");
            return false;
        } else if (!configMethod.isPlayerExist(args[0])) {
            player.sendMessage(PREFIX + ChatColor.RED + "Игрок §l" + args[0] + "§r§c не найден");
            return false;
        }

        String playerNameToUnban = args[0];

        BanList banList = Bukkit.getServer().getBanList(BanList.Type.NAME);

        if (banList.isBanned(playerNameToUnban)) {
            configMethod.unbanPlayer(playerNameToUnban);
            banList.pardon(playerNameToUnban);
            Bukkit.broadcastMessage(SERVERPREFIX + "§d" + "Игрок " + "§l" + playerNameToUnban + "§r" + "§d" + " был разбанен");
        } else {
            player.sendMessage(PREFIX + ChatColor.RED + "§l" + playerNameToUnban + "§r§c не забанен");
        }

        return true;
    }

    private boolean InvSeeCommand(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage(PREFIX + ChatColor.RED + "Использование > §l/inv <имя_игрока>");
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null || !target.isOnline()) {
            player.sendMessage(PREFIX + ChatColor.RED + "Игрок §l" + args[0] + "§r§c не найден");
            return false;
        }

        player.openInventory(target.getInventory());
        player.sendMessage(PREFIX + "Открыт инвентарь игрока §l" + target.getName());
        return true;
    }

    private boolean EChestCommand(Player player, String[] args) {
        
        if (args.length != 1) {
            player.sendMessage(PREFIX + ChatColor.RED + "Использование > §l/echest <ник>");
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(PREFIX + ChatColor.RED + "Игрок §l" + args[0] + "§r§c не найден");
            return false;
        }

        Inventory enderChest = target.getEnderChest();
        player.openInventory(enderChest);
        player.sendMessage(PREFIX + "Открыт эндер-сундук игрока §l" + target.getName());
        return true;
    }

    private boolean MuteCommand(Player player, String[] args) {
        String target = args[0];
        if (!configMethod.isPlayerExist(target)) {
            player.sendMessage(PREFIX + ChatColor.RED + "Игрок §l" + target + "§r§c не найден");
            return false;
        } else if (args.length <= 1) {
            player.sendMessage(PREFIX + ChatColor.RED + "Использование > §l/mute <ник> <время> <причина> ");
            return false;
        } else if (configMethod.isPlayerMuted(target)) {
            player.sendMessage(PREFIX + ChatColor.RED + "Игрок §l" + target + "§r§c уже находится в муте");
            return false;
        } else if (!Arrays.asList('s', 'm', 'h', 'd').contains(args[1].charAt(args[1].length() - 1)) || !args[1].substring(0, args[1].length() - 1).matches("-?\\d+(\\.\\d+)?")) {
            StringBuilder reason = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                reason.append(args[i]).append(" ");
            }
            configMethod.mutePlayer(target, 86400*365*30);
            Bukkit.broadcastMessage(SERVERPREFIX + ChatColor.LIGHT_PURPLE + "§l" + player.getName() + "§r"  + ChatColor.LIGHT_PURPLE + " перманентно замутил " + "§l" + target + "§r"  + ChatColor.LIGHT_PURPLE + " за §l" + reason.toString().trim());
            return true;
        }

        
        int time = Integer.parseInt(args[1].substring(0, args[1].length() - 1));
        char timeType = args[1].charAt(args[1].length() - 1);

        String timeTypeStr = "Секунд";
        int timeForChat = time;

        switch (timeType) {
            case ('s'):
                if (time >= 31536000) {player.sendMessage(PREFIX + ChatColor.RED + "Для перманентного мута не указывайте время!"); return false;}
                break;
            case ('m'):
                if (time >= 525600) {player.sendMessage(PREFIX + ChatColor.RED + "Для перманентного мута не указывайте время!"); return false;}
                time = time*60;
                timeTypeStr = "Минут";
                break;
            case ('h'):
                if (time >= 8760) {player.sendMessage(PREFIX + ChatColor.RED + "Для перманентного мута не указывайте время!"); return false;}
                time = time*3600;
                timeTypeStr = "Часов";
                break;
            case ('d'):
                if (time >= 365) {player.sendMessage(PREFIX + ChatColor.RED + "Для перманентного мута не указывайте время!"); return false;}
                time = time*86400;
                timeTypeStr = "Дней";
                break;
        }
        
        StringBuilder reason = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            reason.append(args[i]).append(" ");
        }
        configMethod.mutePlayer(target, time);
        Bukkit.broadcastMessage(SERVERPREFIX + ChatColor.LIGHT_PURPLE + "§l" + player.getName() + "§r"  + ChatColor.LIGHT_PURPLE + " замутил " + "§l" + target + "§r"  + ChatColor.LIGHT_PURPLE + " на§l " + timeForChat + " " + timeTypeStr + " §r" + ChatColor.LIGHT_PURPLE + "за §l" + reason.toString().trim());
        return true;
    }

    private boolean UnmuteCommand(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage(PREFIX + ChatColor.RED + "Использование > §l/unmute <ник>");
            return false;
        }

        String target = args[0];
        if (!configMethod.isPlayerExist(target)) {
            player.sendMessage(PREFIX + ChatColor.RED + "Игрок §l" + args[0] + "§r§c не найден");
            return false;
        }

        if (configMethod.isPlayerMuted(target)) {
            configMethod.unmutePlayer(target);
            player.sendMessage(SERVERPREFIX + "§l" + target + "§r§a" + " был размьючен");
        } else {
            player.sendMessage(PREFIX + ChatColor.RED + "Игрок §l" + target + "§r§c не был замьючен");
        }
        return true;
    }
}