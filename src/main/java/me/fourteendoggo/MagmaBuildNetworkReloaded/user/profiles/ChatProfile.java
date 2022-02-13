package me.fourteendoggo.MagmaBuildNetworkReloaded.user.profiles;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.fourteendoggo.MagmaBuildNetworkReloaded.chat.ChannelRank;
import me.fourteendoggo.MagmaBuildNetworkReloaded.chat.ChatChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ChatProfile {
    private static final Cache<UUID, Object> chatCache =
            CacheBuilder.newBuilder().expireAfterWrite(800, TimeUnit.MILLISECONDS).build();

    private final UUID userId;
    /* Assuming that the current channel is also present in the channels */
    private final Map<ChatChannel, ChannelRank> chatChannels;
    private ChatChannel currentChannel = null;
    private boolean muted = false;

    public ChatProfile(UUID userId) {
        this.userId = userId;
        this.chatChannels = new HashMap<>();
    }

    public ChatChannel getCurrentChannel() {
        return currentChannel;
    }

    public void setCurrentChannel(ChatChannel channel) {
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
        return !muted && getRank(currentChannel) != ChannelRank.LISTENER && chatCache.getIfPresent(userId) != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatProfile that = (ChatProfile) o;
        return muted == that.muted && userId.equals(that.userId) && chatChannels.size() == that.chatChannels.size() && Objects.equals(currentChannel, that.currentChannel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, chatChannels, currentChannel, muted);
    }
}
