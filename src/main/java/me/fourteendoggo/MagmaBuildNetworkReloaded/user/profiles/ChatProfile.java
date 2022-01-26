package me.fourteendoggo.MagmaBuildNetworkReloaded.user.profiles;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.fourteendoggo.MagmaBuildNetworkReloaded.chat.ChannelRank;
import me.fourteendoggo.MagmaBuildNetworkReloaded.chat.ChatChannel;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ChatProfile {
    private static final Cache<UUID, Object> chatCache = CacheBuilder.newBuilder()
            .expireAfterWrite(800, TimeUnit.MILLISECONDS).build();
    private final User user;
    /* Assuming that the current channel is also present in the channels */
    private final Map<ChatChannel, ChannelRank> chatChannels;
    private ChatChannel currentChannel;
    private boolean muted;

    public ChatProfile(User user) {
        this.user = user;
        this.chatChannels = new HashMap<>();
    }

    public ChatChannel getCurrentChannel() {
        return currentChannel;
    }

    public void setCurrentChannel(ChatChannel channel) {
        chatChannels.computeIfAbsent(channel, c -> {
            channel.join(user, false);
            return channel.getDefaultRank();
        });
        currentChannel = channel;
    }

    public void addChannel(ChatChannel channel) {
        chatChannels.putIfAbsent(channel, channel.getDefaultRank());
    }

    public void removeChannel(ChatChannel channel) {
        chatChannels.remove(channel);
    }

    public boolean isInChannel(ChatChannel channel) {
        return chatChannels.containsKey(channel);
    }

    public boolean isInChannel() {
        return !chatChannels.isEmpty();
    }

    public boolean isMuted() {
        return muted;
    }

    public boolean setMuted(boolean muted) {
        return (this.muted != (this.muted = muted));
    }

    public ChannelRank getRank(ChatChannel channel) {
        return chatChannels.get(channel);
    }

    public boolean mayChat() {
        return !muted && getRank(currentChannel) != ChannelRank.LISTENER && chatCache.getIfPresent(user.getId()) != null;
    }
}