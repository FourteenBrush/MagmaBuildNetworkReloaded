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
    private final MBNPlugin plugin;
    private final Map<UUID, User> users = new HashMap<>();
    private final Map<String, Kingdom> kingdoms = new HashMap<>();

    public RemoteDataCache(MBNPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::saveAll, 20 * 60 * 5, 20 * 60 * 5);
    }

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

    private void saveAll() {
        users.values().forEach(user -> plugin.getStorage().saveUser(user.getData()).whenComplete((v, t) ->
                plugin.getLogger().info("Saved data for user " + user.getPlayer().getName())));
        kingdoms.values().forEach(kingdom -> plugin.getStorage().saveKingdom(kingdom).whenComplete((k, t) ->
                plugin.getLogger().info("Saved kingdom " + kingdom.getName())));
    }
}
