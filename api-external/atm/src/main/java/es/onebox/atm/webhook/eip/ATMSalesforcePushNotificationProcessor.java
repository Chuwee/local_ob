package es.onebox.atm.webhook.eip;

import es.onebox.atm.webhook.service.AtmSalesforcePushNotificationService;
import es.onebox.message.broker.eip.processor.DefaultProcessor;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ATMSalesforcePushNotificationProcessor extends DefaultProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ATMSalesforcePushNotificationProcessor.class);

    @Autowired
    private AtmSalesforcePushNotificationService atmSalesforcePushNotificationService;

    @Override
    public void execute(Exchange exchange) {
        ATMSalesforcePushNotificationMessage body = exchange.getIn().getBody(ATMSalesforcePushNotificationMessage.class);
        String orderCode = body.getOrderCode();
        String orderType = body.getOrderType();
        String apiKey = body.getApiKey();
        Long entityId = body.getEntityId();
        Map<String, String> headers = body.getHeaders();

        LOGGER.info("[ATM SALESFORCE NOTIFICATION] [{}] Starting notification of type {}", orderCode, orderType);

        atmSalesforcePushNotificationService.pushOrderToSalesforce(orderCode, orderType, apiKey, entityId, headers);
    }
}
