package in.buzzzz.context;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author jitendra on 26/9/15.
 */
public class ChannelContextHolder {

    private Map<String, List<String>> channelContext;
    final Logger logger = Logger.getLogger(this.getClass().getName());

    @PostConstruct
    public void init() {
        channelContext = new HashMap<>();
    }

    public void registerChannelContext(String uri, String sessionId) {
        logger.info("Channel Holding request for -- " + uri + " --  with session id -- " + sessionId + " --");
        if (!channelContext.containsKey(uri)) {
            channelContext.put(uri, new ArrayList<String>());
        }
        channelContext.get(uri).add(sessionId);
    }

    public void removeWebSocketSession(String uri, String sessionId) {
        logger.info("Channel Remove request for -- "+uri+" --  with session id -- "+sessionId+" --");
        if (channelContext.containsKey(uri)) {
            channelContext.get(uri).remove(sessionId);
        }
    }

    public List<String> getSessions(String channelId) {
        logger.info("Fetching sessions for channel Id -- "+channelId+" --");
        logger.info("All channelIds -- "+channelContext+" --");
        return channelContext.get(channelId);
    }
}
