package net.lomeli.boomtwitch.commands;

import net.lomeli.boombot.api.commands.CommandData;
import net.lomeli.boombot.api.commands.CommandResult;
import net.lomeli.boombot.api.commands.ICommand;
import net.lomeli.boombot.api.permissions.BotPermission;
import net.lomeli.boomtwitch.BoomTwitch;

public class SetKeyCommand implements ICommand {

    @Override
    public CommandResult execute(CommandData cmd) {
        if (cmd.getArgs().isEmpty()) return new CommandResult("boombot_twitch_integration.setkey.noargs");
        if (cmd.getArgs().size() > 1) return new CommandResult("boombot_twitch_integration.setkey.toomanyargs");
        BoomTwitch.twitchClientKey = cmd.getArgs().get(0);
        return new CommandResult("boombot_twitch_integration.setkey");
    }

    @Override
    public String getName() {
        return "setTwitchKey";
    }

    @Override
    public boolean canUserExecute(CommandData cmd) {
        return BotPermission.isUserBoomBotAdmin(cmd.getUserInfo().getUserID());
    }

    @Override
    public boolean canBotExecute(CommandData cmd) {
        return true;
    }

    @Override
    public CommandResult failedToExecuteMessage(CommandData cmd) {
        return new CommandResult("boombot_twitch_integration.setkey.permissions");
    }
}
