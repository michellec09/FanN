package edu.tec.azuay.faan.config.websocket;

import lombok.Getter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final ConcurrentHashMap<String, String> connectedUsers = new ConcurrentHashMap<>();

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/faan-websocket");
        registry.addEndpoint("/faan-websocket").setAllowedOrigins("*");
        registry.addEndpoint("/faan-websocket").setAllowedOrigins("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/all","/specific");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = Objects.requireNonNull(accessor.getUser()).getName();
        String sessionId = accessor.getSessionId();

        System.out.println("User connected: " + username + " with session id: " + sessionId);

        if (sessionId != null) {
            connectedUsers.put(username, sessionId);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();

        System.out.println("User disconnected with session id: " + sessionId);

        connectedUsers.entrySet().removeIf(entry -> entry.getValue().equals(sessionId));
    }
}