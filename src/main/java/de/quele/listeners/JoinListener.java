package de.quele.listeners;

import de.quele.TNTRun;
import de.quele.manager.WarpAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Location spawnLocation = WarpAPI.getSpawnLocation(1);
        if (!player.hasPlayedBefore()) {
            Bukkit.getScheduler().runTaskLater(TNTRun.getInstance(), () -> player.teleport(spawnLocation), 2);

        } else {
            player.teleport(spawnLocation);
        }
        event.joinMessage(Component.text("§a" + player.getName() + " §7hat das Spiel betreten! §8(§a" + Bukkit.getOnlinePlayers().size() + "§8/§a8§8)"));
        player.setGameMode(GameMode.SURVIVAL);
        event.getPlayer().getInventory().clear();
        event.getPlayer().getInventory().setArmorContents(null);
        player.setHealth(20);
        var lobby = TNTRun.getInstance().getCountdownManager().getLobbyCountdown();
        player.setLevel((int) lobby.time);
        player.setExp(lobby.time / lobby.originalTime);



    }

}
