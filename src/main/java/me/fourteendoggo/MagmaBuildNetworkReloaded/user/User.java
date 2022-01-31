package me.fourteendoggo.MagmaBuildNetworkReloaded.user;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.profiles.ChatProfile;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.profiles.MembershipProfile;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.profiles.StatisticsProfile;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.UUID;

public class User implements IUser {
    private final BukkitRunnable actionbar;
    private final ChatProfile chatProfile;
    private final StatisticsProfile statisticsProfile;
    private final MembershipProfile membershipProfile;
    private boolean vanished;

    public User(ChatProfile chatprofile, StatisticsProfile statisticsProfile, MembershipProfile membershipProfile) {
        this.actionbar = new Actionbar();
        this.chatProfile = chatprofile;
        this.statisticsProfile = statisticsProfile;
        this.membershipProfile = membershipProfile;
        this.vanished = false;
        disableCollision();
    }

    @Override
    public UUID getId() {
        return statisticsProfile.getId();
    }

    private Player getPlayer() {
        return Bukkit.getPlayer(getId());
    }

    public boolean isOnline() {
        return getPlayer().isOnline();
    }

    public ChatProfile getChatProfile() {
        return chatProfile;
    }

    @Override
    public StatisticsProfile getStatisticsProfile() {
        return statisticsProfile;
    }

    @Override
    public MembershipProfile getMembershipProfile() {
        return membershipProfile;
    }

    public boolean hasPermission(String permission) {
        return getPlayer().hasPermission(permission);
    }

    public boolean isVanished() {
        return vanished;
    }

    public void setVanished(boolean flag) {
        vanished = flag;
        Player player = getPlayer();
        if (flag) {

        } else {

        }
    }

    @Override
    public void sendMessage(String message) {
        getPlayer().sendMessage(Utils.colorize(message));
    }

    @Override
    public void login(MBNPlugin plugin) {
        actionbar.runTaskTimerAsynchronously(plugin, 20, 8);
    }

    @Override
    public void logoutSafely() {
        actionbar.cancel();
    }

    private void disableCollision() {
        Player player = getPlayer();
        Team team = player.getScoreboard().getTeam("mbn");
        if (team == null) {
            team = player.getScoreboard().registerNewTeam("mbn");
        }
        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        team.addEntry(player.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(chatProfile, user.chatProfile) && Objects.equals(statisticsProfile, user.statisticsProfile) && Objects.equals(membershipProfile, user.membershipProfile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), chatProfile, statisticsProfile, membershipProfile);
    }

    private class Actionbar extends BukkitRunnable {
        private final Player player;
        private final DecimalFormat df;

        public Actionbar() {
            this.player = getPlayer();
            this.df = new DecimalFormat("#.##");
        }

        @Override
        public void run() {
            String message = Utils.colorize("&a&lHP&r " + df.format(player.getHealth() * 5) + " / 100");
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
        }
    }
}
