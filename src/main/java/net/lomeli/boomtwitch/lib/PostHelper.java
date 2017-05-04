package net.lomeli.boomtwitch.lib;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;

import java.io.IOException;
import java.io.InputStreamReader;

import net.lomeli.boomtwitch.BoomTwitch;
import net.lomeli.boomtwitch.twitch.ApiResponse;

public class PostHelper {
    public static final Gson GSON = new Gson();
    public static final HttpClient httpClient = HttpClients.createMinimal();

    public static ApiResponse getStreamInfo(String channelName, String clientID) {
        if (Strings.isNullOrEmpty(channelName) || Strings.isNullOrEmpty(clientID)) return null;
        ApiResponse apiResponse = null;
        HttpGet httpGet = new HttpGet("https://api.twitch.tv/kraken/streams/" + channelName);
        httpGet.setHeaders(new Header[]{
                new BasicHeader("Accept", "application/vnd.twitchtv.v3+json"),
                new BasicHeader("Client-ID", clientID)
        });
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                InputStreamReader reader = new InputStreamReader(entity.getContent());
                apiResponse = GSON.fromJson(reader, ApiResponse.class);
            }
        } catch (IOException ex) {
            BoomTwitch.log.error("Could not connect to Twitch's servers!", ex);
        }
        return apiResponse;
    }
}
