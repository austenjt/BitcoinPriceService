package com.websocket.demo.wsdemo.service;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Profile("!test")
public class OnlineTickerProvider implements TickerProvider{

    private MarketDataServiceProvider marketDataServiceProvider;

    @Autowired
    public OnlineTickerProvider(MarketDataServiceProvider marketDataServiceProvider) {
        this.marketDataServiceProvider = marketDataServiceProvider;
    }

    @Override
    public Ticker getTicker(CurrencyPair currencyPair) throws IOException {
        return marketDataServiceProvider.getMarketDataService().getTicker(currencyPair);
    }
}
