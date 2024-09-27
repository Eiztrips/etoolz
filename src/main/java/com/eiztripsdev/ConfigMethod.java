package com.eiztripsdev;

import java.io.File;
import java.io.IOException;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigMethod {
    private File file;
    public FileConfiguration config;

    public ConfigMethod(Main plugin) {
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

    public void VanishPlayer(String playerName) {
        config.set("VanishPlayers." + playerName, true);
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

    public void unvanishPlayer(String playerName) {
        config.set("VanishPlayers." + playerName, false);
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

    public boolean isPlayerVanished(String playerName) {
        reloadConfig();

        if (config.contains("VanishPlayers." + playerName)) {
            if (config.getBoolean("VanishPlayers." + playerName)) {
                return true;
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
}
