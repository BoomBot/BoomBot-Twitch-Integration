package net.lomeli.boomtwitch.twitch;

public class ApiResponse {
    private Stream stream;

    public ApiResponse(){}

    public Stream getStream() {
        return stream;
    }

    public class Channel {
        private boolean mature;
        private String broadcaster_language, url, game;
        public Channel(){}

        public boolean isMature() {
            return mature;
        }

        public String getLanguage() {
            return broadcaster_language;
        }

        public String getStreamURL() {
            return url;
        }

        public String getGame() {
            return game;
        }
    }

    public class Stream {
        private String game;
        private boolean is_playlist;
        private Channel channel;

        public Stream(){}

        public Channel getChannel() {
            return channel;
        }

        public String getGame() {
            return game;
        }

        public boolean isPlaylist() {
            return is_playlist;
        }
    }
}
