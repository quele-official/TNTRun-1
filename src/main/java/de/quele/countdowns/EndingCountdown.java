package de.quele.countdowns;

import de.quele.TNTRun;
import de.quele.manager.WarpAPI;
import eu.hypetime.gameapi.countdown.Countdown;
import eu.hypetime.gameapi.countdown.GameState;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class EndingCountdown extends Countdown {
    public EndingCountdown() {
        super(30, "§aDer Server startet in §6%time% §aSekunden neu!", true, true, GameState.ENDING);
    }

    @Override
    public void onFinish() {
        Bukkit.getScheduler().runTask(TNTRun.getInstance(), () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.kick(Component.text("§cDer Server startet neu!"));
            }
            Bukkit.shutdown();
        });
    }
}
