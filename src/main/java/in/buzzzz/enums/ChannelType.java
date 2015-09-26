package in.buzzzz.enums;

/**
 * @author jitendra on 26/9/15.
 */
public enum ChannelType {
    TOPIC("/chat/topic"),
    QUEUE("/chat/queue");

    private String displayName;

    ChannelType(String displayName) {
        this.displayName = displayName;
    }

    public static ChannelType findChannelType(String displayName) {
        ChannelType destinationEnum = null;
        if (displayName != null) {
            for (ChannelType destination : ChannelType.values()) {
                if (displayName.startsWith(destination.displayName)) {
                    destinationEnum = destination;
                    break;
                }
            }
        }
        return destinationEnum;
    }
}
