package in.buzzzz.services;

import in.buzzzz.domain.Chat;
import in.buzzzz.messaging.Payload;
import in.buzzzz.repositories.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * @author jitendra on 26/9/15.
 */
@Service
public class ChatService {
    @Autowired
    ChatRepository chatRepository;

    public void saveMessage(Payload payload) {
        Chat chat = bindRawObjectToChat(payload.getData());
        if (chat != null) {
            chat.setDestination(payload.getDestination());
            chat.setDateCreated(new Date());
            chatRepository.save(chat);
        }
    }

    private Chat bindRawObjectToChat(Object rawMessageData) {
        Chat chat = new Chat();
        if (rawMessageData instanceof Map) {
            Map messageData = (Map) rawMessageData;
            chat.setImageUrl((String) messageData.get("imageUrl"));
            chat.setMessage((String) messageData.get("message"));
            chat.setSenderId((String) messageData.get("senderId"));
            chat.setSenderName((String) messageData.get("senderName"));
        }
        return chat;
    }
}
