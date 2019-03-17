package com.websocket.demo.wsdemo.service;

import com.websocket.demo.wsdemo.model.Alert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ClientAlertsTest {
    ClientAlerts clientAlerts;

    @Before
    public void init(){
        clientAlerts = new ClientAlerts();
    }

    @Test
    public void addAlertShouldAddNewAlertForNewPrincipal(){
        clientAlerts.addAlert("principal1", new Alert("BTC_USD", 3000));
        List<Alert> principal1Alerts = clientAlerts.getAlerts("principal1");
        assertEquals(1, principal1Alerts.size());
        assertEquals(new Alert("BTC_USD", 3000), principal1Alerts.get(0));
    }

    @Test
    public void addAlertShouldAddNewAlertIfPrincipalAlreadyExist() throws Exception {
        clientAlerts.addAlert("principal1", new Alert("BTC_USD", 3000));
        clientAlerts.addAlert("principal1", new Alert("BTC_EUR", 2500));
        List<Alert> principal1Alerts = clientAlerts.getAlerts("principal1");
        assertEquals(2, principal1Alerts.size());
        assertEquals(new Alert("BTC_USD", 3000), principal1Alerts.get(0));
        assertEquals(new Alert("BTC_EUR", 2500), principal1Alerts.get(1));
    }

    @Test
    public void removeAlertShouldRemoveAlertForPrincipal() throws Exception {
        clientAlerts.addAlert("principal1", new Alert("BTC_USD", 3000));
        clientAlerts.addAlert("principal1", new Alert("BTC_EUR", 2500));
        clientAlerts.removeAlert("principal1", new Alert("BTC_USD", 3000));
        assertEquals(1, clientAlerts.getAlerts("principal1").size());
        assertTrue(clientAlerts.getAlerts("principal1").contains(new Alert("BTC_EUR", 2500)));
    }

    @Test
    public void removeAlertShouldRemovePrincipalAlertsIfLastOneRemoved() throws Exception {
        clientAlerts.addAlert("principal1", new Alert("BTC_USD", 3000));
        clientAlerts.addAlert("principal1", new Alert("BTC_EUR", 2500));
        clientAlerts.removeAlert("principal1", new Alert("BTC_USD", 3000));
        clientAlerts.removeAlert("principal1", new Alert("BTC_EUR", 2500));
        assertTrue(clientAlerts.getAlerts("principal1").isEmpty());
    }

    @Test
    public void removeAlertsShouldRemoveAllAlertsForGivenPrincipal() throws Exception {
        clientAlerts.addAlert("principal1", new Alert("BTC_USD", 3000));
        clientAlerts.addAlert("principal1", new Alert("BTC_EUR", 2500));
        clientAlerts.addAlert("principal2", new Alert("BTC_USD", 3000));
        clientAlerts.addAlert("principal2", new Alert("BTC_EUR", 2500));
        clientAlerts.removeAlerts("principal1");
        assertTrue(clientAlerts.getAlerts("principal1").isEmpty());
    }

    @Test
    public void getAlertsShouldReturnAlertsForGivenPrincipalOrEmptyList() throws Exception {
        clientAlerts.addAlert("principal1", new Alert("BTC_USD", 3000));
        clientAlerts.addAlert("principal1", new Alert("BTC_EUR", 2500));
        assertTrue(!clientAlerts.getAlerts("principal1").isEmpty());
        assertTrue(clientAlerts.getAlerts("principal2").isEmpty());
    }

}