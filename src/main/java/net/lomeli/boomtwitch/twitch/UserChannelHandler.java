package net.lomeli.boomtwitch.twitch;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.lomeli.boombot.api.BoomAPI;
import net.lomeli.boombot.api.lib.BotMessage;
import net.lomeli.boombot.api.lib.I18n;
import net.lomeli.boombot.api.lib.guild.GuildProxy;
import net.lomeli.boombot.api.util.GuildUtil;
import net.lomeli.boomtwitch.BoomTwitch;
import net.lomeli.boomtwitch.lib.GuildHandler;
import net.lomeli.boomtwitch.lib.PostHelper;

public class UserChannelHandler implements Runnable {
    private List<GuildHandler> guildList;
    private Map<String, List<String>> guildCache;
    private long time;
    private boolean isChecking;

    public UserChannelHandler() {
        guildList = Lists.newArrayList();
        guildCache = Maps.newHashMap();
    }

    public void addGuildHandler(GuildHandler guildHandler) {
        if (guildHandler == null) return;
        guildList.add(guildHandler);
    }

    public GuildHandler getGuildHandler(String guildID) {
        if (Strings.isNullOrEmpty(guildID) || guildList.isEmpty()) return null;
        return guildList.stream().filter(guild -> guild != null && guild.getGuildID().equalsIgnoreCase(guildID))
                .findFirst().orElse(null);
    }

    public void removeGuildHandler(String guildID) {
        if (Strings.isNullOrEmpty(guildID) || guildList.isEmpty()) return;
        GuildHandler handler = guildList.stream().filter(guild -> guild != null && guild.getGuildID().equalsIgnoreCase(guildID))
                .findFirst().orElse(null);
        if (handler != null) guildList.remove(handler);
    }

    public List<GuildHandler> getGuildList() {
        return Collections.unmodifiableList(guildList);
    }

    @Override
    public void run() {
        while (true) {
            long current = System.currentTimeMillis();
            if (current < time + BoomTwitch.delay || isChecking) continue;
            isChecking = true;
            guildList.stream().forEach(handler -> {
                I18n lang = GuildUtil.getGuildLang(handler.getGuildID());
                GuildProxy proxy = BoomAPI.messageHandler.getGuildProxy(handler.getGuildID());
                handler.getUsersRegistered().stream().forEach(id -> {
                    ApiResponse response = PostHelper.getStreamInfo(handler.getUserChannel(id), BoomTwitch.twitchClientKey);
                    if (response != null && response.getStream() != null && !response.getStream().isPlaylist()) {
                        if (!isCached(handler.getGuildID(), id)) {
                            cacheClient(handler.getGuildID(), id);
                            ApiResponse.Stream stream = response.getStream();
                            ApiResponse.Channel info = stream.getChannel();
                            String message = lang.getLocalization("boombot_twitch_integration.announcement",
                                    id, info.getStreamURL(), info.getGame(), info.getStatus() +
                                            (info.isMature() ? lang.getLocalization("boombot_twitch_integration.announcement.mature") : ""),
                                    info.getLanguage());
                            BotMessage msg = new BotMessage(handler.getGuildID(), proxy.getPublicChannelID(), message);
                            BoomAPI.messageHandler.sendMessage(msg);
                        }
                    } else if (isCached(handler.getGuildID(), id)) removeFromCache(handler.getGuildID(), id);
                });
            });
            isChecking = false;
            time = current;
        }
    }

    public boolean isCached(String guildID, String clientID) {
        return guildCache.containsKey(guildID) ? guildCache.get(guildID).contains(clientID) : false;
    }

    public void cacheClient(String guildID, String clientID) {
        List<String> cache = Lists.newArrayList();
        if (guildCache.get(guildID) != null) cache = guildCache.get(guildID);
        cache.add(clientID);
        guildCache.put(guildID, cache);
    }

    public void removeFromCache(String guildID, String clientID) {
        if (!guildCache.containsKey(guildID)) return;
        guildCache.get(guildID).remove(clientID);
    }
}
