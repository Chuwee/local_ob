package es.onebox.ms.notification.ie.orderrelease;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.message.broker.eip.processor.DefaultProcessor;
import es.onebox.ms.notification.exception.MsNotificationErrorCode;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by joandf on 27/04/2015.
 */
public class OrderReleaseProcessor extends DefaultProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderReleaseProcessor.class);

    @Autowired
    public OrderReleaseService orderReleaseService;

    @Override
    public void execute(Exchange exchange) {
        OrderReleaseMessage orderReleaseMessage = exchange.getIn().getBody(OrderReleaseMessage.class);
        try {
            orderReleaseService.processReleaseToShoppingCart(orderReleaseMessage);

        } catch (Exception e) {
            LOGGER.error("[IE] orderCode: {} - Error on refund IE notification", orderReleaseMessage.getOrderCode(), e);
            throw new OneboxRestException(MsNotificationErrorCode.RABBITMQ_EXCEPTION,
                    "Error processing message " + exchange.getIn().getBody(String.class), e);
        }
    }

}
