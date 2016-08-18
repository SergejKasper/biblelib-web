package me.sergejkasper.bibelbibliothek.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import static org.hibernate.jpa.internal.QueryImpl.LOG;

/*@Configuration
@EnableWebSocket*/
public class SimpleWebsocketConfiguration implements WebSocketConfigurer {


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new BookWebSocketHandler(), "/websocket/addBook").setAllowedOrigins("*");
    }

    private class BookWebSocketHandler extends TextWebSocketHandler {

        @Override
        public void handleTransportError(WebSocketSession session, Throwable throwable) throws Exception {
            LOG.error("error occured at sender " + session, throwable);
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            LOG.info(String.format("Session %s closed because of %s", session.getId(), status.getReason()));

        }

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            LOG.info("Connected ... " + session.getId());

        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage jsonTextMessage) throws Exception {
            LOG.debug("message received: " + jsonTextMessage.getPayload());

        }
    }


}
