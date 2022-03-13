package me.fourteendoggo.MagmaBuildNetworkReloaded.user;

import java.util.concurrent.TimeUnit;

public class Cooldown {
    private final long end;
    private final CooldownType type;

    public Cooldown(long duration, TimeUnit timeUnit, CooldownType type) {
        end = System.currentTimeMillis() + timeUnit.toMillis(duration);
        this.type = type;
    }

    public long getTimeLeftInMillis() {
        return end - System.currentTimeMillis();
    }

    public long getTimeLeft(TimeUnit timeUnit) {
        return TimeUnit.MILLISECONDS.convert(getTimeLeftInMillis(), timeUnit);
    }

    public boolean isExpired() {
        return end < System.currentTimeMillis();
    }

    public CooldownType getType() {
        return type;
    }
}
