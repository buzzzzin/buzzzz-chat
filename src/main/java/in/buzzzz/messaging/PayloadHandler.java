package in.buzzzz.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.buzzzz.context.ChannelContextHolder;
import in.buzzzz.context.WebSocketContextHolder;
import in.buzzzz.enums.DestinationEnum;
import in.buzzzz.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author jitendra on 26/9/15.
 */
public class PayloadHandler {
    final Logger logger = Logger.getLogger(this.getClass().getName());
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ChannelContextHolder channelContextHolder;
    @Autowired
    WebSocketContextHolder webSocketContextHolder;
    @Autowired
    ChatService chatService;

    /**
     * This method will fetch the convert the {@link WebSocketMessage} to our customize
     * {@link Payload} message. Which includes destination type, message data some other
     * important fields to identify the message type
     * @param session
     * @param message
     */
    void handlePayload(WebSocketSession session, WebSocketMessage message) {
        if (message != null) {
            logger.info("Handling payload started");
            String payloadString = (String) message.getPayload();
            logger.info("payload String is -- " + payloadString + " --");
            Payload payload = null;
            try {
                payload = objectMapper.readValue(payloadString, Payload.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            logger.info("Object created -- " + payload + " --");
            if (payload != null) {
                consumePayload(session, payload);
            }
        }
    }

    /**
     * This method will check whether the {@link Payload} contains the valid auth token.
     * If it doesn't contains any token it'll end the current {@link WebSocketSession}.
     * Otherwise it'll continue process the message.
     * @param session
     * @param payload
     */
    void consumePayload(WebSocketSession session, Payload payload) {
        if (payload.getToken() == null) {
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            DestinationEnum destination = DestinationEnum.findDestination(payload.getDestination());
            switch (destination) {
                case CHANNEL:
                    handleChannel(session, payload);
                    break;
                case CHAT:
                    handleChat(session, payload);
                    break;
            }
        }
    }

    /**
     * This method will log the message data into the Mongo and broadcast the message to the
     * given channel. Channel is will be in the {@link Payload#destination} property
     * @param session
     * @param payload
     */
    void handleChat(WebSocketSession session, Payload payload) {
        broadcastMessage(session, payload, false);
        chatService.saveMessage(payload);
    }

    void handleChannel(WebSocketSession session, Payload payload) {

    }

    /**
     * This is the core method to trigger broadcast the message payload to the channel listeners.
     * @param session
     * @param payload
     * @param excludeCurrentSession
     */
    void broadcastMessage(WebSocketSession session, Payload payload, boolean excludeCurrentSession) {
        List<String> sessionIds = channelContextHolder.getSessions(payload.getDestination());
        logger.info(" Session Ids -- " + sessionIds + " --");
        for (String sessionId : sessionIds) {
            logger.info("Sending message to -- " + sessionId + " --");
            if (excludeCurrentSession && sessionId.equals(session.getId())) {
                continue;
            }
            try {
                webSocketContextHolder.getWebSocketSession(sessionId)
                        .sendMessage(
                                new TextMessage(objectMapper.writeValueAsString(payload.getData()))
                        );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
