package me.fourteendoggo.MagmaBuildNetworkReloaded.listeners;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.cache.UserRepository;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.User;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.UserSnapshot;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Lang;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerListener implements Listener {
    private final MBNPlugin plugin;
    private final UserRepository userRepository;
    private final Cache<UUID, Object> chatCache =
            CacheBuilder.newBuilder().expireAfterWrite(800, TimeUnit.MILLISECONDS).build();

    public PlayerListener(MBNPlugin plugin) {
        this.plugin = plugin;
        userRepository = plugin.getBaseRepository().getUserRepository();
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        plugin.getLogger().info("Entering player login event block");
        User user = new User(event.getPlayer());
        if (event.getPlayer().hasPlayedBefore()) {
            plugin.getLogger().info("Handling normal join");
            handleNormalJoin(user);
        } else {
            plugin.getLogger().info("Handling first join");
            handleFirstJoin(user);
        }
    }

    private void handleNormalJoin(User user) {
        plugin.getStorage().loadUser(user.getId()).whenComplete((s, t) -> {
            if (t != null) {
                user.getPlayer().kickPlayer(Lang.ERROR_FAILED_TO_LOAD_DATA.get());
            } else {
                plugin.getLogger().info("Trying to handle join data for normal join");
                user.setSnapshot(s);
                user.onDataLoadComplete(plugin);
                userRepository.cache(user.getId(), user);
                plugin.getStorage().saveUser(s);
                plugin.getLogger().info("Completed");
            }
        });
    }

    private void handleFirstJoin(User user) {
        UserSnapshot snapshot = UserSnapshot.createNewFor(user.getId());
        plugin.getStorage().createUser(snapshot).whenComplete((v, t) -> {
            if (t != null) {
                user.getPlayer().kickPlayer(Lang.ERROR_FAILED_TO_LOAD_DATA.get());
            } else {
                plugin.getLogger().info("Trying to handle join data for first join");
                user.setSnapshot(snapshot);
                user.onDataLoadComplete(plugin);
                userRepository.cache(user.getId(), user);
                plugin.getStorage().saveUser(user.getData());
                plugin.getLogger().info("Completed");
            }
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) { // TODO check vanish
        User user = userRepository.get(event.getPlayer().getUniqueId());
        user.logout();
        plugin.getStorage().saveUser(user.getData()).whenComplete((v, t) -> userRepository.remove(event.getPlayer().getUniqueId()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        User user = plugin.getBaseRepository().getUserRepository().get(event.getPlayer().getUniqueId());
        if (!user.getData().chatProfile().mayChat() && chatCache.getIfPresent(user.getId()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onTabComplete(TabCompleteEvent event) {
        event.getSender().sendMessage("Tab complete event triggered!");
    }
}
