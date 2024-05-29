package de.quele.listeners;

import de.quele.TNTRun;
import de.quele.manager.WarpAPI;
import eu.hypetime.gameapi.GameAPI;
import eu.hypetime.gameapi.countdown.GameState;
import eu.hypetime.gameapi.countdown.GameStateManager;
import eu.hypetime.gameapi.team.TeamPlayer;
import eu.hypetime.gameapi.team.Teams;
import eu.hypetime.gameapi.utils.ItemBuilder;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.TNT;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class EventListener implements Listener {

    public static void registerSpec(Player player) {
        var teamP = TeamPlayer.getTeamPlayer(player);
        var team = teamP.getTeam();
        var gutils = GameAPI.getInstance().getUtils();
        GameAPI.getInstance().getUtils().respawn(player);
        if (GameStateManager.gameState == GameState.INGAME && TNTRun.getInstance().getCountdownManager().getIngameCountdown().isActive) {
            if (team != Teams.SPEC.getTeam()) {
                team.removePlayer(teamP);
                gutils.removeTeamPlayer(player);
                var specTeam = Teams.SPEC.getTeam();
                var newTeamP = new TeamPlayer(player, Teams.SPEC.getTeam(), "");
                specTeam.addPlayer(newTeamP);
                gutils.addTeamPlayer(player, newTeamP);
                player.teleport(player.getWorld().getSpawnLocation());
            }
            for (TeamPlayer value : GameAPI.getInstance().getUtils().getTeamPlayers().values()) {
                var player1 = value.getPlayer();
                var team1 = value.getTeam();
                if (team1 != Teams.SPEC.getTeam()) {
                    player1.hidePlayer(GameAPI.getInstance(), player);
                }
            }
            player.setGameMode(GameMode.SURVIVAL);
            player.setAllowFlight(true);
            player.setFlying(true);
            var teleporter = new ItemBuilder(Material.COMPASS).setName("§7» §6Teleporter").toItemStack();
            player.getInventory().setItem(4, teleporter);
            Location spawn = WarpAPI.getSpawnLocation(2);
            Bukkit.getScheduler().runTaskLater(TNTRun.getInstance(), () -> player.teleport(spawn), 2);
        }
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlaceBlock(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDropItem(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPickupItem(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onGetDamage(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        event.setCancelled(event.getCause() != EntityDamageEvent.DamageCause.VOID);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
        event.getWorld().setStorm(false);
        event.getWorld().setThundering(false);

    }


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        TeamPlayer teamPlayer = TeamPlayer.getTeamPlayer(player);
        if (TNTRun.getInstance().getCountdownManager().getIngameCountdown().isDestroyPhaseActive) {
            if (teamPlayer.getTeam() != Teams.SPEC.getTeam()) {
                removeBlocks(player);
                lastMove.put(player, System.currentTimeMillis());
            }
        }
        if (event.getPlayer().getLocation().getY() < 0) {
            registerSpec(event.getPlayer());
            return;
        }
    }

    // Cancel clicking buttons
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        event.setCancelled(true);
    }

    public static HashMap<Player, Long> lastMove = new HashMap<>();

    public static void removeBlocks(Player player) {
        Location location = player.getLocation().subtract(0, 1, 0);

        // Checks the player moves if yes then return
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {

                Block block = location.clone().add(x, 0, z).getBlock();

                if (block.getType() == Material.SAND || block.getType() == Material.GRAVEL) {
                    // Checks whether there is TNT under the gravel
                    Block blockBelow = block.getRelative(BlockFace.DOWN);
                    if (blockBelow.getType() == Material.TNT) {
                        Bukkit.getScheduler().runTaskLater(TNTRun.getInstance(), () -> block.setType(Material.GRAY_STAINED_GLASS), 5L);
                        Bukkit.getScheduler().runTaskLater(TNTRun.getInstance(), () -> blockBelow.setType(Material.GRAY_STAINED_GLASS), 5L);
                        Bukkit.getScheduler().runTaskLater(TNTRun.getInstance(), () -> block.setType(Material.AIR), 20L);
                        Bukkit.getScheduler().runTaskLater(TNTRun.getInstance(), () -> blockBelow.setType(Material.AIR), 20L);
                    }
                }
            }
        }
    }




    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        TeamPlayer teamPlayer = TeamPlayer.getTeamPlayer(player);
        event.setCancelled(true);
        if (teamPlayer.getTeam() == Teams.SPEC.getTeam()) {
            Bukkit.getOnlinePlayers().forEach(player1 -> {
                if (TeamPlayer.getTeamPlayer(player1).getTeam() == Teams.SPEC.getTeam()) {
                    player1.sendMessage(Component.text(teamPlayer.getTeam().getName() + " §7» §f" + player.getName() + " §8» §7").append(event.message()));
                }
            });
        } else {
            Bukkit.getOnlinePlayers().forEach(player1 -> {
                player1.sendMessage(Component.text("§7" + player.getName() + " §8» §7").append(event.message()));
            });
        }
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        registerSpec(player);
        event.quitMessage(null);
    }

}

