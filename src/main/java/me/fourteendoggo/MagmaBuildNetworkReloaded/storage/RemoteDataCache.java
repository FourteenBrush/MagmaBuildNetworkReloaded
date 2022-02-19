package me.fourteendoggo.MagmaBuildNetworkReloaded.storage;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.kingdom.Kingdom;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RemoteDataCache {
    private final Map<UUID, User> users = new HashMap<>();
    private final Map<String, Kingdom> kingdoms = new HashMap<>();

    public User getUser(UUID id) {
        return users.get(id);
    }

    public User getUser(Player player) {
        return getUser(player.getUniqueId());
    }

    public User removeUser(UUID id) {
        return users.remove(id);
    }

    public void cacheUser(User user) {
        users.put(user.getId(), user);
    }

    public Kingdom getKingdom(String name) {
        return kingdoms.get(name);
    }

    public void cacheKingdom(Kingdom kingdom) {
        kingdoms.put(kingdom.getName(), kingdom);
    }

    public void startSaveTask(MBNPlugin plugin) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            users.values().forEach(user -> plugin.getStorage().saveUser(user.getData()).whenComplete((v, t) ->
                    plugin.getLogger().info("Saved data for user " + user.getPlayer().getName())));
            kingdoms.values().forEach(kingdom -> plugin.getStorage().saveKingdom(kingdom).whenComplete((k, t) ->
                    plugin.getLogger().info("Saved kingdom " + kingdom.getName())));
        }, 20 * 60 * 5, 20 * 60 * 5);
    }
}
