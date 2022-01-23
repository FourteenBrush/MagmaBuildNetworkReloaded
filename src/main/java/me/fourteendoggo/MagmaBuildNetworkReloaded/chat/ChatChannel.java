package me.fourteendoggo.MagmaBuildNetworkReloaded.chat;

import com.google.common.collect.ImmutableList;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.User;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Utils;
import org.apache.commons.lang.Validate;

import java.util.*;

public class ChatChannel {
    private final String name;
    private final String prefix;
    private final String joinPermission;
    private String password;
    private final ChannelRank defaultRank;
    private final Set<User> joinedUsers;
    private final Set<UUID> whitelist;
    private final List<String> motd;

    public ChatChannel(String name, String prefix, String joinPermission, ChannelRank defaultRank) {
        Validate.isTrue(name.length() < 21);
        Validate.isTrue(prefix.length() < 21);
        this.name = name;
        this.prefix = Utils.colorize(prefix);
        this.joinPermission = joinPermission;
        this.defaultRank = defaultRank;
        this.joinedUsers = new HashSet<>();
        this.whitelist = new HashSet<>();
        this.motd = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean canJoin(User user) {
        return user.hasPermission(joinPermission) && whitelist.contains(user.getId());
    }

    public String getPassword() {
        return password;
    }

    public ChannelRank getDefaultRank() {
        return defaultRank;
    }

    public boolean join(User user, boolean showMessage) {
        if (joinedUsers.contains(user)) {
            user.sendMessage();
            return false;
        }
        if (!whitelist.contains(user.getId())) {
            user.sendMessage();
            return false;
        }
        user.getChatProfile().addChannel(this);
        return true;
    }

    public boolean leave(User user, boolean showMessage) {
        return true;
    }

    public List<String> getMotd() {
        return ImmutableList.copyOf(motd);
    }
}
