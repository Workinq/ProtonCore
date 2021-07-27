package kr.kieran.protonprisons.commands;

public class CommandRequirements
{

    // The permission node needed to execute the command
    private final String permission;
    public String getPermission() { return permission; }

    // Whether you must be an in-game player to execute the command or not
    private final boolean playerOnly;
    public boolean isPlayerOnly() { return playerOnly; }

    public CommandRequirements(String permission, boolean playerOnly)
    {
        this.permission = permission;
        this.playerOnly = playerOnly;
    }

    public static class Builder
    {

        private String permission;
        private boolean playerOnly = false;

        public Builder(String permission)
        {
            this.permission = permission;
        }

        public Builder playerOnly(boolean playerOnly)
        {
            this.playerOnly = playerOnly;
            return this;
        }

        public Builder permission(String permission)
        {
            this.permission = permission;
            return this;
        }

        public CommandRequirements build()
        {
            return new CommandRequirements(permission, playerOnly);
        }

    }

}
