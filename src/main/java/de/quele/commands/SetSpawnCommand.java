package de.quele.commands;

import de.quele.TNTRun;
import de.quele.commands.utils.Constants;
import de.quele.manager.WarpAPI;
import org.bukkit.block.data.type.TNT;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("lobby.admin")) {
                if (args.length != 1) {
                    player.sendMessage(TNTRun.getInstance().getPrefix() + "§7Benutze §e/setspawn <Nummer>");
                    return true;
                }

                try {
                    int num = Integer.parseInt(args[0]);
                    String number = String.valueOf(num);
                    WarpAPI.setSpawn(player, number);

                } catch (NumberFormatException ex) {
                    player.sendMessage(TNTRun.getInstance().getPrefix() + "§7Du musst eine §eZahl §7eingeben§8.");
                }
            } else {
                player.sendMessage(TNTRun.getInstance().getPrefix() + "§7Dazu hast du §cnicht §7genug §6Rechte§8.");
            }
        }
        return false;
    }
}