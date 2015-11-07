import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
    private static final String PLAYLIST_REQUEST_ITEMS = "id,contentDetails," +
            "snippet";

    // Instance variables
    private YouTube youTube;
    private String apiKey;
    private String username;
    private Channel channel;

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
            try {
                this.channel = findChannel(true);
            }
            catch (ChannelNotFoundException ce) {
                LOG.severe(ce.getMessage());
                System.exit(ErrorCodes.EXIT_FAILURE);
            }
        }
    }

    // Helper methods
    /**
     * Finds the channel associated with the username passed in to this
     * ChannelHandler.
     *
     * @return The found channel.
     * @throws ChannelNotFoundException If the channel is not found.
     */
    private Channel findChannel() throws ChannelNotFoundException {
        return findChannel(false);
    }

    private Channel findChannel(boolean ID) throws ChannelNotFoundException {
        // Perform the get request to get the channel
        try {
            YouTube.Channels.List request = youTube.channels().list(
                    CHANNEL_REQUEST_PART);
            request.setKey(apiKey);
            if (ID) {
                request.setId(username);
            }
            else {
                request.setForUsername(username);
            }
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

    /**
     * Returns an <code>ArrayList</code>of <code>VideoHandler</code> of uploads
     * of this channel, sorted by popularity.
     *
     * @return an <code>ArrayList</code> of <code>VideoHandler</code> of uploads
     * of this channel, sorted by popularity.
     */
    ArrayList<VideoHandler> listUploads() {
        // ID of playlist of all the channel's uploads
        String uploadPlaylistID = channel.getContentDetails()
                .getRelatedPlaylists().getUploads();

        // ArrayList to return
        ArrayList<VideoHandler> videos = new ArrayList<VideoHandler>();

        // Retrieve playlist of uploads
        try {
            YouTube.PlaylistItems.List request = youTube.playlistItems().list(
                    PLAYLIST_REQUEST_ITEMS);
            request.setKey(apiKey);
            request.setPlaylistId(uploadPlaylistID);
            request.setFields("items(contentDetails/videoId),nextPageToken");

            // Keep retrieving until nothing is left
            String nextToken = "";
            do {
                request.setPageToken(nextToken);
                PlaylistItemListResponse result = request.execute();

                // Construct a VideoHandler for each result, and add it to
                // the return value
                List<PlaylistItem> results = result.getItems();
                for (PlaylistItem res : results) {
                    // Get a Video item
                    YouTube.Videos.List videoRequest = youTube.videos().list(
                            "snippet,statistics");
                    videoRequest.setKey(apiKey);
                    videoRequest.setId(res.getContentDetails().getVideoId());
                    videoRequest.setFields("items(snippet/title," +
                            "snippet/publishedAt,statistics/viewCount)");
                    VideoListResponse videoResult = videoRequest.execute();
                    Video video = videoResult.getItems().get(0);

                    // Add to the returned list of videos
                    videos.add(new VideoHandler(video.getSnippet().getTitle(),
                            video.getStatistics().getViewCount().intValue(),
                            video.getSnippet().getPublishedAt()));
                }

                // Update nextToken
                nextToken = result.getNextPageToken();
            } while (nextToken != null);
        } catch (IOException e) {
            LOG.severe(e.getMessage());
            System.exit(ErrorCodes.EXIT_FAILURE);
        }

        // Sort the ArrayList and return it
        Collections.sort(videos);
        return videos;
    }
}
