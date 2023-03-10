package de.quele.listeners;

import de.quele.TNTRun;
import de.quele.manager.WarpAPI;
import eu.hypetime.gameapi.GameAPI;
import eu.hypetime.gameapi.countdown.GameState;
import eu.hypetime.gameapi.countdown.GameStateManager;
import eu.hypetime.gameapi.team.TeamPlayer;
import eu.hypetime.gameapi.team.Teams;
import eu.hypetime.gameapi.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.TNT;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class EventListener implements Listener {

    public static void registerSpec(Player player) {
        GameAPI.getInstance().getUtils().respawn(player);
        if (GameStateManager.gameState == GameState.INGAME && TNTRun.getInstance().getCountdownManager().getIngameCountdown().isActive) {
            var teamP = TeamPlayer.getTeamPlayer(player);
            var team = teamP.getTeam();
            var gutils = GameAPI.getInstance().getUtils();
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
                    player.hidePlayer(GameAPI.getInstance(), player1);
                }
            }
            player.setGameMode(GameMode.SURVIVAL);
            player.setAllowFlight(true);
            player.setFlying(true);
            var teleporter = new ItemBuilder(Material.COMPASS).setName("§7» §6Teleporter").toItemStack();
            player.getInventory().setItem(4, teleporter);
            Location spawn = WarpAPI.getSpawnLocation(2);
            player.teleport(spawn);
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
        registerSpec(event.getPlayer());
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    //check if player not moving

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(TNTRun.getInstance().getCountdownManager().getIngameCountdown().isDestroyPhaseActive) {
            Player player = event.getPlayer();
            removeBlocks(player);
            lastMove.put(player, System.currentTimeMillis());
        }
    }

    // cancel clicking buttons
    @EventHandler//Brauchen wir interact
    public void onInteract(PlayerInteractEvent event) {
        event.setCancelled(true);
    }

    public static HashMap<Player, Long> lastMove = new HashMap<>();

    public static void removeBlocks(Player player) {
        Location location = player.getLocation().subtract(0, 1, 0);

        // überprüfe ob der Spieler sich bewegt wenn ja dann return
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {

                Block block = location.clone().add(x, 0, z).getBlock();

                if (block.getType() == Material.SAND || block.getType() == Material.GRAVEL) {
                    // Überprüfen, ob unter dem Gravel TNT ist
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


}

