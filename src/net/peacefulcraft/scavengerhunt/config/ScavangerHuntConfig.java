package net.peacefulcraft.scavengerhunt.config;

import org.bukkit.configuration.file.FileConfiguration;

public class ScavangerHuntConfig {
    private FileConfiguration c;

    private Boolean debug;

    public ScavangerHuntConfig(FileConfiguration c) {
        this.c = c;
        c.set("debug", true);
        debug = c.getBoolean("debug");
    }

    public Boolean getDebug() {
        return debug;
    }
}
