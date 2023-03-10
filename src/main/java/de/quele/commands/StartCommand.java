package de.quele.commands;

import de.quele.TNTRun;
import de.quele.manager.WarpAPI;
import eu.hypetime.gameapi.countdown.GameState;
import eu.hypetime.gameapi.countdown.GameStateManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.data.type.TNT;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("start")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("tntrun.start")) {
                    if (GameStateManager.gameState == GameState.LOBBY) {
                        if(TNTRun.getInstance().getCountdownManager().getLobbyCountdown().time > 5) {
                            TNTRun.getInstance().getCountdownManager().getLobbyCountdown().time = 5;
                            Bukkit.broadcastMessage("§aDer Countdown wurde auf 5 Sekunden verkürzt!");
                        } else {
                            player.sendMessage("§cDer Countdown ist zu kurz um den zu verkürzen!");
                        }
                        return false;
                    }
                } else {
                    player.sendMessage("§cDazu hast du nicht genug Rechte!");
                    return false;
                }
            }
        }
        return false;
    }

}
