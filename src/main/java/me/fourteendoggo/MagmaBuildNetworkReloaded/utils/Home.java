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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Home home = (Home) o;
        return name.equals(home.name) && location.equals(home.location) && owner.equals(home.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, location, owner);
    }
}
