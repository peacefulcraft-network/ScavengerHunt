package net.peacefulcraft.scavengerhunt.listeners;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.peacefulcraft.scavengerhunt.ScavengerHunt;
import net.peacefulcraft.scavengerhunt.io.PlayerDataHandler;

public class PumpkinInteractListener implements Listener {

	private ArrayList<Location> pumpkinLocations;

	public PumpkinInteractListener() {
		World world = ScavengerHunt.getPluginInstance().getServer().getWorld("world");

		this.pumpkinLocations = new ArrayList<Location>(Arrays.asList(new Location(world, -7, 74, 356),
				new Location(world, -109, 74, 198), new Location(world, -106, 117, 312), new Location(world, -51, 75, 246),
				new Location(world, -28, 70, 144), new Location(world, -89, 65, 266), new Location(world, -7, 60, 88),
				new Location(world, -166, 145, 232), new Location(world, -9, 63, 246), new Location(world, -59, 65, 54)));
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent ev) {
		Player p = ev.getPlayer();
		if (ev.getAction() != Action.RIGHT_CLICK_BLOCK && ev.getAction() != Action.LEFT_CLICK_BLOCK) {
			return;
		}

		// Only fire once for the main hand, ignore the off-hand event
		if (!ev.getHand().equals(EquipmentSlot.HAND)) { return; } 

		// If player hit one of the pumpkins
		int checked = checkPumpkin(ev.getClickedBlock());
		if (checked == -1) {
			return;
		}

		// Go Async while we do file work
		Bukkit.getScheduler().runTaskAsynchronously(ScavengerHunt.sh, () -> {
			PlayerDataHandler playerData;
			if (ScavengerHunt.getDataCache().containsKey(p.getUniqueId())) {
				playerData = ScavengerHunt.getDataCache().get(p.getUniqueId());
			} else {
				playerData = ScavengerHunt.loadPlayerData(p);
			}

			Boolean found = playerData.getProperty(String.valueOf(checked), Boolean.class);
			if (found == null || found == false) {
				try {
					playerData.setProperty(String.valueOf(checked), true);
					if (playerData.numKeys() == pumpkinLocations.size()) {
						// Get back on main thread to do Bukkit API stuff
						Bukkit.getScheduler().runTask(ScavengerHunt.sh, () -> {
							ItemStack pumpkin = new ItemStack(Material.JACK_O_LANTERN, 1);
							ItemMeta pumpkinMeta = pumpkin.getItemMeta();
							pumpkinMeta.setDisplayName("Halloween 2020 Scavenger Hunt Winner");
							pumpkinMeta.setLore(new ArrayList<String>(Arrays.asList("Congratulations. You found all 10 of the hidden pumpkins", "in spawn during the 2020 Halloween Scavenger Hunt!")));
							pumpkin.setItemMeta(pumpkinMeta);
		
							// If space, add to inv. Otherwise drop it at the player's location
							if (p.getInventory().firstEmpty() == -1) {
								p.getWorld().dropItem(p.getLocation(), pumpkin);
							} else {
								p.getInventory().addItem(pumpkin);
							}

							p.sendMessage(ScavengerHunt.getPrefix() + "Congratulations! You've found all the hidden pumpkins.");
						});
					} else {
						p.sendMessage(ScavengerHunt.getPrefix() + "You've found a hidden pumpkin! You've found " + playerData.numKeys() + " out of " + pumpkinLocations.size() + " so far.");
					}
				} catch (IOException e) {
					e.printStackTrace();
					ScavengerHunt.logSevere("Error loading player data for " + p.getDisplayName());
					p.sendMessage(ScavengerHunt.getPrefix() + "An error occured when attempting to claim this find. Please try again and report this issue to an administrator if it continues.");
				}
			} else {
				p.sendMessage(ScavengerHunt.getPrefix() + "You've already found this pumpkin! You have " + (pumpkinLocations.size() - playerData.numKeys()) + " more to find.");
			}
		});
	}

	/**
	 * Checks if block is in locmap
	 */
	private Integer checkPumpkin(Block block) {
		Location loc = block.getLocation();

		if(block.getType() == Material.PUMPKIN || block.getType() == Material.JACK_O_LANTERN || block.getType() == Material.CARVED_PUMPKIN) {
				int i = 1;
				for(Location l : this.pumpkinLocations) {
						if(l.getBlockX() == loc.getBlockX() && l.getBlockY() == loc.getBlockY() && l.getBlockZ() == loc.getBlockZ()) {
								return i;
						}
						i++;
				}
		}
		return -1;
	}
}
