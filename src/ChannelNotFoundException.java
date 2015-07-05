/**
 * ChannelNotFoundException used in ChannelHandler.
 *
 * Created by aneesh on 7/4/15.
 */
public class ChannelNotFoundException extends Exception {
    private static final String MESSAGE = "Channel not found!";

    // Constructors
    public ChannelNotFoundException() {
        super(MESSAGE);
    }

    public ChannelNotFoundException(String message) {
        super(message);
    }
}
