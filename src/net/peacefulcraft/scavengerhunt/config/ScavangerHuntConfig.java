package net.peacefulcraft.scavengerhunt.config;

import org.bukkit.configuration.file.FileConfiguration;

public class ScavangerHuntConfig {
    private FileConfiguration c;

    private Boolean debug;

    private String db_ip;
    private String db_name;
    private String db_user;
    private String db_password;

    public ScavangerHuntConfig(FileConfiguration c) {
        this.c = c;
        c.set("debug", true);
        debug = c.getBoolean("debug");

        db_ip = (String) c.getString("database.ip");
		db_name = (String) c.getString("database.name");
		db_user = (String) c.getString("database.user");
		db_password = (String) c.getString("database.secret");
    }

    public Boolean getDebug() {
        return debug;
    }
}
