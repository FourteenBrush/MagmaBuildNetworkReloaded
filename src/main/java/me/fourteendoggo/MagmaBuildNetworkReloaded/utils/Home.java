package me.fourteendoggo.MagmaBuildNetworkReloaded.utils;

import org.bukkit.Location;

import java.util.Objects;
import java.util.UUID;

public class Home {
    private final String name;
    private final Location location;
    private final UUID owner;

    public Home(String name, UUID owner, Location location) {
        this.name = name;
        this.location = location;
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public UUID getOwner() {
        return owner;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, location, owner);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Home)) return false;
        Home other = (Home) obj;
        return Utils.equals(name, other.getName(), owner, other.getOwner(), location, other.getLocation());
    }
}
