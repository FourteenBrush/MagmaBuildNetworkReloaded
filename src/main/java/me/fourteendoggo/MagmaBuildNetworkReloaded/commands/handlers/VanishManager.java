package me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Lang;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
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
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onCleanup(PlayerQuitEvent event) {
                vanished.remove(event.getPlayer().getUniqueId());
            }
        }, plugin);
    }

    public void vanish(Player player, boolean showMessage) {
        if (!vanished.add(player.getUniqueId())) {
            player.sendMessage(Lang.VANISH_ALREADY_VANISHED.get());
            return;
        }
        handVanish(player);
        if (showMessage) {
            player.sendMessage(Lang.VANISH_ENABLED.get());
        }
    }

    public void vanishOther(Player player, boolean showMessage, CommandSender executor) {
        if (!vanished.add(player.getUniqueId())) {
            executor.sendMessage(Lang.VANISH_OTHER_ALREADY_VANISHED.get());
            return;
        }
        handVanish(player);
        if (showMessage) {
            player.sendMessage(Lang.VANISH_ENABLED_BY_OTHER.get(executor.getName()));
            executor.sendMessage(Lang.VANISH_ENABLED_FOR_OTHER.get(player.getName()));
        }
    }

    private void handVanish(Player player) {
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (!vanished.contains(p.getUniqueId())) {
                p.hidePlayer(plugin, player);
            }
            if (p != player && Permission.MODERATOR.has(p)) {
                p.sendMessage(Lang.VANISH_ANNOUNCE.get(player.getName()));
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

    public void unVanish(Player player, boolean showMessage) {
        if (!vanished.remove(player.getUniqueId())) {
            player.sendMessage(Lang.VANISH_ALREADY_VISIBLE.get());
            return;
        }
        handleUnVanish(player);
        if (showMessage) {
            player.sendMessage(Lang.VANISH_DISABLED.get());
        }
    }

    public void unVanishOther(Player player, boolean showMessage, CommandSender executor) {
        if (!vanished.remove(player.getUniqueId())) {
            executor.sendMessage(Lang.VANISH_OTHER_ALREADY_VISIBLE.get());
            return;
        }
        handleUnVanish(player);
        if (showMessage) {
            player.sendMessage(Lang.VANISH_DISABLED_BY_OTHER.get());
            executor.sendMessage(Lang.VANISH_DISABLED_FOR_OTHER.get(player.getName()));
        }
    }

    private void handleUnVanish(Player player) {
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.showPlayer(plugin, player);
            if (p != player && Permission.MODERATOR.has(p)) {
                p.sendMessage(Lang.VANISH_BACK_VISIBLE_ANNOUNCE.get(player.getName()));
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
            target.sendMessage(Lang.VANISH_NO_PLAYERS_VANISHED.get());
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(ChatColor.GOLD).append("Vanished: ");
            vanished.forEach(uuid -> {
                if (builder.length() > 0)
                    builder.append(", ");
                builder.append(Bukkit.getPlayer(uuid).getName());
            });
            target.sendMessage(builder.toString());
        }
    }

    public void doFakeQuit(Player player) {
        if (!vanished.add(player.getUniqueId())) {
            player.sendMessage(Lang.VANISH_ALREADY_VANISHED.get());
        } else {
            vanish(player, true);
            String leaveMessage = Lang.LEAVE_MESSAGE.get(player.getName());
            player.sendMessage(leaveMessage);
            Bukkit.getOnlinePlayers().forEach(p -> {
                if (p != player && Permission.MODERATOR.has(p)) {
                    p.sendMessage(Lang.VANISH_ANNOUNCE.get(player.getName()));
                } else {
                    p.sendMessage(leaveMessage);
                }
            });
        }
    }

    public void doFakeJoin(Player player) {
        if (!vanished.remove(player.getUniqueId())) {
            player.sendMessage(Lang.VANISH_ALREADY_VISIBLE.get());
        } else {
            unVanish(player, true);
            String joinMessage = Lang.JOIN_MESSAGE.get(player.getName());
            player.sendMessage(joinMessage);
            Bukkit.getOnlinePlayers().forEach(p -> {
                if (p != player && Permission.MODERATOR.has(p)) {
                    p.sendMessage(Lang.VANISH_BACK_VISIBLE_ANNOUNCE.get(player.getName()));
                } else {
                    p.sendMessage(joinMessage);
                }
            });
        }
    }
}
