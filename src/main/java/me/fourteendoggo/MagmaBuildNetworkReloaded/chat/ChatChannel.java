package me.fourteendoggo.MagmaBuildNetworkReloaded.chat;

import me.fourteendoggo.MagmaBuildNetworkReloaded.user.User;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Lang;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Utils;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public class ChatChannel {
    private final String name;
    private final String displayName;
    private final String joinPermission;
    private String password = "";
    private final ChannelRank defaultRank;
    private final Set<User> joinedUsers;
    private final Set<UUID> whitelist;
    private final List<String> motd;

    public ChatChannel(String name, String displayName, String joinPermission, ChannelRank defaultRank) {
        this.name = name;
        this.displayName = Utils.colorize(displayName);
        this.joinPermission = joinPermission;
        this.defaultRank = defaultRank;
        this.joinedUsers = new HashSet<>();
        this.whitelist = new HashSet<>();
        this.motd = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean canJoin(User user) {
        return user.getPlayer().hasPermission(joinPermission) && whitelist.contains(user.getId());
    }

    public boolean whitelist(UUID userId) {
        return whitelist.add(userId);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean hasPassword() {
        return password != null && !password.isEmpty();
    }

    public ChannelRank getDefaultRank() {
        return defaultRank;
    }

    public boolean join(User user, boolean showMessage) {
        if (joinedUsers.contains(user)) {
            Lang.CHANNEL_ALREADY_JOINED.sendTo(user);
            return false;
        }
        if (!user.getPlayer().hasPermission(joinPermission)) {
            Lang.NO_PERMISSION.sendTo(user);
            return false;
        }
        if (!whitelist.contains(user.getId())) {
            Lang.CHANNEL_NOT_WHITELISTED.sendTo(user);
            return false;
        }
        user.getData().getChatProfile().addChannel(this);
        if (showMessage) {
            Lang.CHANNEL_JOINED.sendTo(user, displayName);
        }
        return true;
    }

    public boolean leave(User user, boolean showMessage) {
        if (!joinedUsers.remove(user)) {
            Lang.CHANNEL_NOT_JOINED.sendTo(user);
            return false;
        }
        user.getData().getChatProfile().removeChannel(this);
        if (showMessage) {
            Lang.CHANNEL_LEFT.sendTo(user, displayName);
        }
        return true;
    }

    public boolean setAsCurrentFor(User user) {
        ChatChannel currentChannel = user.getData().getChatProfile().getCurrentChannel();
        if (currentChannel != null && currentChannel.getName().equals(getName())) return false;
        if (!user.getData().getChatProfile().isInChannel(this)) {
            join(user, false);
        }
        user.getData().getChatProfile().setCurrentChannel(this);
        Lang.CHANNEL_SET_CURRENT.sendTo(user, displayName);
        return true;
    }

    public void sendMessage(String message) {
        joinedUsers.forEach(user -> user.getPlayer().sendMessage(message));
    }

    @UnmodifiableView
    public List<String> getMotd() {
        return Collections.unmodifiableList(motd);
    }

    public void setMotdLine(int line, String str) {
        motd.add(line, str);
    }
}
