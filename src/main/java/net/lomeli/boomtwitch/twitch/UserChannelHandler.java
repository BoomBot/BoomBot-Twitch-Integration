package net.lomeli.boomtwitch.twitch;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

import net.lomeli.boomtwitch.BoomTwitch;

public class UserChannelHandler implements Runnable {
    private Map<String, List<String>> guildCache;
    private long time;
    private boolean isChecking;

    public UserChannelHandler() {
        guildCache = Maps.newHashMap();
    }

    @Override
    public void run() {
        while (true) {
            long current = System.currentTimeMillis();
            if (current < time + BoomTwitch.delay || isChecking) continue;
            isChecking = true;

        }
    }
}
