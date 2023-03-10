package de.quele.countdowns;

import eu.hypetime.gameapi.GameAPI;
import eu.hypetime.gameapi.countdown.GameState;
import eu.hypetime.gameapi.countdown.GameStateManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

import java.util.concurrent.TimeUnit;

public class CountdownManager {

    private static LobbyCountdown lobby;
    private static IngameCountdown ingame;
    private static EndingCountdown ending;

    public CountdownManager() {
        lobby = new LobbyCountdown();
        ingame = new IngameCountdown();
        ending = new EndingCountdown();
        GameAPI.getInstance().getScheduledExecutorService().scheduleWithFixedDelay(() -> {
            int minPlayers = 2;
            if (GameStateManager.gameState == GameState.LOBBY) {
                if (!lobby.pause && lobby.time < lobby.originalTime && Bukkit.getOnlinePlayers().size() < minPlayers) {
                    lobby.pause(true);
                    Bukkit.broadcast(Component.text("§7Countdown konnte nicht gestartet werden, da nicht genügend Spieler online sind!"));
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        player.setLevel((int) lobby.time);
                        player.setExp(lobby.time / lobby.originalTime);
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1f, 0.1f);
                    });
                } else if ((!lobby.isActive  || lobby.pause) && lobby.time == lobby.originalTime && Bukkit.getOnlinePlayers().size() >= minPlayers) {
                    lobby.pause(false);
                    if (!lobby.isActive) {
                        lobby.start();
                        lobby.isActive = true;
                    }
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    public LobbyCountdown getLobbyCountdown() {
        return lobby;
    }

    public IngameCountdown getIngameCountdown() {
        return ingame;
    }

    public EndingCountdown getEndingCountdown() {
        return ending;
    }

}
