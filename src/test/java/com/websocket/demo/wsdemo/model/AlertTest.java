package com.websocket.demo.wsdemo.model;

import org.junit.Test;
import org.knowm.xchange.currency.Currency;

import static org.junit.Assert.assertTrue;

public class AlertTest {

    @Test
    public void constructorShouldConvertStringCurrencyPair(){
        Alert btcUsdAlert = new Alert("BTC_USD", 3000);
        assertTrue(btcUsdAlert.getPair().contains(Currency.BTC));
        assertTrue(btcUsdAlert.getPair().contains(Currency.USD));
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorShouldThrowIllegalArgumentExceptionForWrongPairFormat(){
        new Alert("BTC-USD", 3000);
    }
}