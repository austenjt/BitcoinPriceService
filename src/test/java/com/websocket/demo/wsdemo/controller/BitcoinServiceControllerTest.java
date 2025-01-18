package com.websocket.demo.wsdemo.controller;

import com.websocket.demo.wsdemo.model.Alert;
import com.websocket.demo.wsdemo.service.BitcoinChecker;
import com.websocket.demo.wsdemo.service.ClientAlerts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BitcoinServiceControllerTest {

    @Mock
    BitcoinChecker bitcoinChecker;

    @Mock
    ClientAlerts clientAlerts;

    @InjectMocks
    BitcoinServiceController controller;

    @Test
    public void checkControllerAnnotations(){
        assertNotNull(BitcoinServiceController.class.getAnnotation(RestController.class));
    }

    @Test
    public void helloAnnotationsCheck() throws Exception {
        Method hello = BitcoinServiceController.class.getMethod("hello", String.class, Principal.class);
        MessageMapping messageMappingAnnotation = hello.getAnnotation(MessageMapping.class);
        assertNotNull(messageMappingAnnotation);
        assertEquals("/hello", messageMappingAnnotation.value()[0]);
        SendToUser sendToUserAnnotation = hello.getAnnotation(SendToUser.class);
        assertNotNull(sendToUserAnnotation);
        assertEquals("/alerts/hello", sendToUserAnnotation.value()[0]);
    }

    @Test
    public void helloShouldSetPrincipalNameOnBitcoinCheckerInstance(){
        Principal principal = Mockito.mock(Principal.class);
        String principalName = "principalName";

        when(principal.getName()).thenReturn(principalName);
        assertEquals(principalName, controller.hello("message", principal));

        verify(bitcoinChecker).setPrincipalName(principalName);
    }

    @Test
    public void putAlertAnnotationsCheck() throws Exception {
        Method putAlertMethod = BitcoinServiceController.class.
                getMethod("putAlert", String.class, String.class, String.class);
        PutMapping putAlertAnnotation = putAlertMethod.getAnnotation(PutMapping.class);
        assertNotNull(putAlertAnnotation);
        assertEquals("/alert", putAlertAnnotation.value()[0]);
    }

    @Test
    public void putAlertShouldAddAlertToClientAlertsInstance(){
        controller.putAlert("BTC_USD", "3000", "principalName");
        verify(clientAlerts).addAlert(anyString(), any(Alert.class));
    }

    @Test
    public void deleteAlertAnnotationsCheck() throws Exception {
        Method deleteAlertMethod = BitcoinServiceController.class.
                getMethod("deleteAlert", String.class, String.class, String.class);
        DeleteMapping deleteAlertAnnotation = deleteAlertMethod.getAnnotation(DeleteMapping.class);
        assertNotNull(deleteAlertAnnotation);
        assertEquals("/alert", deleteAlertAnnotation.value()[0]);
    }

    @Test
    public void deleteAlertShouldDeleteAlertFromClientAlertsInstance(){
        controller.deleteAlert("BTC_USD", "3000", "principalName");
        verify(clientAlerts).removeAlert(anyString(), any(Alert.class));
    }


}