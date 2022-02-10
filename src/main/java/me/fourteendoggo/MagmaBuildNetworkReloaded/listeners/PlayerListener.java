package me.fourteendoggo.MagmaBuildNetworkReloaded.listeners;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.User;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.profiles.ChatProfile;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.profiles.StatisticsProfile;
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
        User user;
        if (offlinePlayer.hasPlayedBefore()) {
            user = plugin.getStorage().loadUser(event.getUniqueId()).join();
        } else {
            user = new User(new ChatProfile(event.getUniqueId()), new StatisticsProfile(event.getUniqueId()));
            plugin.getStorage().createNewUser(user).whenComplete((v, t) ->
                    plugin.getBaseRepository().getUserRepository().cache(event.getUniqueId(), user));
        }
        // load the homes, kingdom, chatchannel
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

    }
}
