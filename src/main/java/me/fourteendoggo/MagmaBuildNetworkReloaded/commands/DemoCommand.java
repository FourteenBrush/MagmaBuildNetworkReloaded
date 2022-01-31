package me.fourteendoggo.MagmaBuildNetworkReloaded.commands;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandBase;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandResult;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandSource;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Lang;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Permission;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class DemoCommand extends CommandBase {

    public DemoCommand(MBNPlugin plugin) {
        super(plugin, "demo", Permission.DEFAULT, true);
    }

    @Override
    protected CommandResult execute(CommandSource source, @NotNull String[] args) {
        source.getPlayer().ifPresent(player -> player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20, 2)));
        source.getPlayer().ifPresent(player -> player.sendMessage(Lang.CHANNEL_JOINED.get("test")));
        if (!source.isPlayer()) {
            return CommandResult.PLAYER_ONLY;
        }
        return CommandResult.SUCCESS;
    }

    @Override
    protected String getUsage() {
        return "&athis is the description of the demo command idk";
    }
}
