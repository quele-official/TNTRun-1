package de.quele.countdowns;

import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import de.quele.TNTRun;
import de.quele.listeners.EventListener;
import de.quele.manager.WarpAPI;
import eu.hypetime.gameapi.GameAPI;
import eu.hypetime.gameapi.countdown.Countdown;
import eu.hypetime.gameapi.countdown.GameState;
import eu.hypetime.gameapi.countdown.GameStateManager;
import eu.hypetime.gameapi.team.TeamPlayer;
import eu.hypetime.gameapi.team.Teams;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class IngameCountdown extends Countdown {

    public boolean isDestroyPhaseActive = false;
    File file = new File("plugins/TNTRun/sound.nbs");
    Color[] colors = {Color.RED, Color.YELLOW, Color.BLUE, Color.WHITE, Color.AQUA, Color.GREEN, Color.ORANGE, Color.PURPLE, Color.BLACK, Color.FUCHSIA, Color.GRAY, Color.LIME, Color.MAROON, Color.NAVY, Color.OLIVE, Color.SILVER, Color.TEAL};

    public IngameCountdown() {
        super(600, "§aDas Spiel startet in §e%time% §aSekunden!", false, false, GameState.INGAME);
    }

    @Override
    public void start() {
        super.start();
        AtomicInteger time = new AtomicInteger(6);
        GameAPI.getInstance().getScheduledExecutorService().scheduleWithFixedDelay(() -> {
            time.getAndDecrement();
            switch (time.get()) {
                case 5, 4, 3, 2, 1 -> {
                    for (Player players : Bukkit.getOnlinePlayers()) {
                        players.sendTitlePart(TitlePart.TITLE, Component.text("§aDas Spiel startet in §e" + time.get() + " §aSekunden!"));
                        players.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(1), Duration.ofSeconds(1)));
                        players.playSound(players.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

                        // Zufällige Farbe auswählen
                        Color color = colors[new Random().nextInt(colors.length)];

                        // Neue Lederstiefel erstellen
                        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
                        LeatherArmorMeta meta = (LeatherArmorMeta) boots.getItemMeta();

                        // Farbe zu Stiefel hinzufügen
                        meta.setColor(color);
                        boots.setItemMeta(meta);
                        boots.addEnchantment(Enchantment.PROTECTION_FALL, 4);
                        players.getInventory().setBoots(boots);
                    }
                }

                case 0 -> {
                    isDestroyPhaseActive = true;
                    for (Player players : Bukkit.getOnlinePlayers()) {
                        EventListener.lastMove.put(players, System.currentTimeMillis());
                    }
                }
            }
            for (Player players : Bukkit.getOnlinePlayers()) {
                Color color = colors[new Random().nextInt(colors.length)];
                ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
                LeatherArmorMeta meta = (LeatherArmorMeta) boots.getItemMeta();
                meta.setColor(color);
                boots.setItemMeta(meta);

                players.getInventory().setBoots(boots);
            }
            if (isDestroyPhaseActive) {
                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (EventListener.lastMove.get(players) + 5000 < System.currentTimeMillis()) {
                        players.sendTitlePart(TitlePart.TITLE, Component.text("§cDu hast dich zu lange nicht bewegt!"));
                        players.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(1), Duration.ofSeconds(1)));
                        players.playSound(players.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                        players.playSound(players.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5, 20);
                        players.playSound(players.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                        EventListener.removeBlocks(players);
                    }
                }
            }


        }, 0, 1, TimeUnit.SECONDS);
        // set armor

        GameAPI.getInstance().getScheduledExecutorService().scheduleWithFixedDelay(() -> {
            if (isActive) {
                for (Player players : Bukkit.getOnlinePlayers()) {
                    TeamPlayer teamPlayer = TeamPlayer.getTeamPlayer(players);
                    if (teamPlayer.getTeam() == Teams.SPEC.getTeam()) {
                        players.setAllowFlight(true);
                        players.setFlying(true);
                    }
                    List<Player> ingamePlayers = new ArrayList<>();
                    for (TeamPlayer value : GameAPI.getInstance().getUtils().getTeamPlayers().values()) {
                        if (value.getTeam() != Teams.SPEC.getTeam()) {
                            ingamePlayers.add(value.getPlayer());
                        }
                    }
                    if (ingamePlayers.size() == 1) {
                        winner(ingamePlayers.get(0));
                        onFinish();
                        return;
                    }
                    if (ingamePlayers.size() == 0) {
                        noWinner();
                        onFinish();
                        return;
                    }
                }
            }
        }, 1000 * 5, 200, TimeUnit.MILLISECONDS);
    }


    public void winner(Player player) {
        Bukkit.getScheduler().runTaskLater(TNTRun.getInstance(), () -> {

            BossBar bossBar = Bukkit.createBossBar("§7Sieger§8: §a" + player.getName(), BarColor.GREEN, BarStyle.SOLID);
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.teleport(WarpAPI.getSpawnLocation(1));
                bossBar.addPlayer(onlinePlayer);
                onlinePlayer.sendTitlePart(TitlePart.TITLE, Component.text("§a" + player.getName() + " §7hat das Spiel gewonnen!"));
                onlinePlayer.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(1), Duration.ofSeconds(1)));
                onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            }
            player.sendTitlePart(TitlePart.TITLE, Component.text("§aDu hast das Spiel gewonnen!"));
            Bukkit.broadcast(Component.text("§a" + player.getName() + " §7hat das Spiel gewonnen!"));
            player.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(1), Duration.ofSeconds(1)));
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    Location loc = player.getLocation().clone().add(x, 0, z);
                    Firework firework = (Firework)
                            loc.getWorld().spawnEntity(loc, org.bukkit.entity.EntityType.FIREWORK);
                    FireworkMeta fireworkMeta = firework.getFireworkMeta();
                    fireworkMeta.addEffect(FireworkEffect.builder().flicker(true).withColor(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW)
                            .with(FireworkEffect.Type.STAR).trail(true).build());
                    fireworkMeta.setPower(1);
                    firework.setFireworkMeta(fireworkMeta);
                }
            }

        }, 20);
        Bukkit.getScheduler().runTaskLater(TNTRun.getInstance(), () -> {
           /* EnderDragon enderDragon = (EnderDragon) player.getLocation().getWorld().spawnEntity(player.getLocation(), org.bukkit.entity.EntityType.ENDER_DRAGON);
            enderDragon.customName(Component.text("§7Sieger§8: §a" + player.getName()));
            enderDragon.setCustomNameVisible(true);
            enderDragon.setInvulnerable(true);
            enderDragon.addPassenger(player);
            enderDragon.setPhase(EnderDragon.Phase.STRAFING);
            enderDragon.setAI(true);*/
        }, 40);
    }

    public void noWinner() {
        Bukkit.getScheduler().runTaskLater(TNTRun.getInstance(), () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.teleport(WarpAPI.getSpawnLocation(1));
                onlinePlayer.sendTitlePart(TitlePart.TITLE, Component.text("§aEs gibt keinen Gewinner!"));
                onlinePlayer.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(1), Duration.ofSeconds(1)));
                onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            }
        }, 30);
    }

    @Override
    public void onFinish() {
        this.isActive = false;
        this.isDestroyPhaseActive = false;
        Bukkit.getScheduler().runTaskLater(TNTRun.getInstance(), () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.spigot().respawn();
                onlinePlayer.getInventory().clear();
                onlinePlayer.setGameMode(GameMode.SURVIVAL);
                onlinePlayer.setAllowFlight(false);
                onlinePlayer.setFlying(false);
                onlinePlayer.setHealth(20);
                onlinePlayer.setFoodLevel(20);//einfach mal nicht auf respawn drückn
                onlinePlayer.setSaturation(20);
                onlinePlayer.sendTitlePart(TitlePart.TITLE, Component.text("§aDas Spiel ist vorbei!"));
                onlinePlayer.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(1), Duration.ofSeconds(1)));
                // onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                File file = new File("plugins/TNTRun/sound.nbs");
                var song = NBSDecoder.parse(file);
                var songplayer = new RadioSongPlayer(song);
                songplayer.setAutoDestroy(true);
                songplayer.addPlayer(onlinePlayer);
                songplayer.setPlaying(true);
            }
            for (Player toHide : Bukkit.getServer().getOnlinePlayers()) {
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    if (player != toHide) {
                        player.showPlayer(GameAPI.getInstance(), toHide);
                    }
                }
            }
        }, 20);
        GameStateManager.setGameState(GameState.ENDING);
        TNTRun.getInstance().getCountdownManager().getEndingCountdown().start();
    }
}
