package com.websocket.demo.wsdemo.controller;

import com.websocket.demo.wsdemo.model.Alert;
import com.websocket.demo.wsdemo.service.BitcoinChecker;
import com.websocket.demo.wsdemo.service.ClientAlerts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class BitcoinServiceController {

    private static final Logger LOG = LoggerFactory.getLogger(BitcoinServiceController.class);

    private BitcoinChecker bitcoinChecker;
    private ClientAlerts clientAlerts;

    @Autowired
    public BitcoinServiceController(BitcoinChecker bitcoinChecker, ClientAlerts clientAlerts) {
        this.bitcoinChecker = bitcoinChecker;
        this.clientAlerts = clientAlerts;
    }

    @MessageMapping("/hello")
    @SendToUser("/alerts/hello")
    public String hello(String message, Principal principal) {
        String principalName = principal.getName();
        LOG.info("/alerts called. " + message + " received from client. Principal: " + principalName);
        bitcoinChecker.setPrincipalName(principalName);
        return principalName;
    }

    @PutMapping("/alert")
    public void putAlert(@RequestParam("pair") String pair,
                         @RequestParam("limit") String limit,
                         @RequestParam("principal") String principal) {
        LOG.info("PUT /alert params: pair: " + pair +
                " limit: " + limit +
                " principal: " + principal);
        clientAlerts.addAlert(principal, new Alert(pair, Integer.parseInt(limit)));
    }

    @DeleteMapping("/alert")
    public void deleteAlert(@RequestParam("pair") String pair,
                            @RequestParam("limit") String limit,
                            @RequestParam("principal") String principal) {
        LOG.info("DELETE /alert params: pair: " + pair +
                " limit: " + limit +
                " principal: " + principal);
        clientAlerts.removeAlert(principal, new Alert(pair, Integer.parseInt(limit)));
    }
}
