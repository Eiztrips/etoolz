package com.eiztripsdev;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        String[] AttributeCommandExecutor = {"ewspeed", "efspeed"};
        for (String command : AttributeCommandExecutor) {
            getCommand(command).setExecutor(new AttributeCommandExecutor());
        }

        String[] MainCommandExecutor = {"efly", "evanish", "ev", "ebroadcast", "ebc", "eheal", "egod", "egamemode", "egm"};
        for (String command : MainCommandExecutor) {
            getCommand(command).setExecutor(new MainCommandExecutor());
        }
        
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getLogger().info("ETOOLZ ENABLED!");
    }

    @Override
    public void onDisable() {
        getLogger().info("ETOOLZ DISABLED!");
    }
}