package es.onebox.ms.notification.ie.orderrefund;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.message.broker.client.message.DefaultNotificationMessage;
import es.onebox.message.broker.eip.processor.DefaultProcessor;
import es.onebox.ms.notification.exception.MsNotificationErrorCode;
import es.onebox.ms.notification.ie.orderbookingpurchase.OrderBookingPurchaseProcessor;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by sromeu on 8/27/15.
 */
public class OrderRefundProcessor extends DefaultProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderBookingPurchaseProcessor.class);

    @Autowired
    private OrderRefundService orderRefundService;

    @Override
    public void execute(Exchange exchange) {
        String orderCode = (String) exchange.getIn().getBody(DefaultNotificationMessage.class).getMessage();
        try {
            /* Para Fundació la Caixa utilizamos el mismo comportamiento que para las devoluciones.
             * Para el resto de clientes se deberia usar el metodo "msNotificationOperationCancelledService.processOperationCancellation"
             * ya implementado. Simplemente falta hacer la distincion de FLC y del resto de clientes.*/

            //If Fundacio La Caixa
            orderRefundService.processRefund(orderCode);

        } catch (Exception ex) {
            LOGGER.error("[IE] orderCode: {} - Error on refund IE notification", orderCode, ex);
            throw new OneboxRestException(MsNotificationErrorCode.RABBITMQ_EXCEPTION,
                    "Error processing message " + exchange.getIn().getBody(String.class), ex);
        }
    }
}
