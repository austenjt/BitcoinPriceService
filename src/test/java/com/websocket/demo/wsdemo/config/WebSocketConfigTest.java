package com.websocket.demo.wsdemo.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WebSocketConfigTest {

    WebSocketConfig webSocketConfig;

    @BeforeEach
    public void init(){
        webSocketConfig = new WebSocketConfig();
    }

    @Test
    public void checkClassAnnotations(){
        // WebSocketConfig should be annotated with
        // @Configuration annotation and
        // @EnableWebSocketMessageBroker
        Configuration configuration =
                WebSocketConfig.class.getAnnotation(Configuration.class);
        assertNotNull(configuration);
        EnableWebSocketMessageBroker enableWebSocketMessageBroker =
                WebSocketConfig.class.getAnnotation(EnableWebSocketMessageBroker.class);
        assertNotNull(enableWebSocketMessageBroker);

    }

    @Test
    public void configureMessageBrokerTest() throws Exception {
        // configureMessageBroker should setup app destination prefix
        // and setup simple broker
        MessageBrokerRegistry registry = Mockito.mock(MessageBrokerRegistry.class);
        webSocketConfig.configureMessageBroker(registry);
        verify(registry).setApplicationDestinationPrefixes("/app");
        verify(registry).enableSimpleBroker("/alerts");
    }

    @Test
    public void registerStompEndpointsTest() throws Exception {
        // registerStompEndpointsTest should add "/ws-endpoint"
        // websocket endpoint and configure PrincipalHandshakeHandler handshake handler
        StompEndpointRegistry registry = Mockito.mock(StompEndpointRegistry.class);
        StompWebSocketEndpointRegistration registration =
                Mockito.mock(StompWebSocketEndpointRegistration.class);
        when(registry.addEndpoint("/ws-endpoint")).thenReturn(registration);
        webSocketConfig.registerStompEndpoints(registry);

        verify(registry).addEndpoint("/ws-endpoint");
        verify(registration).setHandshakeHandler(any(PrincipalHandshakeHandler.class));
    }

}