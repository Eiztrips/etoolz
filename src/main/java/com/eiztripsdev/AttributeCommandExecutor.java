package com.eiztripsdev;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AttributeCommandExecutor implements CommandExecutor {

    private static final String PREFIX = Main.PREFIX;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Эту команду может выполнить только игрок.");
            return false;
        }

        Player player = (Player) sender;

        switch (cmd.getName().toLowerCase()) {
            case "wspeed":
                return WalkSpeedCommand(player, args);

            case "fspeed":
                return FlySpeedCommand(player, args);

            default:
                return false;
        }
    }

    private boolean WalkSpeedCommand(Player player, String[] args) {
        try {
            if (args.length == 0) {
                player.sendMessage(PREFIX + ChatColor.RED + "Использование: /wspeed <скорость>");
                return false;
            }
            float speed = Float.parseFloat(args[0]);
            if (speed < 0 || speed > 10) {
                player.sendMessage(PREFIX + ChatColor.RED + "Скорость может быть указана от 1 до 10!");
                return false;
            } else {
                player.setWalkSpeed(speed/10);
                player.sendMessage(PREFIX + "Скорость ходьбы изменена на: " + speed);
                return true;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(PREFIX + ChatColor.RED + "Пожалуйста, введите допустимое число для скорости.");
            return false;
        }
    }

    private boolean FlySpeedCommand(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(PREFIX + ChatColor.RED + "Использование: /wspeed <скорость>");
            return false;
        }
        if (args.length == 0) {
            player.sendMessage(PREFIX + ChatColor.RED + "Скорость может быть указана от 1 до 10!");
            return false;
        } else {
            try {
                float speed = Float.parseFloat(args[0]);
                if (speed < 0 || speed > 10) {
                    player.sendMessage(PREFIX + ChatColor.RED + "Скорость может быть указана от 1 до 10!");
                    return false;
                } else {
                    player.setFlySpeed(speed/10);
                    player.sendMessage(PREFIX + "Скорость полёта изменена на: " + speed);
                    return true;
                }
            } catch (NumberFormatException e) {
                player.sendMessage(PREFIX + ChatColor.RED + "Пожалуйста, введите допустимое число для скорости.");
                return false;
            }
        }
    }
}