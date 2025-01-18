package com.websocket.demo.wsdemo.service;

import com.websocket.demo.wsdemo.model.Alert;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.*;

@Component
public class ClientAlerts {

    private final ConcurrentHashMap<String, List<Alert>> principalAlerts = new ConcurrentHashMap<>();

    public void addAlert(String principal, Alert alert) {
        if (principalAlerts.containsKey(principal)) {
            principalAlerts.get(principal).add(alert);
        } else {
            principalAlerts.put(principal, new ArrayList<>(asList(alert)));
        }
    }

    public void removeAlert(String principal, Alert alert) {
        if (principalAlerts.containsKey(principal)) {
            principalAlerts.get(principal).remove(alert);
            if (principalAlerts.get(principal).isEmpty()){
                principalAlerts.remove(principal);
            }
        }
    }

    public void removeAlerts(String principal){
        principalAlerts.remove(principal);
    }

    public List<Alert> getAlerts(String principal) {
        return principalAlerts.containsKey(principal) ?
                principalAlerts.get(principal) :
                new ArrayList<>();
    }

}
