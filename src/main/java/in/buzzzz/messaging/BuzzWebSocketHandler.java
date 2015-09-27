package in.buzzzz.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.buzzzz.context.ChannelContextHolder;
import in.buzzzz.context.WebSocketContextHolder;
import in.buzzzz.domain.Chat;
import in.buzzzz.enums.ChannelType;
import in.buzzzz.repositories.ChatRepository;
import in.buzzzz.services.ForkJoinTaskExecutorService;
import in.buzzzz.utils.ObjectUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author jitendra on 25/9/15.
 */
public class BuzzWebSocketHandler extends TextWebSocketHandler {

    final Logger logger = Logger.getLogger(this.getClass().getName());
    private WebSocketContextHolder socketContextHolder;
    private PayloadHandler payloadHandler;

    @Autowired
    ChannelContextHolder channelContextHolder;
    @Autowired
    ChatRepository chatRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ForkJoinTaskExecutorService forkJoinTaskExecutorService;
    @Value("${buzz.chat.constant.recent-buzz-chat}")
    int recentNChat;

    public void setSocketContextHolder(WebSocketContextHolder socketContextHolder) {
        this.socketContextHolder = socketContextHolder;
    }

    public void setPayloadHandler(PayloadHandler payloadHandler) {
        this.payloadHandler = payloadHandler;
    }

    /**
     * This method will be invoked just after connection established and {@link WebSocketSession} created.
     * It'll register session into {@link WebSocketContextHolder} and channel name to {@link ChannelContextHolder}.
     *
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Assert.notNull(session);
        logger.info("New WebSocket session register " + session.getId());
        socketContextHolder.registerWebSocketSession(session);
        channelContextHolder.registerChannelContext(session.getUri().toString(), session.getId());
        ChannelType channelType = ChannelType.findChannelType(session.getUri().toString());
        if (ObjectUtils.isNotEmptyObject(channelType) && channelType == ChannelType.TOPIC)
            publishNRecentChatMessagesToUser(session);
    }

    /**
     * This method will send recent N chat message to the connected client.
     *
     * @param session
     */
    private void publishNRecentChatMessagesToUser(WebSocketSession session) {
        logger.info(String.format("Sending recent %s chats", recentNChat));
        Sort sort = new Sort(Sort.Direction.DESC, "dateCreated");
        Pageable pageable = new PageRequest(0, recentNChat, sort);
        List<Chat> recentChats = chatRepository.findAllByChannel(session.getUri().toString(), pageable);
        if (ObjectUtils.isNotEmptyList(recentChats)) {
            Collections.sort(recentChats, new Comparator<Chat>() {
                @Override
                public int compare(Chat chat1, Chat chat2) {
                    return (int) (chat1.getDateCreated().getTime() - chat2.getDateCreated().getTime());
                }
            });
            forkJoinTaskExecutorService.start();
            for (Chat chat : recentChats) {
                Payload payload = new Payload();
                payload.setData(chat);
                payload.setDestination(session.getUri().toString());
                try {
                    forkJoinTaskExecutorService.submit(
                            new PublishMessageTask(session, new TextMessage(
                                    objectMapper.writeValueAsString(payload)
                            ))
                    );
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    e.printStackTrace();
                }
            }
            forkJoinTaskExecutorService.shutdown();
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.info("Message received -- " + message.getPayload() + " --");
        payloadHandler.handlePayload(session, message);
    }

    /**
     * If any {@link WebSocketSession} closed this method will be triggered. This method will clear
     * {@link WebSocketContextHolder} and {@link ChannelContextHolder}.
     *
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
