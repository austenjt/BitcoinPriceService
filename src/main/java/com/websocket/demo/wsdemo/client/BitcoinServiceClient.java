package com.websocket.demo.wsdemo.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.*;

@Slf4j
public class BitcoinServiceClient {

    public static void main(String[] args) {
        String hostnameAndPort;
        String localhost8080 = "localhost:8080";
        if (args.length == 0) {
            log.info("No hostname:port provided to the client. Using " + localhost8080);
            hostnameAndPort = localhost8080;
        } else {
            hostnameAndPort = args[0];
        }
        usage();
        String wsUrl = "ws://" + hostnameAndPort + "/ws-endpoint";
        String restUrl = "http://" + hostnameAndPort + "/alert";
        BtcStompSessionHandler sessionHandler = connectToBitcoinWebsocketServer(wsUrl);
        log.info("Ready to set/remove alerts...");
        Scanner scanner = new Scanner(System.in);
        RestTemplate restTemplate = new RestTemplate();
        List<String> alerts = new ArrayList<>();
        while (true) {
            String command = scanner.next();
            if (command.equals("exit")) {
                System.exit(0);
            }
            if (command.equals("alerts")) {
                showActiveAlerts(alerts);
                continue;
            }
            if (command.equals("usage")) {
                usage();
                continue;
            }
            try {
                handleCommand(command, sessionHandler, restTemplate, restUrl, alerts);
            } catch (IllegalArgumentException e) {
                log.error(e.getMessage(), e);
                continue;
            }

            showActiveAlerts(alerts);
        }
    }

    private static void showActiveAlerts(List<String> alerts) {
        log.info("Active alerts: " + alerts.toString());
    }

    private static void usage() {
        log.info("Usage: BitcoinServiceClient [hostname:port]");
        log.info("Setting/Removing price alerts: [PUT|DELETE]:[CCY]_[CCY]:[limit]");
        log.info("Example for alert set: PUT:BTC_USD:3000");
        log.info("Example for alert remove: DELETE:BTC_USD:3000");
        log.info("Type exit to close the client");
    }

    private static void handleCommand(String command, BtcStompSessionHandler sessionHandler,
                                      RestTemplate restTemplate, String restUrl, List<String> alerts) {
        String[] split = command.split(":");
        if (split.length != 3) {
            throw new IllegalArgumentException("Wrong alert format");
        }

        String operation = split[0];
        String currencyPair = split[1];
        String limit = split[2];
        Map<String, String> alert = new HashMap<>();
        alert.put("pair", currencyPair);
        alert.put("limit", limit);
        alert.put("principal", sessionHandler.getPrincipal());
        switch (operation) {
            case "PUT":
                alerts.add(String.join(":", currencyPair, limit));
                restTemplate.put(
                        restUrl + "?pair={pair}&limit={limit}&principal={principal}",
                        null,
                        alert);
                break;
            case "DELETE":
                alerts.remove(String.join(":", currencyPair, limit));
                restTemplate.delete(
                        restUrl + "?pair={pair}&limit={limit}&principal={principal}",
                        alert);
                break;
            default:
                throw new IllegalArgumentException("Wrong alert format");
        }
    }

    private static BtcStompSessionHandler connectToBitcoinWebsocketServer(String url) {
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
        stompClient.setMessageConverter(new StringMessageConverter());
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        taskScheduler.initialize();
        stompClient.setTaskScheduler(taskScheduler);
        BtcStompSessionHandler sessionHandler = new BtcStompSessionHandler();
        stompClient.connectAsync(url, sessionHandler);
        return sessionHandler;
    }

}
