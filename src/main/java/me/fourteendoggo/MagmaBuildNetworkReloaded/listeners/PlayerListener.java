package me.fourteendoggo.MagmaBuildNetworkReloaded.listeners;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    private final MBNPlugin plugin;

    public PlayerListener(MBNPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(event.getUniqueId());
        // load the homes, kingdom, chatchannel
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

    }
}
