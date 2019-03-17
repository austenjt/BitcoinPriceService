package com.websocket.demo.wsdemo.model;

import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;

public class Alert {
    public static final String CURRENCY_SEPARATOR = "_";

    private CurrencyPair pair;
    private int limit;

    public Alert(String pair, int limit) {
        String[] currencies = pair.split(CURRENCY_SEPARATOR);
        if (currencies.length != 2){
            throw new IllegalArgumentException("Wrong Currency Pair format, ex: BTC_USD");
        }
        this.pair = new CurrencyPair(Currency.getInstance(currencies[0]),
                Currency.getInstance(currencies[1]));
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public CurrencyPair getPair() {
        return pair;
    }

    public void setPair(CurrencyPair pair) {
        this.pair = pair;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Alert alert = (Alert) o;

        if (getLimit() != alert.getLimit()) return false;
        return getPair().equals(alert.getPair());
    }

    @Override
    public int hashCode() {
        int result = getPair().hashCode();
        result = 31 * result + getLimit();
        return result;
    }
}
