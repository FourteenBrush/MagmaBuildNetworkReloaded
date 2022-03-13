package me.fourteendoggo.MagmaBuildNetworkReloaded.listeners;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.User;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.UserSnapshot;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Lang;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerListener implements Listener {
    private final MBNPlugin plugin;
    private final Cache<UUID, Object> chatCache;

    public PlayerListener(MBNPlugin plugin) {
        this.plugin = plugin;
        chatCache = CacheBuilder.newBuilder().expireAfterWrite(800, TimeUnit.MILLISECONDS).build();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        User user = new User(event.getPlayer());
        if (event.getPlayer().hasPlayedBefore()) {
            handleNormalJoin(user);
        } else {
            handleFirstJoin(user);
        }
    }

    private void handleNormalJoin(User user) {
        plugin.getLogger().info("Loading data for user " + user.getPlayer().getName());
        plugin.getStorage().loadUser(user.getId()).whenComplete((s, t) ->
                handleJoin(user, s, t, "Loaded user " + user.getPlayer().getName()));
    }

    private void handleFirstJoin(User user) {
        plugin.getLogger().info("User " + user.getPlayer().getName() + " joined for the first time, creating their data...");
        UserSnapshot snapshot = UserSnapshot.createNew(user.getId());
        plugin.getStorage().createUser(snapshot).whenComplete((v, t) ->
                handleJoin(user, snapshot, t, "Created user " + user.getPlayer().getName()));
    }

    private void handleJoin(User user, UserSnapshot snapshot, Throwable throwable, String logOnSuccess) {
        if (throwable != null) {
            user.getPlayer().kickPlayer(Lang.ERROR_FAILED_TO_LOAD_DATA.get());
            plugin.getLogger().warning("Failed to load create or load user " + user.getPlayer().getName() + ", storage implementation is " + plugin.getStorage().getStorageType().getDescription());
            throwable.printStackTrace();
        } else {
            plugin.getCache().cacheUser(user);
            user.onDataLoadComplete(plugin, snapshot);
            plugin.getLogger().info(logOnSuccess);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        User user = plugin.getCache().removeUser(event.getPlayer().getUniqueId());
        user.logout();
        plugin.getStorage().saveUser(user.getData()).thenRun(() -> plugin.getLogger().info("Saved quit user " + user.getPlayer().getName()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        User user = plugin.getCache().getUser(event.getPlayer());
        if (!user.getData().getChatProfile().mayChat() || chatCache.getIfPresent(user.getId()) != null) {
            event.setCancelled(true);
            Lang.CHAT_COOLDOWN.sendTo(user);
        } else {
            chatCache.put(event.getPlayer().getUniqueId(), false);
        }
    }
}
