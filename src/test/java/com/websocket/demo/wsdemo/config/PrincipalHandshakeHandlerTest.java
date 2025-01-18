package com.websocket.demo.wsdemo.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;

import java.security.Principal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class PrincipalHandshakeHandlerTest {

    PrincipalHandshakeHandler handshakeHandler;

    @BeforeEach
    public void init(){
        handshakeHandler = new PrincipalHandshakeHandler();
    }

    @Test
    public void determineUserShouldCreatePrincipalWithUniqueName() throws Exception {
        ServerHttpRequest request = Mockito.mock(ServerHttpRequest.class);
        WebSocketHandler handler = Mockito.mock(WebSocketHandler.class);
        Map<String, Object> attributes = new HashMap<>();

        List<Principal> principals = new ArrayList<>(50);
        for (int i=0;i<50;i++){
            attributes.clear();
            principals.add(handshakeHandler.determineUser(request, handler, attributes));
            assertTrue(attributes.containsKey("__principal__"));
            assertEquals(30, principals.get(i).getName().length());
        }
        assertTrue(principals.stream().allMatch(new HashSet<>()::add));
    }

}