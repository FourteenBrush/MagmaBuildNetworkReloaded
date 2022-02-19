package me.fourteendoggo.MagmaBuildNetworkReloaded.user.profiles;

import org.apache.commons.lang.Validate;

import java.util.Objects;
import java.util.UUID;

public class StatisticsProfile {
    private final UUID id;
    private int minutesPlayed;
    private int level;
    private long lastUpdate;
    private final long firstJoin;

    public StatisticsProfile(UUID uuid) {
        this(uuid, 0, 0, System.currentTimeMillis());
    }

    public StatisticsProfile(UUID id, int playTime, int level, long firstJoin) {
        this.id = id;
        this.minutesPlayed = playTime;
        this.level = level;
        this.firstJoin = firstJoin;
        lastUpdate = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public int getMinutesPlayed() {
        return minutesPlayed;
    }

    public int getLevel() {
        return level;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public long getFirstJoin() {
        return firstJoin;
    }

    public void incrementPlaytimeWith(int minutes) {
        Validate.isTrue(minutes > 0, "Cannot increment playtime with a negative number");
        minutesPlayed =+ minutes;
    }

    public void incrementLevel() {
        level++;
    }

    public void setLastUpdate(long lastUpdate) {
        Validate.isTrue(lastUpdate > 0, "Last update time must be bigger than 0");
        this.lastUpdate = lastUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatisticsProfile that = (StatisticsProfile) o;
        return minutesPlayed == that.minutesPlayed && level == that.level && lastUpdate == that.lastUpdate && firstJoin == that.firstJoin && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, minutesPlayed, level, lastUpdate, firstJoin);
    }
}
