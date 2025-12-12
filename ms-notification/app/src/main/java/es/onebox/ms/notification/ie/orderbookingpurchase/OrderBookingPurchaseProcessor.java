package es.onebox.ms.notification.ie.orderbookingpurchase;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.message.broker.client.message.DefaultNotificationMessage;
import es.onebox.message.broker.eip.processor.DefaultProcessor;
import es.onebox.ms.notification.exception.MsNotificationErrorCode;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: grodrigues
 * Date: 18/11/2015
 */
public class OrderBookingPurchaseProcessor extends DefaultProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderBookingPurchaseProcessor.class);

    @Autowired
    private OrderBookingPurchaseService service;

    @Override
    public void execute(Exchange exchange) {
        String orderCode = (String) exchange.getIn().getBody(DefaultNotificationMessage.class).getMessage();
        try {
            service.processNotification(orderCode);
        } catch (Exception ex) {
            LOGGER.error("[IE] orderCode: {} - Error on bookingPurchase IE notification", orderCode, ex);
            throw new OneboxRestException(MsNotificationErrorCode.RABBITMQ_EXCEPTION,
                    "Error processing message " + exchange.getIn().getBody(String.class), ex);
        }
    }

}
