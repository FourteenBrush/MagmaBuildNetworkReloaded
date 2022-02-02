package me.fourteendoggo.MagmaBuildNetworkReloaded.chat;

import com.google.common.collect.ImmutableList;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.User;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Lang;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Utils;
import org.apache.commons.lang.Validate;

import java.util.*;

public class ChatChannel {
    private final String name; // identifier
    private final String displayName;
    private final String joinPermission;
    private String password;
    private final ChannelRank defaultRank;
    private final Set<User> joinedUsers;
    private final Set<UUID> whitelist;
    private final List<String> motd;

    public ChatChannel(String name, String displayName, String joinPermission, ChannelRank defaultRank) {
        Validate.isTrue(name.length() < 30);
        Validate.isTrue(displayName.length() < 30);
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
        return user.hasPermission(joinPermission) && whitelist.contains(user.getId());
    }

    public boolean whitelist(UUID userId) {
        return whitelist.add(userId);
    }

    public String getPassword() {
        return password;
    }

    public boolean hasPassword() {
        return password != null && !password.isEmpty();
    }

    public ChannelRank getDefaultRank() {
        return defaultRank;
    }

    public boolean join(User user, boolean showMessage) {
        if (joinedUsers.contains(user)) {
            user.sendMessage(Lang.CHANNEL_ALREADY_JOINED.get());
            return false;
        }
        if (!user.hasPermission(joinPermission)) {
            user.sendMessage(Lang.NO_PERMISSION.get());
            return false;
        }
        if (!whitelist.contains(user.getId())) {
            user.sendMessage(Lang.CHANNEL_NOT_WHITELISTED.get());
            return false;
        }
        user.getChatProfile().addChannel(this);
        if (showMessage) {
            user.sendMessage(Lang.CHANNEL_JOINED.get(getDisplayName()));
        }
        return true;
    }

    public boolean leave(User user, boolean showMessage) {
        if (!joinedUsers.remove(user)) {
            user.sendMessage(Lang.CHANNEL_NOT_JOINED.get());
            return false;
        }
        user.getChatProfile().removeChannel(this);
        if (showMessage) {
            user.sendMessage(Lang.CHANNEL_LEFT.get(getDisplayName()));
        }
        return true;
    }

    public boolean setAsCurrentFor(User user) {
        ChatChannel currentChannel = user.getChatProfile().getCurrentChannel();
        if (currentChannel != null && currentChannel.getName().equals(getName())) return false;
        if (!user.getChatProfile().isInChannel(this)) {
            join(user, false);
        }
        user.getChatProfile().setCurrentChannel(this);
        user.sendMessage(Lang.CHANNEL_SET_CURRENT.get(getDisplayName()));
        return true;
    }

    public List<String> getMotd() {
        return ImmutableList.copyOf(motd);
    }

    public void setMotdLine(int line, String str) {
        motd.add(line, str);
    }
}
