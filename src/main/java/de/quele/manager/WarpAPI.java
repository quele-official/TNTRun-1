package de.quele.manager;

import de.quele.TNTRun;
import de.quele.commands.utils.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Random;

public class WarpAPI {

    /*
     *     Urheberrechtshinweis
     *      Copyright © quele 2022
     *      Erstellt: 26.04.22, 12:25
     *
     *      Alle Inhalte dieses Quelltextes sind urheberrechtlich geschützt.
     *      Das Urheberrecht liegt, soweit nicht ausdrücklich anders gekennzeichnet,
     *      bei quele. Alle Rechte vorbehalten.
     *
     *      Jede Art der Vervielfältigung, Verbreitung, Vermietung, Verleihung,
     *      öffentlichen Zugänglichmachung oder andere Nutzung
     *      bedarf der ausdrücklichen, schriftlichen Zustimmung von quele.
     *
     */

    private static final Config locationConfig = new Config(TNTRun.getInstance().getDataFolder().getAbsolutePath(), "location.yml");
    public static Config location = new Config(TNTRun.getInstance().getDataFolder().getAbsolutePath(), "location.yml");

    public static void setSpawn(Player player, String number) {
        Location playerLoc = player.getLocation();
        location.c.set("loc.spawn." + number, playerLoc.add(0, 2, 0));
        try {
            location.c.save(location.f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendMessage(TNTRun.getInstance().getPrefix() + "Du hast §aerfolgreich §7den §6Spawnpunkt §7mit Nummer §6" + number + " §7gesetzt§8.");
    }

    public static Location getSpawn() {
        int randomSpawn = (int) ((Math.random() * (32 - 1)) + 1);
        if (!isExist(String.valueOf(randomSpawn))) {
            return new Location(Bukkit.getWorld("world"), 0, 0, 0, 0, 0);
        }

        Location returnLocation = location.c.getLocation("loc.spawn." + randomSpawn);
        if (returnLocation == null) {
            return new Location(Bukkit.getWorld("world"), 0, 0, 0, 0, 0);
        }
        while (returnLocation.getNearbyEntitiesByType(EntityType.PLAYER.getEntityClass(), 10).size() > 0) {
            randomSpawn = (int) ((Math.random() * (32 - 1)) + 1);
            returnLocation = location.c.getLocation("loc.spawn." + randomSpawn);
        }
        return returnLocation;
    }

    public static boolean isExist(String number) {
        return location.c.getLocation("loc.spawn." + number) != null;
    }

    public static Location getSpawnLocation(int spawnNumber) {
        Location spawnLocation = locationConfig.c.getLocation("loc.spawn." + spawnNumber);
        return spawnLocation != null ? spawnLocation : new Location(Bukkit.getWorld("world"), 0, 0, 0);
    }

    public static String getPlayerSpawnNumber(Player player) {
        // TODO: Implementiere eine Methode, um die Spawnpunktnummer des Spielers aus einer Datenbank oder einer Konfigurationsdatei abzurufen
        // Hier ist ein Beispielcode, der eine zufällige Spawnpunktnummer zuweist:
        int randomSpawnNumber = new Random().nextInt(2, 9);
        player.sendMessage("§7Du hast den Spawnpunkt §6" + (randomSpawnNumber - 1) + " §7zugewiesen bekommen!");

        return String.valueOf(randomSpawnNumber);
    }

    public void teleportPlayersToSpawn() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            String spawnNumber = getPlayerSpawnNumber(player);
            if (spawnNumber != null) {
                Location spawnLocation = WarpAPI.getSpawnLocation(Integer.parseInt(spawnNumber));
                if (spawnLocation != null) {
                    player.teleport(spawnLocation);
                } else {
                    player.sendMessage("§cDer Spawnpunkt konnte nicht gefunden werden!");
                }
            } else {
                player.sendMessage("§cDu hast keinen Spawnpunkt zugewiesen bekommen!");
            }
        }
    }

    public class SpawnManager {

        private static final Config locationConfig = new Config(TNTRun.getInstance().getDataFolder().getAbsolutePath(), "location.yml");

        public static Location getSpawnLocation(int spawnNumber) {
            Location spawnLocation = locationConfig.c.getLocation("loc.spawn." + spawnNumber);
            return spawnLocation != null ? spawnLocation : new Location(Bukkit.getWorld("world"), 0, 0, 0);
        }


        public static int getPlayerSpawnNumber(Player player) {
            int spawnNumber = 0;
            for (String key : locationConfig.c.getConfigurationSection("loc.spawn").getKeys(false)) {
                Location spawnLocation = locationConfig.c.getLocation("loc.spawn." + key);
                if (spawnLocation != null && spawnLocation.getBlockX() == player.getLocation().getBlockX()
                        && spawnLocation.getBlockY() == player.getLocation().getBlockY()
                        && spawnLocation.getBlockZ() == player.getLocation().getBlockZ()) {
                    spawnNumber = Integer.parseInt(key);
                    break;
                }
            }
            return spawnNumber;
        }
    }

}
