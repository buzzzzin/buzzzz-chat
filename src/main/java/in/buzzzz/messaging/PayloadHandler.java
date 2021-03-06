package in.buzzzz.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.buzzzz.context.ChannelContextHolder;
import in.buzzzz.context.WebSocketContextHolder;
import in.buzzzz.domain.Chat;
import in.buzzzz.domain.Notification;
import in.buzzzz.enums.ChannelType;
import in.buzzzz.services.AuthService;
import in.buzzzz.services.ChatService;
import in.buzzzz.services.ForkJoinTaskExecutorService;
import in.buzzzz.services.NotificationService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;

import static in.buzzzz.utils.ObjectUtils.isEmptyObject;
import static in.buzzzz.utils.ObjectUtils.isNotEmptyObject;

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
    @Autowired
    NotificationService notificationService;
    @Autowired
    AuthService authService;
    @Autowired
    ForkJoinTaskExecutorService forkJoinTaskExecutorService;
    @Value("${api.invalid-auth-token.error}")
    String invalidTokenError;

    /**
     * This method will fetch the convert the {@link WebSocketMessage} to our customize
     * {@link Payload} message. Which includes destination type, message data some other
     * important fields to identify the message type
     *
     * @param session
     * @param message
     */
    void handlePayload(WebSocketSession session, WebSocketMessage message) {
        if (isNotEmptyObject(message)) {
            logger.info("Handling payload started");
            String payloadString = (String) message.getPayload();
            logger.info(String.format("payload String is --  %s --", payloadString));
            Payload payload = null;
            try {
                payload = objectMapper.readValue(payloadString, Payload.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            logger.info(String.format("Object created --  %s --", payload));
            if (isNotEmptyObject(payload)) {
                payload.setDestination(session.getUri().toString());
                consumePayload(session, payload);
            }
        }
    }

    /**
     * This method will check whether the {@link Payload} contains the valid auth token.
     * If it doesn't contains any token it'll end the current {@link WebSocketSession}.
     * Otherwise it'll continue process the message.
     *
     * @param session
     * @param payload
     */
    void consumePayload(WebSocketSession session, Payload payload) {
        logger.info("Started consuming payload.");
        if (isEmptyObject(payload.getToken()) || !authService.isValidToken(payload.getToken())) {
            logger.error(String.format("Either token is empty or its no a valid token. -- %s --", payload.getToken()));
            try {
                logger.error("Preparing Close status with reason");
                CloseStatus closeStatus = new CloseStatus(CloseStatus.GOING_AWAY.getCode(), invalidTokenError);
                logger.error(String.format("CloseStatus prepared with code %s and reason '%s'", closeStatus.getCode(), closeStatus.getReason()));
                session.close(closeStatus);
                logger.error("Triggered close event on Session");
            } catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
            logger.error("Failed to consume payload");
        } else {
            ChannelType destination = ChannelType.findChannelType(payload.getDestination());
            switch (destination) {
                case TOPIC:
                    handleTopicChannel(session, payload);
                    break;
                case QUEUE:
                    handleQueueChannel(session, payload);
                    break;
            }
        }
    }

    /**
     * This method will log the message data into the Mongo and broadcast the message to the
     * given channel. Channel is will be in the {@link Payload#destination} property
     *
     * @param session
     * @param payload
     */
    void handleQueueChannel(WebSocketSession session, Payload payload) {
        if (isNotEmptyObject(payload.getType()) && payload.getType().equalsIgnoreCase("CHAT")) {
            Chat chat = chatService.saveMessage(payload);
            payload.setData(isNotEmptyObject(chat) ? chat : payload.getData());
        } else {
            Notification notification = notificationService.saveNotification(payload);
            payload.setData(isNotEmptyObject(notification) ? notification : payload.getData());
        }
        broadcastMessage(session, payload, true);
    }

    /**
     * This method will handle Topic based channel handling. Where multiple users will be
     * listening on same topic.
     *
     * @param session
     * @param payload
     */
    void handleTopicChannel(WebSocketSession session, Payload payload) {
        Chat chat = chatService.saveMessage(payload);
        payload.setData(isNotEmptyObject(chat) ? chat : payload.getData());
        broadcastMessage(session, payload, false);
    }

    /**
     * This is the core method to trigger broadcast the message payload to the channel listeners.
     *
     * @param session
     * @param payload
     * @param excludeCurrentSession
     */
    void broadcastMessage(WebSocketSession session, Payload payload, boolean excludeCurrentSession) {
        List<String> sessionIds = channelContextHolder.getSessions(payload.getDestination());
        clearSensitiveData(payload);
        logger.info(" Session Ids -- " + sessionIds + " --");
        forkJoinTaskExecutorService.start();
        for (String sessionId : sessionIds) {
            logger.info("Sending message to -- " + sessionId + " --");
            if (excludeCurrentSession && sessionId.equals(session.getId())) {
                continue;
            }
            try {
                PublishMessageTask publishMessageTask = new PublishMessageTask(
                        webSocketContextHolder.getWebSocketSession(sessionId),
                        new TextMessage(objectMapper.writeValueAsString(payload))
                );
                forkJoinTaskExecutorService.submit(publishMessageTask);
            } catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        }
        forkJoinTaskExecutorService.shutdown();
    }

    private void clearSensitiveData(Payload payload) {
        if(isNotEmptyObject(payload)) {
            payload.setToken("");
        }
    }
}
