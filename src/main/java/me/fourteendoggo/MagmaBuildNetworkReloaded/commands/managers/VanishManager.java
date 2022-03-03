package me.fourteendoggo.MagmaBuildNetworkReloaded.commands.managers;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Lang;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VanishManager {
    private final MBNPlugin plugin;
    private final Set<UUID> vanished;
    private final BossBar bossBar;
    private final NamespacedKey namespacedKey;

    public VanishManager(MBNPlugin plugin) {
        this.plugin = plugin;
        vanished = new HashSet<>();
        bossBar = Bukkit.createBossBar("Vanished", BarColor.BLUE, BarStyle.SOLID);
        bossBar.setProgress(1);
        namespacedKey = new NamespacedKey(plugin, "vanished");
        Bukkit.getPluginManager().registerEvents(new VanishListener(), plugin);
    }

    public boolean vanish(Player player, boolean showMessage) {
        if (!vanished.add(player.getUniqueId())) {
            Lang.VANISH_ALREADY_VANISHED.sendTo(player);
            return false;
        }
        handleVanish(player);
        if (showMessage) {
            Lang.VANISH_ENABLED.sendTo(player);
        }
        return true;
    }

    public void vanishOther(Player player, boolean showMessage, CommandSender executor) {
        if (!vanished.add(player.getUniqueId())) {
            Lang.VANISH_OTHER_ALREADY_VANISHED.sendTo(executor);
            return;
        }
        handleVanish(player);
        if (showMessage) {
            Lang.VANISH_ENABLED_BY_OTHER.sendTo(player, executor.getName());
            Lang.VANISH_ENABLED_FOR_OTHER.sendTo(executor, player.getName());
        }
    }

    private void handleVanish(Player player) {
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (!vanished.contains(p.getUniqueId())) {
                p.hidePlayer(plugin, player);
            }
            if (!p.equals(player) && Permission.MODERATOR.has(p)) {
                Lang.VANISH_ANNOUNCE.sendTo(p, player.getName());
            }
        });
        handleCommon(player, true);
        int status = 1; // 1 means vanish without nightvision applied, 2 with
        if (plugin.getConfig().getBoolean("vanish.nightvision")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1, false, false, false));
            status = 2;
        }
        player.getPersistentDataContainer().set(namespacedKey, PersistentDataType.BYTE, (byte)status);
    }

    public boolean unVanish(Player player, boolean showMessage) {
        if (!vanished.remove(player.getUniqueId())) {
            Lang.VANISH_ALREADY_VISIBLE.sendTo(player);
            return false;
        }
        handleUnVanish(player);
        if (showMessage) {
            Lang.VANISH_DISABLED.sendTo(player);
        }
        return true;
    }

    public void unVanishOther(Player player, boolean showMessage, CommandSender executor) {
        if (!vanished.remove(player.getUniqueId())) {
            Lang.VANISH_OTHER_ALREADY_VISIBLE.sendTo(executor);
            return;
        }
        handleUnVanish(player);
        if (showMessage) {
            Lang.VANISH_DISABLED_BY_OTHER.sendTo(player, executor.getName());
            Lang.VANISH_DISABLED_FOR_OTHER.sendTo(executor, player.getName());
        }
    }

    private void handleUnVanish(Player player) {
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.showPlayer(plugin, player);
            if (!p.equals(player) && Permission.MODERATOR.has(p)) {
                Lang.VANISH_BACK_VISIBLE_ANNOUNCE.sendTo(p, player.getName());
            }
        });
        handleCommon(player, false);
        byte status = player.getPersistentDataContainer().getOrDefault(namespacedKey, PersistentDataType.BYTE, (byte)0);
        if (status == 2) {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
        player.getPersistentDataContainer().set(namespacedKey, PersistentDataType.BYTE, (byte)0);
    }

    private void handleCommon(Player player, boolean vanish) {
        player.setInvulnerable(vanish);
        player.setSleepingIgnored(vanish);
        player.setAllowFlight(vanish || player.getGameMode() == GameMode.CREATIVE);
        if (vanish) {
            bossBar.addPlayer(player);
        } else {
            bossBar.removePlayer(player);
        }
    }

    public void toggleVanish(Player player, boolean showMessage) {
        if (vanished.contains(player.getUniqueId())) { // un-vanish
            unVanish(player, showMessage);
        } else {
            vanish(player, showMessage);
        }
    }

    public void toggleVanishFor(Player player, boolean showMessage, CommandSender executor) {
        if (vanished.contains(player.getUniqueId())) { // un-vanish
             unVanishOther(player, showMessage, executor);
        } else {
            vanishOther(player, showMessage, executor);
        }
    }

    public void sendVanishedPlayerListTo(CommandSender target) {
        if (vanished.isEmpty()) {
            Lang.VANISH_NO_PLAYERS_VANISHED.sendTo(target);
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(ChatColor.GOLD).append("Vanished players: ");
            vanished.forEach(uuid -> {
                if (builder.length() > 19)
                    builder.append(", ");
                //noinspection all
                builder.append(Bukkit.getPlayer(uuid).getName());
            });
            target.sendMessage(builder.toString());
        }
    }

    public void doFakeQuit(Player player) {
        if (!vanish(player, true)) return;
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (!p.equals(player) && Permission.MODERATOR.has(p)) {
                Lang.VANISH_ANNOUNCE.sendTo(p, player.getName());
            } else {
                Lang.LEAVE_MESSAGE.sendTo(p, player.getName());
            }
        });
    }

    public void doFakeJoin(Player player) {
        if (!unVanish(player, true)) return;
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (!p.equals(player) && Permission.MODERATOR.has(p)) {
                Lang.VANISH_BACK_VISIBLE_ANNOUNCE.sendTo(p, player.getName());
            } else {
                Lang.JOIN_MESSAGE.sendTo(p, player.getName());
            }
        });
    }

    private class VanishListener implements Listener {

        @EventHandler
        public void onJoin(PlayerJoinEvent event) {
            Player player = event.getPlayer();
            byte status = event.getPlayer().getPersistentDataContainer().getOrDefault(namespacedKey, PersistentDataType.BYTE, (byte)0);
            if (status == 1 || status == 2) {
                vanish(player, false);
                sendMessageForStaffExclude(Lang.JOINED_VANISHED.get(player.getName()), player);
                event.setJoinMessage(null);
            } else {
                event.setJoinMessage(Lang.JOIN_MESSAGE.get(player.getName()));
            }
        }

        @EventHandler
        public void onQuit(PlayerQuitEvent event) {
            Player player = event.getPlayer();
            if (vanished.remove(player.getUniqueId())) {
                sendMessageForStaffExclude(Lang.LEFT_VANISHED.get(player.getName()), player);
                event.setQuitMessage(null);
            } else {
                event.setQuitMessage(Lang.LEAVE_MESSAGE.get(event.getPlayer().getName()));
            }
        }

        @EventHandler
        public void onGameModeChange(PlayerGameModeChangeEvent event) {
            if (event.getNewGameMode() == GameMode.SURVIVAL || event.getNewGameMode() == GameMode.ADVENTURE) {
                allowFlight(event.getPlayer());
            }
        }

        @EventHandler
        public void onTeleport(PlayerTeleportEvent event) {
            //noinspection all
            if (!event.getFrom().getWorld().getName().equals(event.getTo().getWorld().getName())) {
                allowFlight(event.getPlayer());
            }
        }

        @EventHandler
        public void onFoodLevelChange(FoodLevelChangeEvent event) {
            if (vanished.contains(event.getEntity().getUniqueId())) {
                event.setCancelled(true);
            }
        }

        @EventHandler
        public void onArrowPickup(PlayerPickupArrowEvent event) {
            if (vanished.contains(event.getPlayer().getUniqueId())) {
                event.setCancelled(true);
            }
        }

        private void allowFlight(Player player) {
            if (vanished.contains(player.getUniqueId())) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.setAllowFlight(true);
                    player.setFlying(true);
                });
            }
        }

        private void sendMessageForStaffExclude(String message, Player exclude) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (!player.equals(exclude) && Permission.MODERATOR.has(player)) {
                    player.sendMessage(message);
                }
            });
        }
    }
}
