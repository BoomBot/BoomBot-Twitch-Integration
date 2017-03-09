package net.lomeli.boomtwitch;

import com.google.common.base.Strings;

import net.lomeli.boombot.api.Addon;
import net.lomeli.boombot.api.BoomAPI;
import net.lomeli.boombot.api.events.Event;
import net.lomeli.boombot.api.events.bot.GuildEvent;
import net.lomeli.boombot.api.events.bot.InitEvent;
import net.lomeli.boombot.api.events.bot.PostInitEvent;
import net.lomeli.boombot.api.events.bot.PreInitEvent;
import net.lomeli.boombot.api.events.bot.data.DataEvent;
import net.lomeli.boombot.api.nbt.NBTTagBase;
import net.lomeli.boombot.api.nbt.NBTTagCompound;
import net.lomeli.boombot.api.util.Logger;
import net.lomeli.boomtwitch.lib.PostHelper;
import net.lomeli.boomtwitch.twitch.ApiResponse;

@Addon(addonID = "boombot_twitch_integration", name = "BoomBot Twitch Integration", version = "1.0.0")
public class BoomTwitch {
    public static final String DELAY_KEY = "CheckDelayMillis";
    public static final String TWITCH_KEY = "TwitchClientID";
    public static final String INTEGRATION_KEY = "TwitchIntegration";
    @Addon.Instance
    public static BoomTwitch INSTANCE;

    public static Logger log;
    public static boolean keyFlag;
    public static String twitchClientKey;
    public static long delay;

    @Addon.Event
    public void preInit(PreInitEvent event) {
        log = new Logger(event.getAddon().addonID());
        BoomAPI.eventRegistry.registerEventHandler(INSTANCE);
    }

    @Addon.Event
    public void initEvent(InitEvent event) {
    }

    @Addon.Event
    public void post(PostInitEvent event) {
        if (keyFlag) {
            ApiResponse response = PostHelper.getStreamInfo("PlayHearthstone", twitchClientKey);
            if (response == null) log.error("Could not get info on test channel");
            else
                log.debug("%s is %s playing %s", "PlayHearthstone", response.getStream() == null ? "Offline" : "Online",
                        response.getStream() == null ? "nothing" : response.getStream().getGame());
        }
    }

    @Event.EventHandler
    public void dataWriteEvent(DataEvent.DataWriteEvent event) {
        NBTTagCompound addonData = new NBTTagCompound();
        addonData.setLong(DELAY_KEY, delay);
        addonData.setString(TWITCH_KEY, twitchClientKey);
        event.getBotData().setTag(INTEGRATION_KEY, addonData);
    }

    @Event.EventHandler
    public void dataReadEvent(DataEvent.DataReadEvent event) {
        NBTTagCompound addonData = new NBTTagCompound();
        if (event.getBotData().hasTag(INTEGRATION_KEY, NBTTagBase.TagType.TAG_COMPOUND)) addonData = event.getBotData().getTagCompound(INTEGRATION_KEY);
        twitchClientKey = addonData.hasTag(TWITCH_KEY, NBTTagBase.TagType.TAG_STRING) ? addonData.getString(TWITCH_KEY) : "";
        delay = addonData.hasTag(DELAY_KEY, NBTTagBase.TagType.TAG_LONG) ? addonData.getLong(DELAY_KEY) : 60000;
        keyFlag = !Strings.isNullOrEmpty(twitchClientKey);
        log.debug(twitchClientKey);
    }

    @Event.EventHandler
    public void joinGuildEvent(GuildEvent.JoinedGuildEvent event) {

    }

    @Event.EventHandler
    public void leaveGuildEvent(GuildEvent.LeaveGuildEvent event) {

    }
}
