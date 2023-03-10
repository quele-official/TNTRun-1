package de.quele.countdowns;

import de.quele.TNTRun;
import de.quele.manager.WarpAPI;
import eu.hypetime.gameapi.countdown.Countdown;
import eu.hypetime.gameapi.countdown.GameState;
import eu.hypetime.gameapi.countdown.GameStateManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class LobbyCountdown extends Countdown {
    public LobbyCountdown() {
        super(15, "§aDas Spiel startet in §e%time% §aSekunden!", true, true, GameState.LOBBY);
    }

    @Override
    public void onFinish() {
        Bukkit.getScheduler().runTask(TNTRun.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.setHealth(20);
                player.setFoodLevel(20);
                player.setFireTicks(0);
                player.setExp(0);
                player.setLevel(0);
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
                String spawnNumber = WarpAPI.getPlayerSpawnNumber(player);
                Location spawnLocation = WarpAPI.getSpawnLocation(Integer.parseInt(spawnNumber));
                player.teleport(spawnLocation);
                Sound sound = Sound.ENTITY_PLAYER_LEVELUP;
                player.playSound(player.getLocation(), sound, 1, 1);
            }
        });
        GameStateManager.setGameState(GameState.INGAME);
        TNTRun.getInstance().getCountdownManager().getIngameCountdown().start();
    }
}
