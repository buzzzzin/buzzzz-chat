package in.buzzzz.enums;

/**
 * @author jitendra on 26/9/15.
 */
public enum DestinationEnum {
    CHANNEL("/buzz/channel"),
    CHAT("/buzz/chat");

    private String displayName;

    DestinationEnum(String displayName) {
        this.displayName = displayName;
    }

    public static DestinationEnum findDestination(String displayName) {
        DestinationEnum destinationEnum = null;
        if (displayName != null) {
            for (DestinationEnum destination : DestinationEnum.values()) {
                if (displayName.startsWith(destination.displayName)) {
                    destinationEnum = destination;
                    break;
                }
            }
        }
        return destinationEnum;
    }
}
