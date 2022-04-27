package me.fourteendoggo.MagmaBuildNetworkReloaded.user;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class User {
    private final UUID id;
    private final BukkitRunnable actionbar;
    private final Reference<Player> player;
    private final AtomicReference<UserSnapshot> snapshot;

    public User(Player bindTo) {
        id = bindTo.getUniqueId();
        actionbar = new Actionbar();
        player = new WeakReference<>(bindTo);
        snapshot = new AtomicReference<>();
    }

    public UUID getId() {
        return id;
    }

    public UserSnapshot getData() {
        return snapshot.get();
    }

    public Player getPlayer() {
        return player.get();
    }

    public void onDataLoadComplete(MBNPlugin plugin, UserSnapshot loadedSnapshot) {
        actionbar.runTaskTimerAsynchronously(plugin, 1L, 8L);
        snapshot.set(loadedSnapshot);
        Team team = getPlayer().getScoreboard().getTeam("mbn");
        if (team == null) {
            team = getPlayer().getScoreboard().registerNewTeam("mbn");
        } // disable collision
        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        team.addEntry(getPlayer().getName());
    }

    public void logout() {
        actionbar.cancel();
        player.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id) && player.equals(user.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, player);
    }

    @Override
    public String toString() {
        return "User{" +
                "name=" + getPlayer().getName() +
                "online=" + getPlayer().isOnline() +
                '}';
    }

    private class Actionbar extends BukkitRunnable {
        private final DecimalFormat df = new DecimalFormat("#.#");

        @Override
        public void run() {
            String message = Utils.colorize("&a&lHP&r " + df.format(getPlayer().getHealth() * 5) + " / 100");
            getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
        }
    }
}
