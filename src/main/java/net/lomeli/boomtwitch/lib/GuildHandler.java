package net.lomeli.boomtwitch.lib;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import net.lomeli.boombot.api.nbt.TagBase;
import net.lomeli.boombot.api.nbt.TagCompound;

public class GuildHandler {
    private Map<String, String> userChannelMap;
    private String guildID;

    public GuildHandler(String guildID) {
        this.guildID = guildID;
        userChannelMap = Maps.newHashMap();
    }

    public void setUserChannel(String userID, String channelName) {
        if (Strings.isNullOrEmpty(userID) || Strings.isNullOrEmpty(channelName)) return;
        userChannelMap.put(userID, channelName);
    }

    public void removeUserChannel(String userID) {
        if (Strings.isNullOrEmpty(userID)) return;
        userChannelMap.remove(userID);
    }

    public String getUserChannel(String userID) {
        return userChannelMap.get(userID);
    }

    public Set<String> getUsersRegistered() {
        return Collections.unmodifiableSet(userChannelMap.keySet());
    }

    public void readFromNBT(TagCompound tag) {
        if (tag == null || tag.getKeys().isEmpty()) return;
        tag.getKeys().stream().filter(key -> !Strings.isNullOrEmpty(key) && tag.hasTag(key, TagBase.TagType.TAG_STRING))
                .forEach(key -> userChannelMap.put(key, tag.getString(key)));
    }

    public TagCompound writeToNBT() {
        TagCompound tag = new TagCompound();
        if (!userChannelMap.isEmpty())
            userChannelMap.keySet().stream().filter(key -> !Strings.isNullOrEmpty(key))
                    .forEach(key -> tag.setString(key, userChannelMap.get(key)));
        return tag;
    }

    public String getGuildID() {
        return guildID;
    }
}
