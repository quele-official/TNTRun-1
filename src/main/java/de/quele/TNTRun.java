package de.quele;

import de.quele.commands.SetSpawnCommand;
import de.quele.commands.StartCommand;
import de.quele.commands.utils.Config;
import de.quele.commands.utils.Constants;
import de.quele.countdowns.CountdownManager;
import de.quele.listeners.EventListener;
import de.quele.listeners.JoinListener;
import eu.hypetime.gameapi.GameAPI;
import eu.hypetime.gameapi.area.Area;
import eu.hypetime.gameapi.countdown.GameState;
import eu.hypetime.gameapi.countdown.GameStateManager;
import eu.hypetime.gameapi.format.Format;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;


public class TNTRun extends JavaPlugin {


    private static TNTRun instance;
    private Config config;
    private Constants constants;
    private CountdownManager countdownManager;

    public static TNTRun getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        config = new Config(getDataFolder().getAbsolutePath(), "config.yml");
        constants = new Constants(this);
        countdownManager = new CountdownManager();
        register();
        Bukkit.getConsoleSender().sendMessage(constants.getPrefix() + "TNTRun wurde aktiviert!");

    }

    @Override
    public void onDisable() {
        World world = Bukkit.getWorld("tntrun");
        unloadWorld(world);
        deleteWorld(world.getWorldFolder());
        File targetFolder = world.getWorldFolder();
        File sourceFolder = new File(getDataFolder().getAbsolutePath() + "/maptemplate/map");
        copyWorld(sourceFolder, targetFolder);
    }


    public void unloadWorld(World world) {
        if (world != null) {
            Bukkit.getServer().unloadWorld(world, true);
        }
    }

    public void deleteWorld(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            assert files != null;
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteWorld(file);
                } else {
                    file.delete();
                }
            }
        }
        try {
            Files.delete(Path.of(path.getPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void copyWorld(File source, File target) {
        try {
            ArrayList<String> ignore = new ArrayList<>(Arrays.asList("uid.dat", "session.dat"));
            if (!ignore.contains(source.getName())) {
                if (source.isDirectory()) {
                    if (!target.exists())
                        target.mkdirs();
                    String[] files = source.list();
                    assert files != null;
                    for (String file : files) {
                        File srcFile = new File(source, file);
                        File destFile = new File(target, file);
                        copyWorld(srcFile, destFile);
                    }
                } else {
                    InputStream in = new FileInputStream(source);
                    OutputStream out = new FileOutputStream(target);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0)
                        out.write(buffer, 0, length);
                    in.close();
                    out.close();
                }
            }
        } catch (IOException ignored) {

        }
    }

    private void register() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new EventListener(), this);
        pluginManager.registerEvents(new JoinListener(), this);
        getCommand("start").setExecutor(new StartCommand());
        getCommand("setspawn").setExecutor(new SetSpawnCommand());

        GameAPI gameAPI = GameAPI.getInstance();
        gameAPI.getUtils().setPrefix(constants.getPrefix());
        gameAPI.getUtils().setFormat(Format._8x1);
        Area area = gameAPI.getUtils().getAreaConfig().getAreaByName("TNTRun");
        gameAPI.getUtils().setArea(area);
        GameStateManager.setGameState(GameState.LOBBY);

    }

    public String getPrefix() {
        return constants.getPrefix();
    }

    public Config getConfigFile() {
        return config;
    }

    public CountdownManager getCountdownManager() {
        return countdownManager;
    }
}
