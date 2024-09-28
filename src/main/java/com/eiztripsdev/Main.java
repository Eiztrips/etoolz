package com.eiztripsdev;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    public static final String PREFIX = ChatColor.translateAlternateColorCodes('&', "&l&0[&9E&dT&0]§r§a ");
    public static final String SERVERPREFIX = ChatColor.translateAlternateColorCodes('&', "&l&0[&9Test&dServer&0]§r§7 ");
    public static String textFormater(String arg) {return ChatColor.translateAlternateColorCodes('&', arg);}

    @Override
    public void onEnable() {
        ConfigMethod configMethod = new ConfigMethod(this);
        MainCommandExecutor mainCommandExecutor = new MainCommandExecutor(configMethod, this);
        EventListener eventListener = new EventListener(configMethod, mainCommandExecutor, this);

        String[] AttributeCommandExecutor = {"wspeed", "fspeed"};
        for (String command : AttributeCommandExecutor) {
            getCommand(command).setExecutor(new AttributeCommandExecutor());
        }

        String[] MainCommandExecutor = {"fly", "vanish", "v", "broadcast", "bc", "heal", "god", "gamemode", "gm", "kick", "ban", "unban", "inv", "inventory", "echest", "enderchest", "mute", "unmute"};
        for (String command : MainCommandExecutor) {
            getCommand(command).setExecutor(mainCommandExecutor);
        }
        
        getServer().getPluginManager().registerEvents(eventListener, this);
        getLogger().info("ETOOLZ ENABLED!");
    }


    @Override
    public void onDisable() {
        getLogger().info("ETOOLZ DISABLED!");
    }
}