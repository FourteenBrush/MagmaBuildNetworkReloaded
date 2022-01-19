package me.fourteendoggo.MagmaBuildNetworkReloaded.commands.managers;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VanishManager {
    private final Set<UUID> vanished;
    private final BossBar bossBar;

    public VanishManager() {
        vanished = new HashSet<>();
        bossBar = Bukkit.createBossBar("Vanished", BarColor.BLUE, BarStyle.SOLID);
        bossBar.setProgress(1);
    }

    public void toggle(Player player) {

    }
}
