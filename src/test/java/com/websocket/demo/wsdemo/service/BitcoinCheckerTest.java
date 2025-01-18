package com.websocket.demo.wsdemo.service;

import com.websocket.demo.wsdemo.model.Alert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BitcoinCheckerTest {

    @Mock
    private TickerProvider tickerProvider;

    @Mock
    private ClientAlerts clientAlerts;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private final LocalDateTime dateTime = LocalDateTime.now();

    private BitcoinChecker bitcoinChecker;

    @Before
    public void init(){
        bitcoinChecker = new BitcoinChecker(clientAlerts, tickerProvider, messagingTemplate){
            @Override
            public LocalDateTime getTimeStamp() {
                return dateTime;
            }
        };
        bitcoinChecker.setPrincipalName("principalName");
    }

    @Test
    public void testClassAnnotations(){
        // class should have websocket scope
        Scope scopeAnnotation = BitcoinChecker.class.getAnnotation(Scope.class);
        assertEquals("websocket", scopeAnnotation.scopeName());
    }

    @Test
    public void getBitcoinLastPriceShouldBeScheduled() throws NoSuchMethodException {
        Method getBitcLastPriceMethod = BitcoinChecker.class.getMethod("getBitcoinLastPrice");
        Scheduled scheduledAnnotation = getBitcLastPriceMethod.getAnnotation(Scheduled.class);
        assertNotNull(scheduledAnnotation);
        assertEquals(8000, scheduledAnnotation.fixedRate());
        assertEquals(2000, scheduledAnnotation.initialDelay());
    }

    @Test
    public void getBitcoinLastPriceShouldCheckPriceForEachAlertAndSendNotification(){
        BigDecimal btcUsdLastPrice = BigDecimal.valueOf(3000);
        List<Alert> alerts = getTestAlerts();
        when(clientAlerts.getAlerts(bitcoinChecker.getPrincipalName())).thenReturn(alerts);
        alerts.forEach(alert -> {
            try {
                Ticker ticker = new Ticker.Builder().
                        currencyPair(alert.getPair()).
                        last(btcUsdLastPrice).
                        build();
                when(tickerProvider.getTicker(alert.getPair())).thenReturn(ticker);
            } catch (IOException e) {
                fail();
            }
        });
        bitcoinChecker.getBitcoinLastPrice();

        alerts.forEach( alert -> {
            if (btcUsdLastPrice.compareTo(BigDecimal.valueOf(alert.getLimit())) == 1){
                verify(messagingTemplate).convertAndSendToUser(
                        bitcoinChecker.getPrincipalName(),
                        "/alerts",
                        alert.getPair().toString() + " limit: "+ alert.getLimit() +" ts: " + dateTime);
            }
        });

        // only two notifications should be sent, as one alert limit is greater than price
        verify(messagingTemplate, times(2)).
                convertAndSendToUser(any(String.class), any(String.class), any(String.class));
    }

    @Test
    public void destroyShouldRemovePrincipalAlertsFromClientAlertsInstance(){
        bitcoinChecker.destroy();
        verify(clientAlerts).removeAlerts(bitcoinChecker.getPrincipalName());
    }

    private List<Alert> getTestAlerts() {
        List<Alert> alerts = new ArrayList<>();
        alerts.add(new Alert("BTC_USD", 2000));
        alerts.add(new Alert("BTC_EUR", 2999));
        alerts.add(new Alert("BTC_USD", 4000));
        return alerts;
    }

}