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
    }

    public void vanish(Player player) {
        vanished.add(player.getUniqueId());
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (!vanished.contains(p.getUniqueId())) {
                p.hidePlayer(plugin, player);
            }
            if (p != player && Permission.MODERATOR.has(p)) {
                p.sendMessage(Lang.VANISH_ANNOUNCE.get(player.getName()));
            }
        });
        handleVanish(player, true);
        byte statusByte = (byte)1;
        if (plugin.getConfig().getBoolean("vanish.nightvision")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1, false, false, false));
            statusByte = (byte)2; // night vision applied by vanish code, 1 means not applied, or by user
        }
        player.getPersistentDataContainer().set(namespacedKey, PersistentDataType.BYTE, statusByte);
    }

    public void unVanish(Player player) {
        vanished.remove(player.getUniqueId());
        Bukkit.getOnlinePlayers().forEach(p -> {
           p.showPlayer(plugin, player);
           if (p != player && Permission.MODERATOR.has(p)) {
                p.sendMessage(Lang.VANISHED_BACK_VISIBLE_ANNOUNCE.get(player.getName()));
           }
        });
        handleVanish(player, false);
        byte statusByte = player.getPersistentDataContainer().getOrDefault(namespacedKey, PersistentDataType.BYTE, (byte)0);
        if (statusByte == 2) {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
        player.getPersistentDataContainer().set(namespacedKey, PersistentDataType.BYTE, (byte)0);
    }

    public void sendVanishedPlayerListTo(CommandSender target) {
        if (vanished.isEmpty()) {
            target.sendMessage(Lang.VANISHED_NO_PLAYERS_VANISHED.get());
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(ChatColor.GOLD).append("Vanished: ");
            vanished.forEach(uuid -> {
                if (builder.length() > 0)
                    builder.append(", ");
                builder.append(Bukkit.getPlayer(uuid));
            });
            target.sendMessage(builder.toString());
        }
    }

    public void doFakeQuitFor(Player player) {

    }

    private void handleVanish(Player player, boolean vanish) {
        player.setInvulnerable(vanish);
        player.setSleepingIgnored(vanish);
        if (vanish) {
            bossBar.addPlayer(player);
        } else {
            bossBar.removePlayer(player);
        }
    }
}
