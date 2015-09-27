package in.buzzzz.messaging;

import org.apache.log4j.Logger;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.Callable;

import static in.buzzzz.utils.ObjectUtils.*;

/**
 * @author jitendra on 26/9/15.
 */
public class PublishMessageTask implements Callable<Void> {
    WebSocketMessage message;
    WebSocketSession session;
    Logger logger = Logger.getLogger(getClass().getName());

    public PublishMessageTask(WebSocketSession session, WebSocketMessage message) {
        this.message = message;
        this.session = session;
    }

    @Override
    public Void call() throws Exception {
        if(isNotEmptyObject(message) && isNotEmptyObject(session)) {
            try {
                session.sendMessage(message);
            } catch (Exception e) {
                logger.error(String.format("Error while publishing message [%s] to address [%s]. ERROR IS [ %s ]", message.getPayload(), session.getUri().toString(), e.getMessage()));
            }
        }
        return null;
    }
}
