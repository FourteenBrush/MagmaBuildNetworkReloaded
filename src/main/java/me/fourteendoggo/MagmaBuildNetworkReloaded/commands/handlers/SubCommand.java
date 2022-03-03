package me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers;

public abstract class SubCommand {
    private final int argsLength;
    private final boolean allowConsole;

    public SubCommand(int argsLength) {
        this(argsLength, true);
    }

    public SubCommand(int argsLength, boolean allowConsole) {
        this.argsLength = argsLength;
        this.allowConsole = allowConsole;
    }

    public int getArgsLength() {
        return argsLength;
    }

    public boolean isAllowConsole() {
        return allowConsole;
    }

    public abstract CommandResult execute(CommandSource source, String[] args);
}
