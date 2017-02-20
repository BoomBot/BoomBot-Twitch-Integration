package net.lomeli.boomtwitch;

import com.google.common.base.Strings;

import net.lomeli.boombot.api.Addon;
import net.lomeli.boombot.api.BoomAPI;
import net.lomeli.boombot.api.data.EntityData;
import net.lomeli.boombot.api.events.bot.InitEvent;
import net.lomeli.boombot.api.events.bot.PreInitEvent;
import net.lomeli.boombot.api.util.Logger;
import net.lomeli.boomtwitch.lib.PostHelper;
import net.lomeli.boomtwitch.twitch.ApiResponse;

@Addon(addonID = "boombot_twitch_integration", name = "BoomBot Twitch Integration", version = "1.0.0")
public class BoomTwitch {
    @Addon.Instance
    public static BoomTwitch INSTANCE;

    public static Logger log;
    public static boolean keyFlag;
    public static String twitchClientKey;
    public static long delay;

    @Addon.Event
    public void preInit(PreInitEvent event) {
        log = new Logger(event.getAddon().addonID());
        boolean change = false;
        EntityData data = BoomAPI.dataRegistry.getBoomBotData().getData("TwitchIntegration");
        if (!data.hasKey("TwitchClientID")) {
            keyFlag = false;
            data.setString("TwitchClientID", "");
            change = true;
        } else {
            twitchClientKey = data.getString("TwitchClientID");
            keyFlag = !Strings.isNullOrEmpty(twitchClientKey);
        }

        if (!data.hasKey("TwitchClientID")) {
            change = true;
            data.setLong("CheckDelayMillis", 60000);
            delay = 60000;
        } else delay = data.getLong("CheckDelayMillis");

        if (change) BoomAPI.dataRegistry.getBoomBotData().setData("TwitchIntegration", data);
    }


    @Addon.Event
    public void init(InitEvent event) {
        if (keyFlag) {
            ApiResponse response = PostHelper.getStreamInfo("PlayHearthstone", twitchClientKey);
            if (response == null) log.error("Could not get info on test channel");
            else
                log.debug("%s is %s playing %s", "PlayHearthstone", response.getStream() == null ? "Offline" : "Online",
                        response.getStream() == null ? "nothing" : response.getStream().getGame());
        }
    }
}
