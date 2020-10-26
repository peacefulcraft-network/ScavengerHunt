package net.peacefulcraft.scavengerhunt;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;
import net.peacefulcraft.scavengerhunt.commands.ScavengerHuntCommand;
import net.peacefulcraft.scavengerhunt.config.ScavangerHuntConfig;
import net.peacefulcraft.scavengerhunt.io.PlayerDataHandler;
import net.peacefulcraft.scavengerhunt.listeners.PumpkinInteractListener;

public class ScavengerHunt extends JavaPlugin {
    
    private static final String prefix = ChatColor.RED + "[" + ChatColor.BLUE + "ScavengerHunt" + ChatColor.RED + "]" + ChatColor.GOLD;
        public static String getPrefix() { return prefix;}

    public static ScavengerHunt sh;
        public static ScavengerHunt getPluginInstance() { return sh; }

    public static ScavangerHuntConfig cfg;
        public static ScavangerHuntConfig getSHConfig() { return cfg; }
        public static Boolean showDebug() { return cfg.getDebug(); }

    private static HashMap<UUID, PlayerDataHandler> dataCache;
        public static HashMap<UUID, PlayerDataHandler> getDataCache() { return dataCache; }
        public static PlayerDataHandler loadPlayerData(Player p) {
            PlayerDataHandler playerData = null;
			try {
                playerData = new PlayerDataHandler(p.getUniqueId());
                ScavengerHunt.getDataCache().put(p.getUniqueId(), playerData);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
                ScavengerHunt.logSevere("Error loading player data for " + p.getDisplayName());
                p.sendMessage(ScavengerHunt.getPrefix() + "An error occured when attempting to claim this find. Please try again and report this issue to an administrator if it continues.");
            }
            return playerData;            
        }

    public ScavengerHunt() {
        sh = this;
        cfg = new ScavangerHuntConfig(getConfig());
		dataCache = new HashMap<UUID, PlayerDataHandler>();
    }

    public void onEnable() {
        this.saveDefaultConfig();

        this.loadCommands();
        this.loadEventListeners();

        this.getLogger().info("ScavengerHunt has been enabled!");
    }

    public void onDisable() {
        this.saveConfig();
        this.getLogger().info("ScavengerHunt has been disabled!");
    }

    public void loadCommands() {
        this.getCommand("scavengerhunt").setExecutor(new ScavengerHuntCommand());
    }

    public void loadEventListeners() {
        getServer().getPluginManager().registerEvents(new PumpkinInteractListener(), this);
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

