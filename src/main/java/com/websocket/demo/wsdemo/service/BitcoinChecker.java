package com.websocket.demo.wsdemo.service;

import com.websocket.demo.wsdemo.model.Alert;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Scope(scopeName = "websocket", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BitcoinChecker {

    private static final int PRICE_CHECK_INTERVAL_SEC = 8000;
    private static final int INITIAL_DELAY = 2000;

    private final TickerProvider tickerProvider;
    private final ClientAlerts clientAlerts;
    private final SimpMessagingTemplate template;

    @Getter
    @Setter
    private String principalName = "";

    @Autowired
    public BitcoinChecker(ClientAlerts clientAlerts,
                          TickerProvider tickerProvider,
                          SimpMessagingTemplate template) {
        this.tickerProvider = tickerProvider;
        this.clientAlerts = clientAlerts;
        this.template = template;
    }

    @PostConstruct
    public void init() {
        log.info("New session created. BitcoinChecker instance " + this.toString() + " created.");
    }

    @PreDestroy
    public void destroy() {
        log.info("Session closed. BitcoinChecker instance for principal " + this.principalName + " will be destroyed.");
        clientAlerts.removeAlerts(principalName);
    }

    @Scheduled(fixedRate = PRICE_CHECK_INTERVAL_SEC, initialDelay = INITIAL_DELAY)
    public void getBitcoinLastPrice() {
        List<Alert> alerts = clientAlerts.getAlerts(principalName);
        alerts.forEach(alert -> {
            try {
                checkPriceAndSendNotification(alert);
            } catch (IOException e) {
                log.error("Error during sending price alert to the client principal: " + principalName, e);
            }
        });
    }

    private void checkPriceAndSendNotification(Alert alert) throws IOException {
        Ticker ticker = tickerProvider.getTicker(alert.getPair());
        BigDecimal price = ticker.getLast();
        log.info(ticker.getCurrencyPair() + " price: " + price);
        if (price.compareTo(BigDecimal.valueOf(alert.getLimit())) == 1) {
            log.info("sending " + alert.getPair().toString() + " limit: " + alert.getLimit() + " to user: " + principalName);
            template.convertAndSendToUser(principalName,
                    "/alerts",
                    alert.getPair().toString() + " limit: " + alert.getLimit() + " ts: " + getTimeStamp());
        }
    }

    public LocalDateTime getTimeStamp() {
        return LocalDateTime.now();
    }

}
