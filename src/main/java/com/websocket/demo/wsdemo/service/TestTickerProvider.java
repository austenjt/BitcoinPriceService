package com.websocket.demo.wsdemo.service;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;

@Component
@Profile("test")
public class TestTickerProvider implements TickerProvider {

    private static final BigDecimal TEST_PRICE = BigDecimal.valueOf(3000);

    @Override
    public Ticker getTicker(CurrencyPair currencyPair) throws IOException {
        return new Ticker.Builder().last(TEST_PRICE).currencyPair(currencyPair).build();
    }

}
