package net.peacefulcraft.scavengerhunt;

import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

import net.peacefulcraft.scavengerhunt.config.ScavangerHuntConfig;
import net.peacefulcraft.scavengerhunt.listeners.BlockInteractListener;

public class ScavengerHunt extends JavaPlugin {
    
    public static ScavengerHunt sh;
        public static ScavengerHunt getPluginInstance() { return sh; }

    public static ScavangerHuntConfig cfg;
        public static ScavangerHuntConfig getSHConfig() { return cfg; }
        public static Boolean showDebug() { return cfg.getDebug(); }

    private static BlockInteractListener huntHandler;

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
        this.getLogger().info("ScavenegerHunt has been disabled!");
    }

    public void loadCommands() {

    }

    public void loadEventListeners() {

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

