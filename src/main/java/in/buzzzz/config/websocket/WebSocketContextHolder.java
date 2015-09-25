package in.buzzzz.config.websocket;

import org.springframework.util.Assert;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jitendra on 25/9/15.
 */
public class WebSocketContextHolder {

    private Map<String, WebSocketSession> sessionContext;

    @PostConstruct
    public void init() {
        sessionContext = new HashMap<>();
    }

    public void registerWebSocketSession(WebSocketSession session) {
        Assert.notNull(session);
        sessionContext.put(session.getId(), session);
    }

    public void removeWebSocketSession(WebSocketSession session) {
        sessionContext.remove(session.getId());
    }

    public WebSocketSession getWebSocketSession(String sessionId) {
        return sessionContext.get(sessionId);
    }
}
