package com.websocket.demo.wsdemo.service;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;

import java.io.IOException;

public interface TickerProvider {

    Ticker getTicker(CurrencyPair currencyPair) throws IOException;

}
