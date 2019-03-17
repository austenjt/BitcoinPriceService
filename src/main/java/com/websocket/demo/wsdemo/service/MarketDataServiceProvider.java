package com.websocket.demo.wsdemo.service;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.bitstamp.BitstampExchange;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class MarketDataServiceProvider {
    private MarketDataService marketDataService;

    @PostConstruct
    public void init(){
        Exchange bitstamp = ExchangeFactory.INSTANCE.createExchange(BitstampExchange.class.getName());
        this.marketDataService = bitstamp.getMarketDataService();
    }

    public MarketDataService getMarketDataService() {
        return marketDataService;
    }

}
