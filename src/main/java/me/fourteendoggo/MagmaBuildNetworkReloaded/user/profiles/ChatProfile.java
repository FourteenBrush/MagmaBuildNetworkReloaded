package me.fourteendoggo.MagmaBuildNetworkReloaded.user.profiles;

import me.fourteendoggo.MagmaBuildNetworkReloaded.chat.ChannelRank;
import me.fourteendoggo.MagmaBuildNetworkReloaded.chat.ChatChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChatProfile {
    /* Assuming that the current channel is also present in the channels */
    private final Map<ChatChannel, ChannelRank> chatChannels;
    private ChatChannel currentChannel = null;
    private boolean muted = false;

    public ChatProfile() {
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

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public ChannelRank getRank(ChatChannel channel) {
        return chatChannels.get(channel);
    }

    public boolean mayChat() {
        return !muted && getRank(currentChannel) != ChannelRank.LISTENER;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatProfile that = (ChatProfile) o;
        return muted == that.muted && chatChannels.size() == that.chatChannels.size() && Objects.equals(currentChannel, that.currentChannel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatChannels, currentChannel, muted);
    }
}
