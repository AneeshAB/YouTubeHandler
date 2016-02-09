import com.google.api.client.util.DateTime;

import java.util.Date;

/**
 * Handles video data manipulation.
 *
 * @author Aneesh Bhansali
 */
public class VideoHandler implements Comparable {
    private static final String FORMAT = "%s\t%s";

    // Instance variables
    private String title;
    private int numViews;
    private Date publishDate;

    private double popularity = -1;

    // Constructors
    VideoHandler(String title, int numViews, DateTime publishDate) {
        this.title = title;
        this.numViews = numViews;
        this.publishDate = new Date(publishDate.getValue());
    }

    // Public methods
    /**
     * Calculates the popularity of this Video according to:
     * views / timeSincePublish
     *
     * @return The popularity of the video as a double.
     */
    double popularity() {
        // Only proceed if popularity hasn't been calculated
        if (popularity == - 1) {
            // Now
            Date now = new Date();

            // Days since upload; add 1 to avoid divide by 0
            double diff = (now.getTime() - publishDate.getTime()) /
                    (1000 * 60 * 60 * 24) + 1;

            // Calculate the popularity
            popularity = (double) numViews / diff;
        }

        // Return the popularity
        return popularity;
    }

    // Comparable methods
    @Override
    public int compareTo(Object o) {
        VideoHandler other = (VideoHandler) o;
        if (popularity() > other.popularity()) {
            return -1;
        }
        else {
            return 1;
        }
    }

    // Object methods
    @Override
    public String toString() {
        return String.format(FORMAT, title, popularity());
    }
}
