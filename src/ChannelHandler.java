import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Class <code>ChannelHandler</code> performs information analysis on YouTube
 * channels.
 *
 * @author Aneesh Bhansali
 */
public class ChannelHandler {
    private static final Logger LOG = Logger.getLogger(ChannelHandler.class
            .getName());

    private static final String CHANNEL_REQUEST_PART = "contentDetails";

    // Instance variables
    private YouTube youTube;
    private String apiKey;
    private String username;
    public Channel channel;

    // Constructors
    /**
     * Allocates and Initializes a new instance of <code>ChannelHandler</code>.
     *
     * @param youTube   YouTube API object.
     * @param apiKey    API Key used for authentication.
     * @param username  username of channel.
     */
    ChannelHandler(YouTube youTube, String apiKey, String username) {
        this.youTube = youTube;
        this.apiKey = apiKey;
        this.username = username;

        // Find the channel
        try {
            this.channel = findChannel();
        } catch (ChannelNotFoundException e) {
            LOG.severe(e.getMessage());
            System.exit(ErrorCodes.EXIT_FAILURE);
        }
    }

    // Public methods

    // Helper methods
    /**
     * Finds the channel associated with the username passed in to this
     * ChannelHandler.
     *
     * @return The found channel.
     * @throws ChannelNotFoundException If the channel is not found.
     */
    public Channel findChannel() throws ChannelNotFoundException {
        // Perform the get request to get the channel
        try {
            YouTube.Channels.List request = youTube.channels().list(
                    CHANNEL_REQUEST_PART);
            request.setKey(apiKey);
            request.setForUsername(username);
            List<Channel> channels = request.execute().getItems();

            // If the channel list is not null, return the first item, which
            // should be the correct channel
            if (channels != null && channels.size() != 0) {
                return channels.get(0);
            }
        } catch (IOException e) {
            LOG.severe(e.getMessage());
            System.exit(ErrorCodes.EXIT_FAILURE);
        }

        // If a channel was not found, throw an exception
        throw new ChannelNotFoundException();
    }
}
