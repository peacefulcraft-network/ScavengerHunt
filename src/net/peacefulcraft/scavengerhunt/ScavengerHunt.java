package net.peacefulcraft.scavengerhunt;

import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;
import net.peacefulcraft.scavengerhunt.commands.ScavengerHuntCommand;
import net.peacefulcraft.scavengerhunt.config.ScavangerHuntConfig;
import net.peacefulcraft.scavengerhunt.listeners.BlockInteractListener;

public class ScavengerHunt extends JavaPlugin {
    
    private static final String prefix = ChatColor.RED + "[" + ChatColor.BLUE + "ScavengerHunt" + ChatColor.RED + "]";
        public static String getPrefix() { return prefix;}

    public static ScavengerHunt sh;
        public static ScavengerHunt getPluginInstance() { return sh; }

    public static ScavangerHuntConfig cfg;
        public static ScavangerHuntConfig getSHConfig() { return cfg; }
        public static Boolean showDebug() { return cfg.getDebug(); }

    private static BlockInteractListener huntHandler;
        public static BlockInteractListener getHuntHandler() { return huntHandler; }

    public ScavengerHunt() {
        sh = this;
        cfg = new ScavangerHuntConfig(getConfig());

        huntHandler = new BlockInteractListener();
    }

    public void onEnable() {
        this.saveDefaultConfig();

        this.loadCommands();
        this.loadEventListeners();

        this.getLogger().info("ScavengerHunt has been enabled!");
    }

    public void onDisable() {
        huntHandler.save();

        this.saveConfig();
        this.getLogger().info("ScavengerHunt has been disabled!");
    }

    public void loadCommands() {
        this.getCommand("scavengerhunt").setExecutor(new ScavengerHuntCommand());
    }

    public void loadEventListeners() {
        getServer().getPluginManager().registerEvents(new BlockInteractListener(), this);
    }

    public static void logDebug(String debug) {
		if(showDebug()) {
			sh.getLogger().log(Level.INFO, debug);
		}
	}
	
	public static void logInfo(String info) {
		sh.getLogger().log(Level.INFO, info);
	}
	
	public static void logWarning(String warning) {
		sh.getLogger().log(Level.WARNING, warning);
	}
	
	public static void logSevere(String severe) {
		sh.getLogger().log(Level.SEVERE, severe);
	}
}

