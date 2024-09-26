package com.eiztripsdev;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        EventListener eventListener = new EventListener(this);

        String[] AttributeCommandExecutor = {"wspeed", "fspeed"};
        for (String command : AttributeCommandExecutor) {
            getCommand(command).setExecutor(new AttributeCommandExecutor());
        }

        String[] MainCommandExecutor = {"fly", "vanish", "v", "broadcast", "bc", "heal", "god", "gamemode", "gm", "kick", "ban", "unban", "inv", "inventory", "echest", "enderchest", "mute", "unmute", "mutes"};
        for (String command : MainCommandExecutor) {
            getCommand(command).setExecutor(new MainCommandExecutor(eventListener));
        }
        
        getServer().getPluginManager().registerEvents(new EventListener(this), this);
        getLogger().info("ETOOLZ ENABLED!");
    }

    @Override
    public void onDisable() {
        getLogger().info("ETOOLZ DISABLED!");
    }
}