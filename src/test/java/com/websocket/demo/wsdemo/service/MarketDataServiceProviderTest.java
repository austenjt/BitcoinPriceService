package com.websocket.demo.wsdemo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
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