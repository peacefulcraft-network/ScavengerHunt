package net.peacefulcraft.scavengerhunt.listeners;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;

import net.md_5.bungee.api.ChatColor;
import net.peacefulcraft.scavengerhunt.ScavengerHunt;
import net.peacefulcraft.scavengerhunt.io.HuntConfig;
import net.peacefulcraft.scavengerhunt.io.IOHandler;
import net.peacefulcraft.scavengerhunt.io.IOLoader;

public class BlockInteractListener implements Listener {
    
    private HashMap<UUID,List<Integer>> playerMap = new HashMap<>();

    private HashMap<Integer, Location> locMap = new HashMap<>();

    public BlockInteractListener() {
        loadPlayerData();
        loadPumpkins();
    }

    public void loadPlayerData() {

        this.playerMap.clear();
        this.locMap.clear();

        // Loading player data from file.
        IOLoader<ScavengerHunt> defaultPlayer = new IOLoader<ScavengerHunt>(ScavengerHunt.getPluginInstance(), "ExampleList.yml", "data");
        defaultPlayer = new IOLoader<ScavengerHunt>(ScavengerHunt.getPluginInstance(), "ExampleList.yml", "data");
        List<File> playerFiles = IOHandler.getAllFiles(defaultPlayer.getFile().getParent());
        List<IOLoader<ScavengerHunt>> playerLoaders = IOHandler.getSaveLoad(ScavengerHunt.getPluginInstance(), playerFiles, "data");

        // Name is UUID
        // Section: "Pumpkins" -> list of integers
        for(IOLoader<ScavengerHunt> s1 : playerLoaders) {
            String name = s1.getFile().getName();
            HuntConfig hc = new HuntConfig(name, s1.getFile(), s1.getCustomConfig());

            // Possible redudant conversion
            List<String> tempLis = hc.getStringList("Pumpkins");
            List<Integer> intLis = new ArrayList<>();
            for(String s : tempLis) {
                intLis.add(Integer.valueOf(s));
            }
            playerMap.put(UUID.fromString(name), intLis);
        }
    }

    public void save() {
        IOLoader<ScavengerHunt> defaultPlayer = new IOLoader<ScavengerHunt>(ScavengerHunt.getPluginInstance(), "ExampleList.yml", "data");
        defaultPlayer = new IOLoader<ScavengerHunt>(ScavengerHunt.getPluginInstance(), "ExampleList.yml", "data");
        List<File> playerFiles = IOHandler.getAllFiles(defaultPlayer.getFile().getParent());
        List<IOLoader<ScavengerHunt>> playerLoaders = IOHandler.getSaveLoad(ScavengerHunt.getPluginInstance(), playerFiles, "data");

        // Reading all player data currently in config
        for(UUID id : playerMap.keySet()) {
            // Fetching or creating new loader
            IOLoader<ScavengerHunt> loader = getLoader(playerLoaders, id);
            FileConfiguration config = loader.getCustomConfig();
            
            List<Integer> lis = playerMap.get(id);

            config.createSection("Pumpkins");
            config.set("Pumpkins", lis);

            try {
                config.save(loader.getFile());
            } catch(IOException ex) {
                ScavengerHunt.logSevere("Failed to save: " + loader.getFile());
                continue;
            }
        }

        this.playerMap.clear();
        ScavengerHunt.logInfo("ScavengerHunt player data successfully saved!");
    }

    /**
     * Returns existing loader if it exists. If not creates new one
     * @return File loader
     */
    private IOLoader<ScavengerHunt> getLoader(List<IOLoader<ScavengerHunt>> loaders, UUID id) {
        for(IOLoader<ScavengerHunt> s1 : loaders) {
            if(s1.getFile().getName().equalsIgnoreCase(String.valueOf(id))) {
                return s1;
            }
        } 
        return new IOLoader<ScavengerHunt>(ScavengerHunt.getPluginInstance(), String.valueOf(id) + ".yml", "data");
    }

    /**
     * Loads hardcoded location data
     */
    public void loadPumpkins() {
        // Format for adding pumpkin locations. Need world name and location coords
        //locMap.put(1, new Location(ScavengerHunt.getPluginInstance().getServer().getWorld(name), x, y, z))
    }

    /**
     * Checks if block is in locmap
     */
    private Integer checkPumpkin(Block block) {
        Location loc = block.getLocation();

        if(block.getType() == Material.PUMPKIN || block.getType() == Material.JACK_O_LANTERN) {
            int i = 1;
            for(Location l : locMap.values()) {
                if(l.getBlockX() == loc.getBlockX() && l.getBlockY() == loc.getBlockY() && l.getBlockZ() == loc.getBlockZ()) {
                    return i;
                }
                i++;
            }
        }
        return -1;
    }

    /**
     * Calculates player remaining pumpkins
     */
    public Integer getRemainingPumpkins(UUID id) {
        if(!playerMap.containsKey(id)) { return -1; }
        return locMap.keySet().size() - playerMap.get(id).size();
    }

    /**
     * Calculates total number of pumpkins
     */
    public Integer getPumpkinAmount() {
        return locMap.keySet().size();
    }

    @EventHandler
    public void onInteract(EntityInteractEvent e) {
        // If not player we don't care
        if(!(e.getEntity() instanceof Player)) { return; }
        Player p = (Player)e.getEntity();

        // If player hit one of the pumpkins
        int checked = checkPumpkin(e.getBlock());
        if(checked == -1) { return; }

        UUID id = p.getUniqueId();
        if(!playerMap.containsKey(id)) {
            playerMap.put(id, new ArrayList<>());
        }

        // If player has already found this pumpkin
        if(playerMap.get(id).contains(checked)) { 
            int remainder = getRemainingPumpkins(id);
            String m = " You have already found this pumpkin! You have " + String.valueOf(remainder) + " pumpkins left!";
            p.sendMessage(ScavengerHunt.getPrefix() + ChatColor.WHITE + m);
            return; 
        }

        playerMap.get(id).add(checked);
        int remainder = getRemainingPumpkins(id);
        String m = " Pumpkin found! You have " + String.valueOf(remainder) + " pumpkins left!";
        p.sendMessage(ScavengerHunt.getPrefix() + ChatColor.WHITE + m);
    }

}
