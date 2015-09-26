package in.buzzzz.config.websocket;

import in.buzzzz.context.ChannelContextHolder;
import in.buzzzz.context.WebSocketContextHolder;
import in.buzzzz.messaging.BuzzWebSocketHandler;
import in.buzzzz.messaging.PayloadHandler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.List;

/**
 * @author jitendra on 25/9/15.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        String urls[] = new String[]{"/chat/topic/**", "/chat/queue/**"};
        logger.info("Registering Web Socket endpoints");
        registry
                .addHandler(webSocketHandler(), urls)
                .setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler webSocketHandler() {
        BuzzWebSocketHandler buzzWebSocketHandler = new BuzzWebSocketHandler();
        buzzWebSocketHandler.setSocketContextHolder(socketContextHolder());
        buzzWebSocketHandler.setPayloadHandler(payloadHandler());
        return buzzWebSocketHandler;
    }

    @Bean
    public PayloadHandler payloadHandler() {
        return new PayloadHandler();
    }

    @Bean
    public WebSocketContextHolder socketContextHolder() {
        return new WebSocketContextHolder();
    }

    @Bean
    public ChannelContextHolder channelContextHolder() {
        ChannelContextHolder channelContextHolder = new ChannelContextHolder();
        return channelContextHolder;
    }
}
