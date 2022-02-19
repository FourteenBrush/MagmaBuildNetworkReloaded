package me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers;

public enum CommandResult {

    SUCCESS,
    FAILED,
    /** this will print the help message to the sender */
    NO_PERMISSION,
    PLAYER_ONLY,
    SHOW_USAGE,
    TARGET_NOT_FOUND
}
