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
    private static String CHANNEL_USERNAME = "PowerfulJRE";

    /**
     * Entry point to program.
     */
    public static void main(String[] args) {
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
                CHANNEL_USERNAME);

        // Print the uploads in order of popularity
        List<VideoHandler> videos = channelHandler.listUploads();
        for (VideoHandler video : videos) {
            System.out.println(video);
        }
    }
}
