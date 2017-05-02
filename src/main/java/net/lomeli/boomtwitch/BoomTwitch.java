package net.lomeli.boomtwitch;

import com.google.common.base.Strings;

import java.util.List;

import net.lomeli.boombot.api.Addon;
import net.lomeli.boombot.api.BoomAPI;
import net.lomeli.boombot.api.events.Event;
import net.lomeli.boombot.api.events.bot.GuildEvent;
import net.lomeli.boombot.api.events.bot.InitEvent;
import net.lomeli.boombot.api.events.bot.PostInitEvent;
import net.lomeli.boombot.api.events.bot.PreInitEvent;
import net.lomeli.boombot.api.events.bot.data.DataEvent;
import net.lomeli.boombot.api.events.registry.RegisterCommandEvent;
import net.lomeli.boombot.api.nbt.TagBase;
import net.lomeli.boombot.api.nbt.TagCompound;
import net.lomeli.boombot.api.util.Logger;
import net.lomeli.boomtwitch.commands.SetKeyCommand;
import net.lomeli.boomtwitch.lib.GuildHandler;
import net.lomeli.boomtwitch.twitch.UserChannelHandler;

@Addon(addonID = "boombot_twitch_integration", name = "BoomBot Twitch Integration", version = "1.0.0")
public class BoomTwitch {
    public static final String DELAY_KEY = "CheckDelayMillis";
    public static final String TWITCH_KEY = "TwitchClientID";
    public static final String INTEGRATION_KEY = "TwitchIntegration";
    public static final String GUILD_KEY = "GuildKeys";
    @Addon.Instance
    public static BoomTwitch INSTANCE;

    public static Logger log;
    public static boolean keyFlag;
    public static String twitchClientKey;
    public static long delay;
    public static final UserChannelHandler channelHandler = new UserChannelHandler();
    public static final Thread handlerThread = new Thread(channelHandler);

    @Addon.Event
    public void preInit(PreInitEvent event) {
        log = new Logger(event.getAddon().addonID());
        log.info("Pre-Init");
        BoomAPI.eventRegistry.registerEventHandler(INSTANCE);
    }

    @Addon.Event
    public void initEvent(InitEvent event) {
        log.info("Init");
    }

    @Addon.Event
    public void post(PostInitEvent event) {
        log.info("Post-Init");
        if (!event.getGuildIDs().isEmpty()) event.getGuildIDs().stream().filter(id -> !Strings.isNullOrEmpty(id))
                .forEach(id -> channelHandler.addGuildHandler(new GuildHandler(id)));
        if (keyFlag) handlerThread.start();
    }

    @Event.EventHandler
    public void commandRegisterEvent(RegisterCommandEvent event) {
        event.getCommandRegistry().addCommand(INSTANCE, new SetKeyCommand());
    }

    @Event.EventHandler
    public void dataWriteEvent(DataEvent.DataWriteEvent event) {
        TagCompound addonData = new TagCompound();
        addonData.setLong(DELAY_KEY, delay);
        addonData.setString(TWITCH_KEY, twitchClientKey);
        TagCompound guildData = new TagCompound();
        List<GuildHandler> guildList = channelHandler.getGuildList();
        if (!guildList.isEmpty()) {
            guildList.stream().filter(guild -> guild != null).forEach(guild -> {
                TagCompound tag = guild.writeToNBT();
                guildData.setTag(guild.getGuildID(), tag);
            });
        }
        addonData.setTag(GUILD_KEY, guildData);
        event.getBotData().setTag(INTEGRATION_KEY, addonData);
    }

    @Event.EventHandler
    public void dataReadEvent(DataEvent.DataReadEvent event) {
        TagCompound addonData = new TagCompound();
        if (event.getBotData().hasTag(INTEGRATION_KEY, TagBase.TagType.TAG_COMPOUND))
            addonData = event.getBotData().getTagCompound(INTEGRATION_KEY);
        twitchClientKey = addonData.hasTag(TWITCH_KEY, TagBase.TagType.TAG_STRING) ? addonData.getString(TWITCH_KEY) : "";
        delay = addonData.hasTag(DELAY_KEY, TagBase.TagType.TAG_LONG) ? addonData.getLong(DELAY_KEY) : 60000;
        keyFlag = !Strings.isNullOrEmpty(twitchClientKey);
        TagCompound guildData = addonData.getTagCompound(GUILD_KEY);
        if (guildData == null) return;
        guildData.getKeys().stream().forEach(key -> {
            GuildHandler handler = channelHandler.getGuildHandler(key);
            if (handler == null) handler = new GuildHandler(key);
            handler.readFromNBT(guildData.getTagCompound(key));
            channelHandler.addGuildHandler(handler);
        });
    }

    @Event.EventHandler
    public void joinGuildEvent(GuildEvent.JoinedGuildEvent event) {
        channelHandler.addGuildHandler(new GuildHandler(event.getNewGuildID()));
    }

    @Event.EventHandler
    public void leaveGuildEvent(GuildEvent.LeaveGuildEvent event) {
        channelHandler.removeGuildHandler(event.getLeftGuildID());
    }
}
