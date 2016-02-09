import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.youtube.YouTube;

import java.io.IOException;
import java.util.List;

/**
 * Main class which handles program execution.
 *
 * @author Aneesh Bhansali
 */
public class Main {
    private static String API_KEY = "AIzaSyCKM_JAXF1kom4kYzFZetL0OcxuPI9DhlM";
    private static String APPLICATION_NAME = "YouTubeHandler";

    /**
     * Entry point to program.
     */
    public static void main(String[] args) {
        boolean pickRandomVideo = false;    // Whether to pick a random video or
                                            // list all videos

        // If incorrect number of arguments, exit
        if (args.length != 2) {
            System.err.println("Incorrect arguments.");
            System.exit(-1);
        }

        // Determine whether to pick a random video or list all videos
        if (args[0].toLowerCase().equals("random")) {
            pickRandomVideo = true;
        }
        else if (args[0].toLowerCase().equals("list")) {
            pickRandomVideo = false;
        }
        else {
            System.exit(-1);
        }

        // Get the channel name
        String channelUsername = args[1];

        // Necessary objects for creating a new YouTube api object
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();

        // Create a new YouTube api object
        YouTube youTube = new YouTube.Builder(httpTransport, jsonFactory,
                new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest httpRequest) throws IOException {

            }
        }).setApplicationName(APPLICATION_NAME).build();

        // Create the ChannelHandler to do the heavy lifting
        ChannelHandler channelHandler = new ChannelHandler(youTube, API_KEY,
                channelUsername);

        // Get the list of uploads
        List<VideoHandler> videos = channelHandler.listUploads();

        // Pick a random (weighed based on popularity) video if necessary
        if (pickRandomVideo) {
            // Credit to http://stackoverflow.com/questions/6737283/weighted-randomness-in-java
            // Calculate total popularity
            double totalPopularity = 0; // Total popularity of all videos on
                                        // channel
            for (VideoHandler videoHandler : videos) {
                totalPopularity += videoHandler.popularity();
            }
            System.err.println("Total popularity: " + totalPopularity); // Debug

            // Choose a random video
            int index = -1;  // Index of video in list of videos
            double randomPopularity = Math.random() * totalPopularity;  // RNG
            for (int i = 0; i < videos.size(); i++) {
                randomPopularity -= videos.get(i).popularity();
                if (randomPopularity <= 0.0d) {
                    index = i;
                    break;
                }
            }

            // Print the random video chosen
            System.out.println(videos.get(index));
        }

        // Print the uploads if necessary
        else {
            for (VideoHandler videoHandler : videos) {
                System.out.println(videoHandler);
            }
        }
    }
}
