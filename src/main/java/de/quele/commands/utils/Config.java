package de.quele.commands.utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class Config {

    public File f;
    public YamlConfiguration c;
    public FileConfiguration fc;

    public Config(String DateiPfad, String DateiName) {
        this.f = new File(DateiPfad, DateiName);
        this.c = YamlConfiguration.loadConfiguration(this.f);
        this.fc = YamlConfiguration.loadConfiguration(this.f);
        if (!this.f.exists()) {
            try {
                this.f.createNewFile();
            } catch (IOException e) {

            }
        }
    }

    public Config setValue(String name, Object inhalt) {
        c.set(name, inhalt);
        save();
        return this;
    }


    @SuppressWarnings("unchecked")
    public ArrayList<UUID> getUUIDMap(String name) {
        return (ArrayList<UUID>) c.getList(name);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<String> getMap(String name) {
        return (ArrayList<String>) c.getList(name);
    }

    public void addDefault(String name, Object value) {
        load();
        if (c.get(name) == null) {
            setValue(name, value);
            save();
        }

    }

    public void copyDefaults() {
        c.options().copyDefaults();
    }

    public String getString(String name) {
        load();
        return c.getString(name);
    }

    public Config load() {
        try {
            c.load(f);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return this;
    }

    public Config save() {
        try {
            this.c.save(this.f);
        } catch (IOException ignored) {
        }
        return this;
    }
}