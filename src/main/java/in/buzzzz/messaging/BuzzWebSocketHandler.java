package in.buzzzz.messaging;

import in.buzzzz.context.ChannelContextHolder;
import in.buzzzz.context.WebSocketContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.logging.Logger;

/**
 * @author jitendra on 25/9/15.
 */
public class BuzzWebSocketHandler extends TextWebSocketHandler {

    final Logger logger = Logger.getLogger(this.getClass().getName());
    private WebSocketContextHolder socketContextHolder;
    private PayloadHandler payloadHandler;

    @Autowired
    ChannelContextHolder channelContextHolder;

    public void setSocketContextHolder(WebSocketContextHolder socketContextHolder) {
        this.socketContextHolder = socketContextHolder;
    }

    public void setPayloadHandler(PayloadHandler payloadHandler) {
        this.payloadHandler = payloadHandler;
    }

    /**
     * This method will be invoked just after connection established and {@link WebSocketSession} created.
     * It'll register session into {@link WebSocketContextHolder} and channel name to {@link ChannelContextHolder}.
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Assert.notNull(session);
        logger.info("New WebSocket session register " + session.getId());
        socketContextHolder.registerWebSocketSession(session);
        channelContextHolder.registerChannelContext(session.getUri().toString(), session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.info("Message received -- "+message.getPayload() +" --");
        payloadHandler.handlePayload(session, message);
    }

    /**
     * If any {@link WebSocketSession} closed this method will be triggered. This method will clear
     * {@link WebSocketContextHolder} and {@link ChannelContextHolder}.
     * @param session
     * @param status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("Destroying WebSocket session " + session.getId());
        socketContextHolder.removeWebSocketSession(session);
        channelContextHolder.removeWebSocketSession(session.getUri().toString(), session.getId());
    }
}
