package com.websocket.demo.wsdemo.model;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.knowm.xchange.currency.Currency;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class AlertTest {

    @Test
    public void constructorShouldConvertStringCurrencyPair(){
        Alert btcUsdAlert = new Alert("BTC_USD", 3000);
        assertTrue(btcUsdAlert.getPair().contains(Currency.BTC));
        assertTrue(btcUsdAlert.getPair().contains(Currency.USD));
    }

    @Test
    public void constructorShouldThrowIllegalArgumentExceptionForWrongPairFormat() {
        Assertions.assertThrows((IllegalArgumentException.class), () -> {
            new Alert("BTC-USD", 3000);
        });
    }

}