package com.websocket.demo.wsdemo.service;

import com.websocket.demo.wsdemo.model.Alert;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service
@Scope(scopeName = "websocket", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BitcoinChecker {

    private static final Logger LOG = LoggerFactory.getLogger(BitcoinChecker.class);

    private static final int PRICE_CHECK_INTERVAL_SEC = 8000;
    private static final int INITIAL_DELAY = 2000;

    private TickerProvider tickerProvider;
    private ClientAlerts clientAlerts;
    private SimpMessagingTemplate template;

    private String principalName = "";


    @Autowired
    public BitcoinChecker(ClientAlerts clientAlerts,
                          TickerProvider tickerProvider,
                          SimpMessagingTemplate template) {
        this.tickerProvider = tickerProvider;
        this.clientAlerts = clientAlerts;
        this.template = template;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getPrincipalName() {
        return principalName;
    }


    @PostConstruct
    public void init(){
        LOG.info("New session created. BitcoinChecker instance " + this.toString() + " created.");

    }

    @PreDestroy
    public void destroy(){
        LOG.info("Session closed. BitcoinChecker instance for principal " + this.principalName + " will be destroyed.");
        clientAlerts.removeAlerts(principalName);
    }

    @Scheduled(fixedRate = PRICE_CHECK_INTERVAL_SEC, initialDelay = INITIAL_DELAY)
    public void getBitcLastPrice() {
        List<Alert> alerts = clientAlerts.getAlerts(principalName);
        alerts.forEach(alert -> {
            try {
                checkPriceAndSendNotification(alert);
            } catch (IOException e) {
                LOG.error("Error during sending price alert to the client principal: " + principalName);
            }
        });
    }

    private void checkPriceAndSendNotification(Alert alert) throws IOException {
        Ticker ticker = tickerProvider.getTicker(alert.getPair());
        BigDecimal price = ticker.getLast();
        LOG.info(ticker.getCurrencyPair() + " price: " + price);
        if (price.compareTo(BigDecimal.valueOf(alert.getLimit())) == 1) {
            LOG.info("sending " + alert.getPair().toString() + " limit: " + alert.getLimit() + " to user: " + principalName);

            template.convertAndSendToUser(principalName,
                    "/alerts",
                    alert.getPair().toString() + " limit: " + alert.getLimit() + " ts: " + getTimeStamp());
        }
    }

    LocalDateTime getTimeStamp() {
        return LocalDateTime.now();
    }
}
