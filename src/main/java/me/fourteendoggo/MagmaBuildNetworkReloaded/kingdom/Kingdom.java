package me.fourteendoggo.MagmaBuildNetworkReloaded.kingdom;

import me.fourteendoggo.MagmaBuildNetworkReloaded.user.User;

import javax.xml.stream.Location;
import java.util.HashSet;
import java.util.Set;

public class Kingdom {
    private final String name;
    private final KingdomType kingdomType;
    private final Location spawnLocation;
    private final Set<User> members;

    public Kingdom(String name, KingdomType kingdomType, Location spawnLocation) {
        this.name = name;
        this.kingdomType = kingdomType;
        this.spawnLocation = spawnLocation;
        members = new HashSet<>();
    }

    public String getName() {
        return name;
    }
}
