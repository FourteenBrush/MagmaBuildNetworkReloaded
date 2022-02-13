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
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

public class NewUser {
    private final UUID id;
    private boolean dirty = false;
    private BukkitRunnable actionbar;
    private final Reference<Player> player;
    private final AtomicReference<UserSnapshot> snapshot;

    public NewUser(Player bindTo) {
        id = bindTo.getUniqueId();
        player = new WeakReference<>(bindTo);
        snapshot = new AtomicReference<>();
    }

    public UUID getId() {
        return id;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public void setSnapshot(UserSnapshot userSnapshot) {
        snapshot.set(userSnapshot);
        dirty = true;
    }

    public UserSnapshot getSnapshot() {
        return snapshot.get();
    }

    public void update(UnaryOperator<UserSnapshot> op) {
        snapshot.updateAndGet(op);
        dirty = true;
    }

    public Player getPlayer() {
        return player.get();
    }

    public void onClientReady(MBNPlugin plugin) {
        actionbar = new Actionbar(getPlayer());
        actionbar.runTaskTimerAsynchronously(plugin, 1L, 8L);
        Team team = getPlayer().getScoreboard().getTeam("mbn");
        if (team == null) {
            team = getPlayer().getScoreboard().registerNewTeam("mbn");
        }
        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        team.addEntry(getPlayer().getName());
    }

    public void logout() {
        actionbar.cancel();
    }

    private static class Actionbar extends BukkitRunnable {
        private final Player player;
        private final DecimalFormat df;

        public Actionbar(Player player) {
            this.player = player;
            df = new DecimalFormat("#.##");
        }

        @Override
        public void run() {
            String message = Utils.colorize("&a&lHP&r " + df.format(player.getHealth() * 5) + " / 100");
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
        }
    }
}
