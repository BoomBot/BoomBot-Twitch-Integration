package net.lomeli.boomtwitch.twitch;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class UserChannelHandler implements Runnable {
    private Map<String, List<String>> guildCache;

    public UserChannelHandler() {
        guildCache = Maps.newHashMap();
    }

    @Override
    public void run() {

    }
}
