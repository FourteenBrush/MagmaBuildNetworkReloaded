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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
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

    private boolean isVanished(Entity entity) {
        return vanished.contains(entity.getUniqueId());
    }

    public void vanish(Player target, boolean showMessage, boolean showQuitMessage) {
        if (!vanishSucceeded(target, Lang.VANISH_ALREADY_VANISHED)) return;
        vanishInternal(target, showMessage, showQuitMessage);
    }

    private void vanishInternal(Player target, boolean showMessage, boolean showQuitMessage) {
        handleVanish(target, showQuitMessage);
        if (showMessage) {
            Lang.VANISH_ENABLED.sendTo(target);
        }
    }

    public void vanishOther(Player target, CommandSender executor) {
        if (vanishSucceeded(target, Lang.VANISH_OTHER_ALREADY_VANISHED)) {
            vanishOtherInternal(target, executor);
        }
    }

    private void vanishOtherInternal(Player target, CommandSender executor) {
        handleVanish(target, false);
        Lang.VANISH_ENABLED_BY_OTHER.sendTo(target, executor.getName());
        Lang.VANISH_ENABLED_FOR_OTHER.sendTo(executor, target.getName());
    }

    private boolean vanishSucceeded(Player target, Lang alreadyVanished) {
        if (vanished.add(target.getUniqueId())) return true;
        alreadyVanished.sendTo(target);
        return false;
    }

    private void handleVanish(Player target, boolean showQuitMessage) {
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (!isVanished(p)) {
                p.hidePlayer(plugin, target);
            }
            if (!p.equals(target) && Permission.MODERATOR.has(p)) {
                Lang.VANISH_ANNOUNCE.sendTo(p, target.getName());
            } else if (showQuitMessage) { // quit message will be shown to the target too
                Lang.LEAVE_MESSAGE.sendTo(p, target.getName());
            }
        });
        handleCommon(target, true);
        int status = 1; // 1 means vanish without nightvision applied, 2 with
        if (plugin.getConfig().getBoolean("vanish.nightvision")) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1, false, false, false));
            status = 2;
        }
        target.getPersistentDataContainer().set(namespacedKey, PersistentDataType.BYTE, (byte)status);
    }

    public void unVanish(Player target, boolean showJoinMessage) {
        if (!unVanishSucceeded(target, Lang.VANISH_ALREADY_VISIBLE)) return;
        unVanishInternal(target, showJoinMessage);
    }

    private void unVanishInternal(Player target, boolean showJoinMessage) {
        handleUnVanish(target, showJoinMessage);
        Lang.VANISH_DISABLED.sendTo(target);
    }

    public void unVanishOther(Player target, CommandSender executor) {
        if (unVanishSucceeded(target, Lang.VANISH_OTHER_ALREADY_VISIBLE)) {
            unVanishOtherInternal(target, executor);
        }
    }

    private void unVanishOtherInternal(Player target, CommandSender executor) {
        handleUnVanish(target, false);
        Lang.VANISH_DISABLED_BY_OTHER.sendTo(target, executor.getName());
        Lang.VANISH_DISABLED_FOR_OTHER.sendTo(executor, target.getName());
    }

    private boolean unVanishSucceeded(Player target, Lang alreadyUnVanished) {
        if (vanished.remove(target.getUniqueId())) return true;
        alreadyUnVanished.sendTo(target);
        return false;
    }

    private void handleUnVanish(Player target, boolean showJoinMessage) {
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.showPlayer(plugin, target);
            if (!p.equals(target) && Permission.MODERATOR.has(p)) {
                Lang.VANISH_BACK_VISIBLE_ANNOUNCE.sendTo(p, target.getName());
            } else if (showJoinMessage) { // join message will be shown to the target too
                Lang.JOIN_MESSAGE.sendTo(p, target.getName());
            }
        });
        handleCommon(target, false);
        byte status = target.getPersistentDataContainer().getOrDefault(namespacedKey, PersistentDataType.BYTE, (byte)0);
        if (status == 2) {
            target.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
        target.getPersistentDataContainer().set(namespacedKey, PersistentDataType.BYTE, (byte)0);
    }

    private void handleCommon(Player target, boolean vanish) {
        target.setInvulnerable(vanish);
        target.setSleepingIgnored(vanish);
        target.setAllowFlight(vanish || target.getGameMode() == GameMode.CREATIVE || target.getGameMode() == GameMode.SPECTATOR);
        if (vanish) {
            bossBar.addPlayer(target);
            target.setSaturation(20f);
        } else {
            bossBar.removePlayer(target);
        }
    }

    public void toggleVanish(Player target) { // intentionally chosen for the internal methods, avoid double checking
        if (isVanished(target)) { // un-vanish
            unVanishInternal(target, false);
        } else {
            vanishInternal(target, true, false);
        }
    }

    public void toggleVanishFor(Player target, CommandSender executor) { // intentionally chosen for the internal methods, avoid double checking
        if (isVanished(target)) { // un-vanish
             unVanishOtherInternal(target, executor);
        } else {
            vanishOtherInternal(target, executor);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void sendVanishedPlayerListTo(CommandSender target) {
        if (vanished.isEmpty()) {
            Lang.VANISH_NO_PLAYERS_VANISHED.sendTo(target);
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(ChatColor.GOLD).append("Vanished players: ");
            vanished.forEach(uuid -> {
                if (builder.length() > 21)
                    builder.append(", ");
                builder.append(Bukkit.getPlayer(uuid).getName());
            });
            target.sendMessage(builder.toString());
        }
    }

    private class VanishListener implements Listener {

        @EventHandler
        public void onJoin(PlayerJoinEvent event) {
            Player player = event.getPlayer();
            byte status = event.getPlayer().getPersistentDataContainer().getOrDefault(namespacedKey, PersistentDataType.BYTE, (byte)0);
            if (status == 1 || status == 2) {
                vanish(player, false, false);
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
            GameMode gm = event.getNewGameMode();
            if ((gm == GameMode.SURVIVAL || gm == GameMode.ADVENTURE) && isVanished(event.getPlayer())) {
                allowFlight(event.getPlayer());
            }
        }

        @SuppressWarnings("ConstantConditions")
        @EventHandler
        public void onTeleport(PlayerTeleportEvent event) {
            String fromWorld = event.getFrom().getWorld().getName();
            String destinationWorld = event.getTo().getWorld().getName();
            if (!fromWorld.equals(destinationWorld) && isVanished(event.getPlayer())) {
                allowFlight(event.getPlayer());
            }
        }

        @EventHandler
        public void onFoodLevelChange(FoodLevelChangeEvent event) {
            if (event.getFoodLevel() < event.getEntity().getFoodLevel() && isVanished(event.getEntity())) {
                event.setCancelled(true);
            }
        }

        @EventHandler
        public void onPickup(EntityPickupItemEvent event) {
            if (event.getEntity() instanceof Player player && isVanished(player)) {
                event.setCancelled(true);
            }
        }

        private void allowFlight(Player target) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                target.setAllowFlight(true);
                target.setFlying(true);
            });
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
