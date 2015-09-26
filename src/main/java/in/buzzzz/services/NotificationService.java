package in.buzzzz.services;

import in.buzzzz.domain.Notification;
import in.buzzzz.messaging.Payload;
import in.buzzzz.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * @author jitendra on 26/9/15.
 */
@Service
public class NotificationService {
    @Autowired
    NotificationRepository notificationRepository;

    public Notification saveNotification(Payload payload) {
        Notification notification = bindRawObjectToChat(payload.getData());
        if (notification != null) {
            notification.setDateCreated(new Date());
            return notificationRepository.save(notification);
        }
        return null;
    }

    private Notification bindRawObjectToChat(Object rawMessageData) {
        Notification notification = new Notification();
        if (rawMessageData instanceof Map) {
            Map messageData = (Map) rawMessageData;
            notification.setNotificationText((String) messageData.get("notificationText"));
            notification.setSenderId((String) messageData.get("senderId"));
            notification.setSenderName((String) messageData.get("senderName"));
            notification.setBuzzId((String) messageData.get("buzzId"));
            notification.setBuzzName((String) messageData.get("buzzName"));
            notification.setRead(false);
        }
        return notification;
    }
}
