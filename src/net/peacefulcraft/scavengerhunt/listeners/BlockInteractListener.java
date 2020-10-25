package net.peacefulcraft.scavengerhunt.listeners;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import net.md_5.bungee.api.ChatColor;
import net.peacefulcraft.scavengerhunt.ScavengerHunt;
import net.peacefulcraft.scavengerhunt.io.HuntConfig;
import net.peacefulcraft.scavengerhunt.io.IOHandler;
import net.peacefulcraft.scavengerhunt.io.IOLoader;

public class BlockInteractListener implements Listener {
    
    private static HashMap<UUID,List<Integer>> playerMap = new HashMap<>();

    private HashMap<Integer, Location> locMap = new HashMap<>();

    public BlockInteractListener() {
        loadPlayerData();
        loadPumpkins();
    }

    /**
     * Safely reloads all data saved in config
     */
    public void reload() {
        save();
        loadPlayerData();
    }

    public void loadPlayerData() {

        playerMap.clear();
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

            // Skipping the example file
            if(name.equalsIgnoreCase("ExampleList") || name.equalsIgnoreCase("ExampleList.yml")) { continue; }

            //HuntConfig hc = new HuntConfig(name, s1.getFile(), s1.getCustomConfig());
            
            FileConfiguration config = s1.getCustomConfig();
            List<String> tempLis = config.getStringList("Pumpkins");

            // Possible redudant conversion
            List<Integer> intLis = new ArrayList<>();
            for(String s : tempLis) {
                ScavengerHunt.logDebug("Loading: " + s);
                intLis.add(Integer.valueOf(s));
            }

            //name = name.replace(".yml", "");
            playerMap.put(UUID.fromString(name.replace(".yml","")), intLis);
            ScavengerHunt.logDebug("Loaded: " + name + " ,with: " + String.valueOf(intLis));
        }
    }

    public void save() {
        IOLoader<ScavengerHunt> defaultPlayer = new IOLoader<ScavengerHunt>(ScavengerHunt.getPluginInstance(), "ExampleList.yml", "data");
        defaultPlayer = new IOLoader<ScavengerHunt>(ScavengerHunt.getPluginInstance(), "ExampleList.yml", "data");
        List<File> playerFiles = IOHandler.getAllFiles(defaultPlayer.getFile().getParent());
        List<IOLoader<ScavengerHunt>> playerLoaders = IOHandler.getSaveLoad(ScavengerHunt.getPluginInstance(), playerFiles, "data");

        ScavengerHunt.logDebug("Saving " + String.valueOf(playerMap.size()) + " entries.");

        // Reading all player data currently in config
        for(UUID id : playerMap.keySet()) {
            // Fetching or creating new loader
            IOLoader<ScavengerHunt> loader = getLoader(playerLoaders, id);
            FileConfiguration config = loader.getCustomConfig();
            
            List<Integer> lis = playerMap.get(id);
            List<String> sLis = new ArrayList<>();
            for(Integer i : lis) {
                sLis.add(String.valueOf(i));
            }

            config.createSection("Pumpkins");
            config.set("Pumpkins", sLis);

            try {
                config.save(loader.getFile());
                ScavengerHunt.logDebug("Saved: " + loader.getFile().getName());
            } catch(IOException ex) {
                ScavengerHunt.logSevere("Failed to save: " + loader.getFile());
                continue;
            }
        }

        playerMap.clear();
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
        World world = ScavengerHunt.getPluginInstance().getServer().getWorld("SwordCraftOnline");

        locMap.put(1, new Location(world, -7, 74, 356));
        locMap.put(2, new Location(world, -109, 74, 198));
        locMap.put(3, new Location(world, -106, 117, 312));
        locMap.put(4, new Location(world, -51, 75, 246));
        locMap.put(5, new Location(world, -28, 70, 144));
        locMap.put(6, new Location(world, -89, 65, 266));
        locMap.put(7, new Location(world, -7, 60, 88));
        locMap.put(8, new Location(world, -166, 145, 232));
        locMap.put(9, new Location(world, -9, 63, 246));
        locMap.put(10, new Location(world, -59, 65, 54));
    }

    /**
     * Checks if block is in locmap
     */
    private Integer checkPumpkin(Block block) {
        Location loc = block.getLocation();

        if(block.getType() == Material.PUMPKIN || block.getType() == Material.JACK_O_LANTERN || block.getType() == Material.CARVED_PUMPKIN) {
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

    public void printList(UUID id) {
        ScavengerHunt.logDebug(String.valueOf(playerMap.get(id)));
    }

    /**
     * Calculates total number of pumpkins
     */
    public Integer getPumpkinAmount() {
        return locMap.keySet().size();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) { return; }

        // If player hit one of the pumpkins
        int checked = checkPumpkin(e.getClickedBlock());
        if(checked == -1) { 
            ScavengerHunt.logDebug(p.getLocation().getWorld().getName());
            return; 
        }

        UUID id = p.getUniqueId();
        if(!playerMap.containsKey(id)) {
            playerMap.put(id, new ArrayList<>());
            ScavengerHunt.logDebug("Added to map. Map size: " + playerMap.size());
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
