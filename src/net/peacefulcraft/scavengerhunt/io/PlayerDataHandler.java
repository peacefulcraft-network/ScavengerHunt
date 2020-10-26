package net.peacefulcraft.scavengerhunt.io;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import net.peacefulcraft.scavengerhunt.ScavengerHunt;

public class PlayerDataHandler {

	private UUID u;
	private File fRef;
	private FileConfiguration c;

	public PlayerDataHandler(UUID u) throws IOException, InvalidConfigurationException {
		// Clone the UUID to avoid memory leaking if the player logs off
		this.u = new UUID(u.getMostSignificantBits(), u.getLeastSignificantBits());

		this.loadConfig();
	}

	public void setProperty(String k, Object v) throws IOException {
		c.set(k, v);
		c.save(this.fRef);
	}

	public <T extends Object> T getProperty(String k, Class<T> type) {
		return type.cast(c.get(k));
	}

	public Integer numKeys() {
		return this.c.getKeys(true).size();
	}

	private void loadConfig() throws IOException, InvalidConfigurationException {
		this.fRef = new File(ScavengerHunt.sh.getDataFolder().getPath() + "/data/" + u.toString().toLowerCase().replaceAll("-", "") +  ".yml");
		this.c = new YamlConfiguration();
		if (this.fRef.exists()) {
			this.c.load(this.fRef);
		} else {
			this.c.save(this.fRef);
		} 
	}
}
