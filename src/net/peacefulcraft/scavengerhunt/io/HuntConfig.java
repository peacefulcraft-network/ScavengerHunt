package net.peacefulcraft.scavengerhunt.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import net.peacefulcraft.scavengerhunt.ScavengerHunt;

public class HuntConfig implements GenericConfig {
    private String configName;

    private File file;

    private FileConfiguration fc;

    public File getFile() {
        return this.file;
    }

    public HuntConfig(String name, FileConfiguration fc) {
        this.configName = name;
        this.fc = fc;
    }

    public HuntConfig(String name, File file, FileConfiguration fc) {
        this.configName = name;
        this.file = file;
        this.fc = fc;
    }

    public HuntConfig(String name, File file) {
        this.configName = name;
        this.file = file;
        this.fc = (FileConfiguration)new YamlConfiguration();
        this.fc.createSection(this.configName);
    }

    public void setKey(String key) {
        this.configName = key;
    }

    public String getKey() {
        return this.configName;
    }

    public FileConfiguration getFileConfiguration() {
        return this.fc;
    }

    public boolean isSet(String field) {
        return this.fc.isSet(this.configName + "." + field);
    }

    public void set(String key, Object value) {
        this.fc.set(this.configName + "." + key, value);
    }

    public void load() {
        this.fc = (FileConfiguration)YamlConfiguration.loadConfiguration(this.file);
    }

    public void save(){
        try {
            this.fc.save(this.file);
        } catch(IOException ex) {
            ScavengerHunt.logInfo("Error attempting to save " + this.configName);
        }
    }

    public HuntConfig getNestedConfig(String field) {
        return new HuntConfig(this.configName + "." + field, this.fc);
    }

    public Map<String, HuntConfig> getNestedConfigs(String key) {
        Map<String, HuntConfig> map = new HashMap<>();
        if(!isSet(key)) { return map; }
        for(String k : getKeys(key)) {
            map.put(k, new HuntConfig(this.configName + "." + key + "." + k, this.fc));
        }
        return map;
    }

    @Override
    public boolean getBoolean(String paramString) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getBoolean(String paramString, boolean paramBoolean) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getString(String paramString) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getString(String paramString1, String paramString2) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getInteger(String paramString) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getInteger(String paramString, int paramInt) {
        // TODO Auto-generated method stub
        return 0;
    }
    
    public double getDouble(String field) {
        return this.fc.getDouble(this.configName + "." + field);
    }

    public double getDouble(String field, double def) {
        return this.fc.getDouble(this.configName + "." + field, def);
    }  
    
    public List<String> getStringList(String field) {
        return this.fc.getStringList(this.configName + "." + field);
    }

    public List<String> getColorStringList(String field) {
        List<String> list = this.fc.getStringList(this.configName + "." + field);
        List<String> parsed = new ArrayList<>();
        if (list != null)
            for (String str : list)
                parsed.add(ChatColor.translateAlternateColorCodes('&', str)); 
        return parsed;  
    }

    public List<Map<?, ?>> getMapList(String field) {
        return this.fc.getMapList(this.configName + "." + field);
    }

    public List<?> getList(String field) {
        return this.fc.getList(this.configName + "." + field);
    }

    public List<Byte> getByteList(String field) {
        return this.fc.getByteList(this.configName + "." + field);
    }

    public ItemStack getItemStack(String field, String def) {
        return this.fc.getItemStack(this.configName + "." + field);
    }

    public boolean isConfigurationSection(String section) {
        return this.fc.isConfigurationSection(this.configName + "." + section);
    }

    public Set<String> getKeys(String section) {
        return this.fc.getConfigurationSection(this.configName + "." + section).getKeys(false);
    }

    public boolean isList(String section) {
        return this.fc.isList(this.configName + "." + section);
    }
}
