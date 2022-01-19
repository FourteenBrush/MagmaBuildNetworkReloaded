package me.fourteendoggo.MagmaBuildNetworkReloaded.listeners;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    private final MBNPlugin plugin;

    public PlayerListener(MBNPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

    }
}
