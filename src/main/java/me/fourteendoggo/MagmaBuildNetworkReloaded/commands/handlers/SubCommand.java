package me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers;

public abstract class SubCommand {
    private final int argsLength;
    private final int subCommandAt;
    private final boolean allowConsole;

    public SubCommand(int argsLength, int subCommandAt) {
        this(argsLength, subCommandAt, true);
    }

    public SubCommand(int argsLength, int subCommandAt, boolean allowConsole) {
        this.argsLength = argsLength;
        this.subCommandAt = subCommandAt;
        this.allowConsole = allowConsole;
    }

    public int getArgsLength() {
        return argsLength;
    }

    public int getSubCommandAt() {
        return subCommandAt;
    }

    public boolean isAllowConsole() {
        return allowConsole;
    }

    public abstract CommandResult execute(CommandSource source, String[] args);
}
