package com.websocket.demo.wsdemo.service;

import org.junit.Test;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MarketDataServiceProviderTest {

    @Test
    public void initShouldCreateMarketDataServiceProviderInstance() throws Exception {
        Method init = MarketDataServiceProvider.class.getMethod("init");
        PostConstruct postConstruct = init.getAnnotation(PostConstruct.class);
        assertNotNull(postConstruct);
        MarketDataServiceProvider provider = new MarketDataServiceProvider();
        provider.init();
        assertTrue(provider.getMarketDataService() != null);
    }

}