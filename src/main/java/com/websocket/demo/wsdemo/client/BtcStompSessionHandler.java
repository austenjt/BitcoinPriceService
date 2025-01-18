package com.websocket.demo.wsdemo.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;

@Slf4j
public class BtcStompSessionHandler extends StompSessionHandlerAdapter {

    private String principal;

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        // subscribe for message with principal name.
        session.subscribe("/user/alerts/hello", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders stompHeaders) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders stompHeaders, @Nullable Object o) {
                // print received message from server
                log.info("message from /user/alerts/hello: " + o.toString());
                principal = o.toString();
            }
        });
        // subscribe for price notifications
        session.subscribe("/user/alerts", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders stompHeaders) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders stompHeaders, @Nullable Object o) {
                // print received message from server
                log.info("message from /user/alerts: " + o.toString());
            }
        });

        // send hello message to initiate session scoped BitcoinChecker instance on the server
        log.info("sending hello to /app/alerts");
        session.send("/app/hello", "hello");
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

}
